package seekLight.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

// 1. 顶层请求实体（Chat Completions API请求体）
import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class OpenAIChatRequest {
    // 模型名称（如gpt-4、gpt-3.5-turbo）
    private String model;

    // 对话消息列表（角色+内容）
    private List<ChatMessage> messages;

    // 可选参数：温度（0-2，值越高生成越随机）
    private Double temperature = 0.7;

    // 构造器（必填参数）
    public OpenAIChatRequest(String model, List<ChatMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

}



// 4. 结果实体（单条生成结果）

