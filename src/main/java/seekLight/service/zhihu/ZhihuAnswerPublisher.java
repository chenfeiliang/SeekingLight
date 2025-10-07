package seekLight.service.zhihu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ZhihuAnswerPublisher {
    // -------------------------- 常量配置（需根据实际情况替换）--------------------------
    // 1. 登录态Cookie（从浏览器F12→Network→该API请求的Headers中复制，必须有效）
    private static final String COOKIE = "_xsrf=h8TUhbDLVccOpywc5iHcIMVArnj1q9x8; _zap=c8601672-f18b-45b1-bef4-7634541f8551; d_c0=VqeTwlkOsxqPTggPmzwkkqRuzDBAEyfJcEA=|1751472847; edu_user_uuid=edu-v1|ae7fc345-00ff-4c97-a92f-2de1a77b7d43; q_c1=676ce054fa9541968f609a9da25f770e|1758499274000|1758499274000; tst=r; z_c0=2|1:0|10:1758499487|4:z_c0|92:Mi4xUXJNVEhBQUFBQUJXcDVQQ1dRNnpHaVlBQUFCZ0FsVk56ZC05YVFBVXVKUDBaN1FQVzc1MzJEelJHNWRfbFBQdm5B|bda639106bfefe05594bdc27b4a2de92a6a5736267bbcb62ffe436d1a632b6e4; __zse_ck=004_vttNwJeWU6j/=jutezUnEkKJOoyeOBZ85xVRyVdDiLKqLOkR01Sm0HdzevRRZ9//0eV6fVhQSkscV=HJD8l7z6kj6XMZN9iQSP2Uz4Q8rUBKyUmmxt3CXpy27v8TsJtd-71bn8wKgT2swfC/N0G+kg3fX+heSA0O8UMmnV/CVBHTshdBbbCjr8fgiGfM3f/wTEQ0pd9LnjUNvU2OTzIHm2bGID4bBQ3bryx7EYWZZr7nFUAdSVKVFE5wwui9mDV6o; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1758499278,1759726942; HMACCOUNT=7058FF02EBD75FA5; SESSIONID=oulSngVtDXFy1xLcRRTD2nme4H7DZSonSAvKeyuBb6k; JOID=UVkUAk0VzLwsJV0MEgrKqkV5UbgKeLnQeE44WmZUlvpVEyBCcQYmWEYiXwwV47BcS3FKYgZrw52XP3Co4xv2Ni8=; osd=UlERCksWxLkkI14EFwLMqU18Wb4JcLzYfk0wX25SlfJQGyZBeQMuXkUqWgQT4LhZQ3dJagNjxZ6fOniu4BPzPik=; BEC=6c53268835aec2199978cd4b4f988f8c; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1759771886";

    // 2. XSRFToken（从Cookie中提取，值与Cookie中的"_xsrf"一致，避免重复维护）
    private static final String X_XSRF_TOKEN = "h8TUhbDLVccOpywc5iHcIMVArnj1q9x8";

    // 3. 知乎API签名（x-zse-96，从浏览器请求Headers中复制，随Cookie变化，需同步更新）
    private static final String X_ZSE_96 = "2.0_==kt7mYeFAPg2eKjcwmSbYc5GBZ86T=7eLX0K4tCcC0ZYywVlytt4hyOW+p/pisw";

    // 4. 目标问题ID（要回答的问题ID，对应文档中的 "https://www.zhihu.com/question/1958457938550158499"）
    private static final String TARGET_QUESTION_ID = "1958457938550158499";

    // 5. 回答内容（HTML格式，可自定义，示例为"66666666666666666666"）
    private static final String ANSWER_HTML_CONTENT = "<p>66666666666666666666</p>";

    // 6. 知乎发布API地址（固定）
    private static final String PUBLISH_API_URL = "https://www.zhihu.com/api/v4/content/publish";

    public static void main(String[] args) {
        publish("581510789","9527001");
    }

    public static void publish(String questionId,String content){
        try {
            content = StringEscapeUtils.escapeJson(content);

            // 步骤2：将实体类序列化为JSON字符串（FastJSON核心方法）
            String requestBody = "{\"action\":\"answer\",\"data\":{\"publish\":{\"traceId\":\"1759763841117,caf30d02-da9c-47bb-95e1-1c046518bc73\"},\"hybridInfo\":{},\"draft\":{\"isPublished\":false,\"disabled\":1}" +
                    ",\"extra_info\":{\"question_id\":\""+questionId
                    +"\",\"publisher\":\"pc\",\"include\":\"is_contain_ai_content,is_visible,paid_info,paid_info_content,has_column,admin_closed_comment,reward_info," +
                    "annotation_action,annotation_detail,collapse_reason,is_normal,is_sticky,collapsed_by,suggest_edit," +
                    "comment_count,thanks_count,favlists_count,can_comment,content,editable_content,voteup_count," +
                    "reshipment_settings,comment_permission,created_time,updated_time,review_info,relevant_info," +
                    "question,excerpt,attachment,content_source,is_labeled,endorsements,reaction_instruction,ip_info," +
                    "relationship.is_authorized,voting,is_thanked,is_author,is_nothelp,is_favorited;author.vip_info,kvip_info," +
                    "badge[*].topics;settings.table_of_content.enabled\",\"pc_business_params\":\"{\\\"reshipment_settings\\\":\\\"disallowed\\\",\\\"comment_permission\\\":\\\"all\\\",\\\"columns\\\":null,\\\"reward_setting\\\":{\\\"can_reward\\\":false,\\\"tagline\\\":\\\"\\\"},\\\"disclaimer_status\\\":\\\"close\\\",\\\"disclaimer_type\\\":\\\"none\\\"," +
                    "\\\"commercial_report_info\\\":{\\\"is_report\\\":false},\\\"commercial_zhitask_bind_info\\\":null,\\\"is_report\\\":false," +
                    "\\\"push_activity\\\":true,\\\"table_of_contents_enabled\\\":false,\\\"thank_inviter_status\\\":\\\"close\\\"," +
                    "\\\"thank_inviter\\\":\\\"\\\"}\"},\"hybrid\":{\"html\":\"<p>"+content+"</p>\"},\"reprint\":{\"reshipment_settings\":\"disallowed\"},\"commentsPermission\":{\"comment_permission\":\"all\"},\"appreciate\":{\"can_reward\":false,\"tagline\":\"\"}," +
                    "\"publishSwitch\":{\"draft_type\":\"normal\"},\"creationStatement\":{\"disclaimer_status\":\"close\"," +
                    "\"disclaimer_type\":\"none\"},\"commercialReportInfo\":{\"isReport\":0},\"toFollower\":{\"push_activity\":true}," +
                    "\"contentsTables\":{\"table_of_contents_enabled\":false},\"thanksInvitation\":{\"thank_inviter_status\":\"close\"," +
                    "\"thank_inviter\":\"\"}}}";
           // System.out.println("请求JSON体：\n" + requestBody); // 格式化输出，便于调试

            // 步骤3：创建HttpClient（Java 11+自带，无需额外依赖）
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL) // 跟随重定向（避免3xx状态码）
                    .build();

            // 步骤4：构建POST请求（配置请求头、请求体）
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(PUBLISH_API_URL))
                    // -------------------------- 关键请求头（必须与浏览器一致，否则反爬拦截）--------------------------
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36") // 模拟浏览器
                    .header("Cookie", COOKIE) // 登录态
                    .header("X-Xsrftoken", X_XSRF_TOKEN) // 防CSRF攻击，与Cookie的_xsrf一致
                    .header("x-zse-93", "101_3_3.0") // 知乎API版本标识
                    .header("x-zse-96", X_ZSE_96) // 知乎API签名（核心反爬参数）
                    .header("x-requested-with", "fetch") // 前端请求方式标识
                    .header("Referer", "https://www.zhihu.com/question/" + questionId) // Referer验证（来源页面，必须是目标问题页）
                    .header("Origin", "https://www.zhihu.com") // 跨域源标识
                    .header("Content-Type", "application/json") // 请求体类型（JSON）
                    .header("Accept", "*/*") // 接受所有响应类型
                    .header("Accept-Encoding", "gzip, deflate, br") // 支持压缩
                    .header("Accept-Language", "zh-CN,zh;q=0.9") // 语言偏好
                    // ------------------------------------------------------------------------------------------------
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // 设置POST请求体
                    .build();

            // 步骤5：发送请求并获取响应
            System.out.println("\n正在发送回答发布请求...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 步骤6：处理响应结果
            handleResponse(response);

        } catch (Exception e) {
            System.err.println("\n发布请求异常：" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 处理响应结果（判断发布成功/失败，输出关键信息）
     */
    private static void handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();

        System.out.println("\n响应状态码：" + statusCode);

        // 根据状态码和响应体判断发布结果（知乎API成功返回200，且响应体含"success"或"id"字段）
        if(response.body().contains("过于频繁")){
            System.err.println("发布失败: "+response.body());
        } else if (statusCode == 200 ) {
            System.out.println("\n✅ 回答发布成功！回答ID可从响应体的\"id\"字段获取");
        } else if (statusCode == 401 || statusCode == 403) {
            System.err.println("\n❌ 发布失败（权限/反爬拦截）：状态码" + statusCode + "，可能原因：");
            System.err.println("1. Cookie过期（需重新从浏览器复制）");
            System.err.println("2. x-zse-96签名失效（需同步更新浏览器中的x-zse-96）");
            System.err.println("3. 请求头缺失或错误（如Referer、User-Agent不正确）");
        } else if (statusCode == 400) {
            System.err.println("\n❌ 发布失败（请求参数错误）：状态码" + statusCode + "，可能原因：");
            System.err.println("1. 问题ID不存在（TARGET_QUESTION_ID错误）");
            System.err.println("2. 回答内容为空或格式错误（ANSWER_HTML_CONTENT无效）");
            System.err.println("3. 请求JSON体字段缺失或格式错误");
        } else {
            System.err.println("\n❌ 发布失败（未知错误）：状态码" + statusCode);
        }
    }
}