package seekLight.service.model;


import lombok.extern.slf4j.Slf4j;
@Slf4j

public class OllamaClient extends BaseModelChatClient {
    // 1. 常量配置（针对本地Ollama服务）
    private static final String API_URL = "http://localhost:11434/api/chat"; // Ollama默认本地API地址
    private static final String MODEL = "qwen3:8b"; // 你本地要调用的模型名称 qwen3:8b llama

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
        String question = "请用中文回答,问题: 请根据问题，写一个500字以上的故事，请保证故事有头有尾，不要中途续写，开头参考现有的经典小说开头范例,问题: 手机店内小孩哥用通缉令上证件号认证游戏，暴露出哪些问题？假冒身份证会受到什么处罚？,具体问题内容: null\n";
        String answer = new OllamaClient().chat(question);
        log.info("问题: " + question);
        log.info("回答: " + answer);
    }
}