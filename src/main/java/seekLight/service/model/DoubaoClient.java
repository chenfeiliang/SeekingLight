package seekLight.service.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import seekLight.dto.ChatMessage; // 假设 ChatMessage 类与 OllamaClient 共用

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 豆包（Doubao）API 客户端
 * <p>
 * 该类用于与豆包的 API 进行交互，发送聊天请求并获取模型的回答。
 * 结构和用法参考了项目中的 OllamaClient。
 * </p>
 */
public class DoubaoClient {

    // 1. 常量配置（针对豆包API）
    // 注意：这里的API密钥是示例，请务必替换为你自己的真实密钥！
    // 通常从环境变量或配置文件中读取，硬编码仅为示例。
    private static final String API_KEY = System.getenv("DOUBAO_API_KEY") != null ?
            System.getenv("DOUBAO_API_KEY") : "5250d239-db9f-4cfc-bc68-2f184ba7bedd";

    // 豆包API的URL
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    // 你希望调用的豆包模型名称
    private static final String MODEL = "doubao-seed-1-6-thinking-250715";

    public static void main(String[] args) {
        // 简单的测试调用
        String question = "你好，豆包，请介绍一下你自己。";
        String answer = chat(question);
        System.out.println("问题: " + question);
        System.out.println("豆包的回答: " + answer);
    }

    /**
     * 发送一个聊天请求到豆包API，并返回模型的回答。
     *
     * @param question 用户的问题
     * @return 模型生成的回答字符串，如果请求失败则返回空字符串。
     */
    public static String chat(String question) {
        try {
            // 2. 构建请求参数（消息格式）
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

            // 构建豆包API请求体
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("model", MODEL);
            requestBodyJson.put("messages", messages);
            // 豆包API通常不使用stream参数，或有不同的控制方式，这里省略

            String requestBody = requestBodyJson.toString();
            // System.out.println("请求体：\n" + JSON.toJSONString(JSON.parseObject(requestBody), true));

            // 3. 创建HttpClient并发送POST请求
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    // 豆包API通常使用Authorization头进行认证
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 4. 发送请求并获取响应
            System.out.println("\n正在请求豆包模型 " + MODEL + " ...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 5. 处理响应结果
            return handleResponse(response);

        } catch (Exception e) {
            System.err.println("豆包API请求异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 处理豆包API的响应，提取模型生成的内容。
     *
     * @param response HTTP响应对象
     * @return 提取出的回答字符串，如果解析失败则返回空字符串。
     */
    private static String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();

        // System.out.println("\n响应状态码：" + statusCode);
        // System.out.println("响应体：\n" + JSON.toJSONString(JSON.parseObject(responseBody), true));

        if (statusCode == 200) {
            // 豆包API响应体结构通常为:
            // {"id":"...", "object":"chat.completion", "created":1677649420,
            //  "model":"doubao-pro", "choices":[{"index":0, "message":{"role":"assistant", "content":"..."}, "finish_reason":"stop"}],
            //  "usage":{"prompt_tokens":9, "completion_tokens":12, "total_tokens":21}}
            JSONObject jsonObject = JSON.parseObject(responseBody);

            // 从choices数组中获取第一个元素的message
            if (jsonObject.containsKey("choices") && !jsonObject.getJSONArray("choices").isEmpty()) {
                JSONObject choiceObject = jsonObject.getJSONArray("choices").getJSONObject(0);
                JSONObject messageObject = choiceObject.getJSONObject("message");
                if (messageObject != null) {
                    String answer = messageObject.getString("content");

                    // 保留与OllamaClient相同的逻辑：提取特定标记后的内容
                    int lastSlashIndex = answer.lastIndexOf("");
                    if (lastSlashIndex != -1 && lastSlashIndex + 8 < answer.length()) {
                        answer = answer.substring(lastSlashIndex + 8);
                    }

                    return answer;
                }
            }
            System.err.println("响应中无有效message字段");
        } else if (statusCode == 401 || statusCode == 403) {
            // 401/403错误通常意味着API密钥无效或无权限
            System.err.println("请求失败，状态码：" + statusCode);
            System.err.println("最可能的原因是API密钥错误或无效。请检查你的API_KEY。");
        } else if (statusCode == 404) {
            System.err.println("请求失败，状态码：" + statusCode);
            System.err.println("API端点可能不存在或已更改。请检查API_URL。");
        } else {
            System.err.println("豆包API请求失败，状态码：" + statusCode);
        }
        return "";
    }
}