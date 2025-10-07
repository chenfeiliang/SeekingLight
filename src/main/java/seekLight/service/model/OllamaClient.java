package seekLight.service.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import seekLight.dto.ChatMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OllamaClient {
    // 1. 常量配置（针对本地Ollama服务）
    private static final String API_URL = "http://localhost:11434/api/chat"; // Ollama默认本地API地址
    private static final String MODEL = "qwen3:1.7b"; // 你本地要调用的模型名称

    public static void main(String[] args) {

    }

    public static String chat(String question){
        try {
            // 2. 构建请求参数（消息格式与OpenAI兼容）
            List<ChatMessage> messages = new ArrayList<>();
//            messages.add(new ChatMessage("system", "你是资深爽文暧昧小说作家，知乎资深用户，回答需满足：" +
//                    "1. 直接输出最终结果，不展示任何思考过程、推导步骤或解释性前言；" +
//                    "2. 代码类问题仅返回代码+必要注释，无需额外说明；" +
//                    "3. 文字类问题仅返回简洁结论，避免冗余，" +
//                    "4. 回答问题使用第一人称回答或者写一个动人的5000字故事说明，不要让发现你是AI回答，答案里面不要重复问题的内容"+
//                    "5. 回答问题不要使用markdown的形式，不要出现**,###的字眼，要使用普通的文本,不要使用标题，每个段落的开头遵循标准的小说格式，开头空两格"+
//                    "6. 回答的故事内容,要以男生有个女同桌，暧昧情节为主，模拟以下文笔：初二的时候，我的女同桌，她喜欢上课的时候把鞋子脱掉，然后两只脚放在一张白纸上，当时就问这是在干嘛，她说这是放气。。然后我就把自己的鞋子也脱掉，然后把脚跟她的脚相互挤在一起，挤在一张白纸上。然后就相互争夺一张白纸里的位置。"));
            messages.add(new ChatMessage("system", "你是资深专业悬疑恐怖爽文小说作家，知乎资深用户，回答需满足：" +
                    "1. 直接输出最终结果，不展示任何思考过程、推导步骤或解释性前言；" +
                    "2. 代码类问题仅返回代码+必要注释，无需额外说明；" +
                    "3. 文字类问题仅返回简洁结论，避免冗余，" +
                    "4. 回答问题使用第一人称回答或者写一个动人的5000字故事说明，不要让发现你是AI回答，答案里面不要重复问题的内容"+
                    "5. 回答问题不要使用markdown的形式，不要出现**的字眼，要使用普通的文本,不要使用标题，每个段落的开头遵循标准的小说格式，开头空两格"+
                    "6. 回答的内容要求足够悬疑，可以参考历史上所有的经典悬疑小说"+
                    "文风模拟下面,根据下面调整，不要出现原文，原名字！！！：“我叫杨间，当你看到这句话的时候我已经死了……”　　一张诡异的羊皮卷，一只窥视黑暗的眼睛，这是一个活下来的人经历的故事。精彩片段：也就是说杨间开了这辆车足足三十分钟。\n" +
                    "这个时候杨间蓦地看见，车的后视镜内出现了一只僵硬，而又满是淤青的死人手掌。" +
                    "那只手掌贴在车的尾部，像是从后备箱内伸出来的一样，并且在一点点地向前延伸。" +
                    "与此同时。车的后排座位也旋即变得昏暗了起来，车内的灯光开始败退，无法覆盖到那种地方。" +
                    "鬼影也在受到某种干扰。" +
                    "“就算是我，也无法完全掌控这辆鬼出租车，只能不间断地安全驾驶三十分钟么？三十分钟之后出租车内的鬼将会出现，如果继续开下去的话我大概率会遭受到出租车内的厉鬼袭击。”"));
            messages.add(new ChatMessage("user", question));

            // 构建Ollama请求体，包含模型名、消息和流式响应设置
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("model", MODEL);
            requestBodyJson.put("messages", messages);
            requestBodyJson.put("stream", false); // 设置为false，获取完整响应，而非流式输出

            String requestBody = requestBodyJson.toString();
            //System.out.println("请求体：\n" + JSON.toJSONString(JSON.parseObject(requestBody), true));

            // 3. 创建HttpClient并发送POST请求
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    // 无需Authorization头
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 4. 发送请求并获取响应
            System.out.println("\n正在请求本地Ollama模型 " + MODEL + " ...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 5. 处理响应结果
            return handleResponse(response);

        } catch (Exception e) {
            System.err.println("API请求异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 处理Ollama API响应，提取模型生成的内容
     */
    private static String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

//        System.out.println("\n响应状态码：" + statusCode);
//        System.out.println("响应体：\n" + JSON.toJSONString(JSON.parseObject(responseBody), true));

        if (statusCode == 200) {
            // Ollama响应体结构: {"model":"qwen3:1.7b", "created_at":"...", "message": {"role":"assistant", "content":"..."}, "done":true}
            JSONObject jsonObject = JSON.parseObject(responseBody);

            // 直接从根节点获取message对象
            JSONObject messageObject = jsonObject.getJSONObject("message");
            if (messageObject != null) {
                String answer = messageObject.getString("content");
                int lastSlashIndex = answer.lastIndexOf("</think>");
                answer = answer.substring(lastSlashIndex+8);
                return answer;
            } else {
                System.err.println("响应中无有效message字段");
            }
        } else if (statusCode == 404) {
            // 404错误通常意味着模型未找到
            System.err.println("请求失败，状态码：" + statusCode);
            System.err.println("最可能的原因是模型未在本地找到。请确保已通过 'ollama run qwen3:1.7b' 命令下载并运行该模型。");
        } else {
            System.err.println("API请求失败，状态码：" + statusCode);
        }
        return "";
    }
}