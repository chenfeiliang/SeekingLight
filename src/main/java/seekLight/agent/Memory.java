package seekLight.agent;
import java.util.List;

/**
 * 定义智能体的记忆接口。
 */
public interface Memory {
    /**
     * 添加一条消息到记忆中。
     * @param role 角色，例如 "system", "user", "assistant", "tool"
     * @param content 消息内容
     */
    void addMessage(String role, String content);

    /**
     * 获取最近的N条消息。
     * @param limit 消息数量限制
     * @return 消息列表，格式为 "role: content"
     */
    List<String> getRecentMessages(int limit);

    /**
     * 清空所有记忆。
     */
    void clear();
}