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
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class DoubaoClient extends BaseModelChatClient{

    // 1. 常量配置（针对豆包API）
    // 注意：这里的API密钥是示例，请务必替换为你自己的真实密钥！
    // 通常从环境变量或配置文件中读取，硬编码仅为示例。
    private static final String API_KEY = System.getenv("DOUBAO_API_KEY") != null ?
            System.getenv("DOUBAO_API_KEY") : "5250d239-db9f-4cfc-bc68-2f184ba7bedd";

    // 豆包API的URL
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";

    // 你希望调用的豆包模型名称
    private static final String MODEL = "doubao-seed-1-6-thinking-250715";

    @Override
    public String getModel() {
        return MODEL;
    }

    @Override
    public String getType() {
        return "doubao";
    }

    public DoubaoClient() {
        super();
    }

    public DoubaoClient(String role) {
        super(role);
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
        // 简单的测试调用
        String question = "你好，豆包，请介绍一下你自己。";
        String answer = new DoubaoClient().chat(question);
        log.info("问题: " + question);
        log.info("豆包的回答: " + answer);
    }
    public  String getAnswer(String responseBody) {
        JSONObject jsonObject = JSON.parseObject(responseBody);
        // 从choices数组中获取第一个元素的message
        if (jsonObject.containsKey("choices") && !jsonObject.getJSONArray("choices").isEmpty()) {
            JSONObject choiceObject = jsonObject.getJSONArray("choices").getJSONObject(0);
            JSONObject messageObject = choiceObject.getJSONObject("message");
            if (messageObject != null) {
                return messageObject.getString("content");
            }
        }
        log.error("响应中无有效message字段");
        return "";
    }
}