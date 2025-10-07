package seekLight.service.model;


public class OllamaClient extends BaseModelChatClient {
    // 1. 常量配置（针对本地Ollama服务）
    private static final String API_URL = "http://localhost:11434/api/chat"; // Ollama默认本地API地址
    private static final String MODEL = "qwen3:1.7b"; // 你本地要调用的模型名称

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
        return "";
    }

    public static void main(String[] args) {
        // 简单的测试调用
        String question = "你好，请介绍一下你自己。";
        String answer = new OllamaClient().chat(question);
        System.out.println("问题: " + question);
        System.out.println("回答: " + answer);
    }
}