package seekLight.agent;
/**
 * 定义智能体可以使用的工具接口。
 */
public interface Tool {
    /**
     * 工具的唯一名称，用于在LLM的JSON响应中识别。
     * @return 工具名称
     */
    String getName();

    /**
     * 工具的描述，用于告诉LLM这个工具的功能。
     * @return 工具描述
     */
    String getDescription();

    /**
     * 执行工具并返回结果。
     * @param args 工具需要的参数
     * @return 工具执行的结果
     */
    String execute(String args);
}