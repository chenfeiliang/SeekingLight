package seekLight.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 知乎生成工具类型枚举类
 * 对应 ZhihuGeneratorTool 中 type=1 到 type=6 的生成规则
 */
@Getter
@AllArgsConstructor
public enum ZhihuGeneratorTypeEnum {

    /**
     * type=1：资深爽文暧昧小说作家（知乎用户）
     */
    AMBIGUOUS_FICTION("1", "资深爽文暧昧小说作家"),

    /**
     * type=2：资深专业悬疑恐怖爽文小说作家（知乎用户）
     */
    SUSPENSE_HORROR_FICTION("2", "资深专业悬疑恐怖爽文小说作家"),

    /**
     * type=3：思想天马行空的悬疑小说作者
     */
    IMAGINATIVE_SUSPENSE_AUTHOR("3", "思想天马行空的悬疑小说作者，擅长构思精妙绝伦、脑洞大开、设定新颖、荒诞不羁的悬疑故事"),

    /**
     * type=4：专业情感类自媒体内容创作专家
     */
    EMOTIONAL_CONTENT_EXPERT("4", "专业情感类自媒体内容创作专家，擅长创作触动内心、引发情感共鸣的文章；"),

    /**
     * type=5：专业历史类自媒体内容创作专家
     */
    HISTORICAL_CONTENT_EXPERT("5", "专业历史类自媒体内容创作专家，擅长创作以史为鉴、引发思考、触动内心的历史文章；"),

    /**
     * type=6：资深专业高质量规则类怪谈作家（知乎用户）
     */
    RULES_BASED_HORROR("6", "资深专业高质量规则类怪谈作家，知乎资深用户");

    /**
     * 类型编码（对应 ZhihuGeneratorTool 中的 type 参数）
     */
    private final String code;

    /**
     * 类型描述（对应 type 对应的生成规则详情）
     */
    private final String desc;

    /**
     * 根据编码获取枚举实例
     * @param code 类型编码（1-6）
     * @return 对应的枚举实例，无匹配时返回 null
     */
    public static String getByCode(String code) {
        for (ZhihuGeneratorTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum.getDesc();
            }
        }
        return null;
    }
}