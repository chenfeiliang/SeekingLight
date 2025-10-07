package seekLight.dto;

import lombok.Data;

@Data
public class ChatChoice {
    // 生成的消息
    private ChatMessage message;

    // 完成原因（如stop：正常结束）
    private String finishReason;
}