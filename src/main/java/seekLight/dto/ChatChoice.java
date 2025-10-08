package seekLight.dto;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class ChatChoice {
    // 生成的消息
    private ChatMessage message;

    // 完成原因（如stop：正常结束）
    private String finishReason;
}