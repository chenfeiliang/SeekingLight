package seekLight.dto;


import lombok.Data;

// 2. 对话消息实体（单条消息：角色+内容）
@Data
public class ChatMessage {
    // 角色（system：系统提示，user：用户输入，assistant：助手回复）
    private String role;

    // 消息内容
    private String content;

    // 构造器
    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }
}