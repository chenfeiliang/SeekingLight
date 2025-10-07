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

public class DeepSeekClient extends BaseModelChatClient{

    // 1. 常量配置
    private static final String API_KEY = System.getenv("DEEPSEEK_API_KEY") != null ?
            System.getenv("DEEPSEEK_API_KEY") : "sk-2894eac9acbf42e7a0005d227f92ecfa"; // 请确保这是你的有效密钥

    // !!! 核心修改：API_URL 必须指向具体的接口路径 !!!
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    private static final String MODEL = "deepseek-chat";

    @Override
    public String getModel() {
        return MODEL;
    }

    @Override
    public String getApiUrl() {
        return API_URL;
    }

    @Override
    public String getApiKey() {
        return API_KEY;
    }
    public static void main(String[] args) {
        String question = "你好，DeepSeek，请用三句话介绍一下黑洞。";
        String answer = new DeepSeekClient().chat(question);
        System.out.println("问题: " + question);
        System.out.println("DeepSeek的回答: " + answer);
    }


    public  String getAnswer(String responseBody) {
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if (jsonObject.containsKey("choices") && !jsonObject.getJSONArray("choices").isEmpty()) {
            JSONObject choiceObject = jsonObject.getJSONArray("choices").getJSONObject(0);
            JSONObject messageObject = choiceObject.getJSONObject("message");
            if (messageObject != null) {
                return messageObject.getString("content");
            }
        }
        return "";
    }
}