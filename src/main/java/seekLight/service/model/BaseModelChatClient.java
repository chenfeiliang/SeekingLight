package seekLight.service.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import seekLight.dto.ChatMessage;
import seekLight.service.zhihu.ZhihuGeneratorTool;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
@Data
@Slf4j
public abstract class BaseModelChatClient {
    public abstract String getModel();

    public abstract String getApiUrl();

    public abstract String getApiKey();

    public  String chat(String question) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", "你是一个高级AI助手,迅速回答用户问题"));
        messages.add(new ChatMessage("user", question));
        return chat(messages);
    }

    public  String chat(String question, String ragInfo) {
        log.info("问题: "+ question);
        log.info("ragInfo: "+ ragInfo);
        if (ragInfo == null) {
            return chat(question);
        }
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", ragInfo));
        messages.add(new ChatMessage("user", question));
        String result = chat(messages);
        log.info("答案: "+ result);
        return result;
    }

    public  String chat(List<ChatMessage> messages) {
        try {
            // 构建Ollama请求体，包含模型名、消息和流式响应设置
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("model", getModel() );
            requestBodyJson.put("messages", messages);
            requestBodyJson.put("stream", false); // 设置为false，获取完整响应，而非流式输出

            String requestBody = requestBodyJson.toString();
            //log.info("请求体：\n" + JSON.toJSONString(JSON.parseObject(requestBody), true));

            // 3. 创建HttpClient并发送POST请求
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl()))
                    // 无需Authorization头
                    .header("Authorization", "Bearer " + getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 4. 发送请求并获取响应
            log.info("\n正在请求模型: " + getModel() + " ...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 5. 处理响应结果
            return handleResponse(response);

        } catch (Exception e) {
            log.error("API请求异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 处理Ollama API响应，提取模型生成的内容
     */
    public   String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode == 200) {
            String answer = getAnswer(responseBody);
            int lastSlashIndex = answer.lastIndexOf("</think>");
            if(lastSlashIndex!=-1){
                answer = answer.substring(lastSlashIndex + 8);
            }
            return answer;
        } else if (statusCode == 404) {
            // 404错误通常意味着模型未找到
            log.error("请求失败，状态码：" + statusCode);
            log.error("最可能的原因是模型未在本地找到");
        } else {
            log.error("API请求失败，状态码：" + statusCode);
        }
        return "";
    }

    public String getAnswer(String responseBody){
        JSONObject jsonObject = JSON.parseObject(responseBody);
        // 直接从根节点获取message对象
        JSONObject messageObject = jsonObject.getJSONObject("message");
        if (messageObject != null) {
            return messageObject.getString("content");
        } else {
            log.error("响应中无有效message字段");
        }
        return "";
    }
}