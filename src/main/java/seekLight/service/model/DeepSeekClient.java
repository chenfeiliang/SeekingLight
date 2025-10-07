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

public class DeepSeekClient {

    // 1. 常量配置
    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY") != null ?
            System.getenv("DEEPSEEK_API_KEY") : "sk-2894eac9acbf42e7a0005d227f92ecfa"; // 请确保这是你的有效密钥

    // !!! 核心修改：API_URL 必须指向具体的接口路径 !!!
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    private static final String MODEL = "deepseek-chat";

    public static void main(String[] args) {
        String question = "你好，DeepSeek，请用三句话介绍一下黑洞。";
        String answer = chat(question);
        System.out.println("问题: " + question);
        System.out.println("DeepSeek的回答: " + answer);
    }

    public static String chat(String question) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", "你是资深专业悬疑恐怖爽文小说作家，知乎资深用户，回答需满足：" +
                    "1. 直接输出最终结果，不展示任何思考过程、推导步骤或解释性前言；" +
                    "2. 代码类问题仅返回代码+必要注释，无需额外说明；" +
                    "3. 文字类问题仅返回简洁结论，避免冗余，" +
                    "4. 回答问题使用第一人称回答或者写一个动人的5000字故事说明，不要让发现你是AI回答，答案里面不要重复问题的内容"+
                    "5. 回答问题不要使用markdown的形式，不要出现**的字眼，要使用普通的文本,不要使用标题，每个段落的开头遵循标准的小说格式，开头空两格"+
                    "6. 回答的内容要求足够悬疑，可以参考历史上所有的经典悬疑小说"+
                    "文风模拟下面,根据下面调整，不要出现原文，原名字！！！：“我叫XXX，当你看到这句话的时候我已经死了……”　　一张诡异的羊皮卷，一只窥视黑暗的眼睛，这是一个活下来的人经历的故事。精彩片段：也就是说XXX开了这辆车足足三十分钟。" +
                    "这个时候XXX蓦地看见，车的后视镜内出现了一只僵硬，而又满是淤青的死人手掌。" +
                    "那只手掌贴在车的尾部，像是从后备箱内伸出来的一样，并且在一点点地向前延伸。" +
                    "与此同时。车的后排座位也旋即变得昏暗了起来，车内的灯光开始败退，无法覆盖到那种地方。" +
                    "鬼影也在受到某种干扰。" +
                    "“就算是我，也无法完全掌控这辆鬼出租车，只能不间断地安全驾驶三十分钟么？三十分钟之后出租车内的鬼将会出现，如果继续开下去的话我大概率会遭受到出租车内的厉鬼袭击。”"));
            messages.add(new ChatMessage("user", question));

            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("model", MODEL);
            requestBodyJson.put("messages", messages);

            String requestBody = requestBodyJson.toString();

            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL)) // 使用修正后的 URL
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            System.out.println("\n正在请求 DeepSeek 模型 " + MODEL + " ...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return handleResponse(response);

        } catch (Exception e) {
            System.err.println("DeepSeek API 请求异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    private static String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

//        System.out.println("\n响应状态码：" + statusCode);
//        System.out.println("响应体：\n" + responseBody); // 打印原始响应体，方便调试

        if (statusCode == 200) {
            JSONObject jsonObject = JSON.parseObject(responseBody);
            if (jsonObject.containsKey("choices") && !jsonObject.getJSONArray("choices").isEmpty()) {
                JSONObject choiceObject = jsonObject.getJSONArray("choices").getJSONObject(0);
                JSONObject messageObject = choiceObject.getJSONObject("message");
                if (messageObject != null) {
                    String answer = messageObject.getString("content");

                    // 这里的截取逻辑可能不再需要，因为DeepSeek不会返回此类标记
                    // 如果你的场景特殊需要，可以保留
                    int lastSlashIndex = answer.lastIndexOf("");
                    if (lastSlashIndex != -1 && lastSlashIndex + 8 < answer.length()) {
                        answer = answer.substring(lastSlashIndex + 8);
                    }

                    return answer;
                }
            }
            System.err.println("响应中无有效message字段");
        } else if (statusCode == 401 || statusCode == 403) {
            System.err.println("请求失败，状态码：" + statusCode);
            System.err.println("最可能的原因是API密钥错误、无效或已过期。请检查你的 API_KEY。");
        } else if (statusCode == 404) {
            System.err.println("请求失败，状态码：" + statusCode);
            System.err.println("API端点不存在。请再次确认 API_URL 是否正确。");
        } else {
            System.err.println("DeepSeek API 请求失败，状态码：" + statusCode);
        }
        return "";
    }
}