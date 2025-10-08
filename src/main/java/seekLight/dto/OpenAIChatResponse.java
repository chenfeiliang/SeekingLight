package seekLight.dto;

import lombok.Data;

import java.util.List;

// 3. 顶层响应实体（API返回结果）
import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class OpenAIChatResponse {
    // 响应ID
    private String id;

    // 响应时间戳
    private Long created;

    // 模型名称
    private String model;

    // 核心结果列表（通常取第一个元素）
    private List<ChatChoice> choices;
}
