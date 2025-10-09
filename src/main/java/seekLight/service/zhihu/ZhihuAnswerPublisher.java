package seekLight.service.zhihu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import seekLight.agent.Tool;

@Slf4j

public class ZhihuAnswerPublisher implements Tool {



    // 6. 知乎发布API地址（固定）
    private static final String PUBLISH_API_URL = "https://www.zhihu.com/api/v4/content/publish";

    public static void main(String[] args) {
        publish("581510789","9527001");
    }

    @Override
    public String getName() {
        return "ZhihuAnswerPublisher"; // 工具名称保持唯一性
    }

    @Override
    public String getDescription() {
        return "可以发布知乎问题的答案" +
                "参数为一个JSON字符串，包含以下字段：" +
                " - questionId (必需): 问题id。" +
                " - content (必需): 问题内容"+
                "示例: " +
                "{\"questionId\":\"为何很多人认为开5-6年车需换车？ \", \"content\":\"为了新鲜感\"}，该工具返回的结果就是发布结果，为发布成功，发布失败" ;
    }

    @Override
    public String execute(String args) {
        JSONObject jsonObject = JSON.parseObject(args);
        return publish(jsonObject.getString("questionId"),jsonObject.getString("content"));
    }

    public static String publish(String questionId, String content){
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

            // 步骤3：创建HttpClient（Java 11+自带，无需额外依赖）
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL) // 跟随重定向（避免3xx状态码）
                    .build();

            // 步骤4：构建POST请求（配置请求头、请求体）
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(PUBLISH_API_URL))
                    // -------------------------- 关键请求头（必须与浏览器一致，否则反爬拦截）--------------------------
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36") // 模拟浏览器
                    .header("Cookie", ZhihuApiFetcher.COOKIE) // 登录态
                    .header("X-Xsrftoken", ZhihuApiFetcher.X_XSRF_TOKEN) // 防CSRF攻击，与Cookie的_xsrf一致
                    .header("x-zse-93", "101_3_3.0") // 知乎API版本标识
                    .header("x-zse-96", ZhihuApiFetcher.X_ZSE_96) // 知乎API签名（核心反爬参数）
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
            log.info("\n正在发送回答发布请求...");
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 步骤6：处理响应结果
            return  handleResponse(response);

        } catch (Exception e) {
            log.error("\n发布请求异常：" + e.getMessage());
            e.printStackTrace();
            return  "发布异常";
        }
    }


    /**
     * 处理响应结果（判断发布成功/失败，输出关键信息）
     */
    private static String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();

        log.info("\n响应状态码：" + statusCode);

        // 根据状态码和响应体判断发布结果（知乎API成功返回200，且响应体含"success"或"id"字段）
        if(response.body().contains("过于频繁")){
            log.error("发布失败: "+response.body());
            return "发布失败";
        } else if (statusCode == 200 ) {
            log.info("\n✅ 回答发布成功！回答ID可从响应体的\"id\"字段获取");
            return "发布成功";
        } else if (statusCode == 401 || statusCode == 403) {
            log.error("\n❌ 发布失败（权限/反爬拦截）：状态码" + statusCode + "，可能原因：");
            log.error("1. Cookie过期（需重新从浏览器复制）");
            log.error("2. x-zse-96签名失效（需同步更新浏览器中的x-zse-96）");
            log.error("3. 请求头缺失或错误（如Referer、User-Agent不正确）");
        } else if (statusCode == 400) {
            log.error("\n❌ 发布失败（请求参数错误）：状态码" + statusCode + "，可能原因：");
            log.error("1. 问题ID不存在（TARGET_QUESTION_ID错误）");
            log.error("2. 回答内容为空或格式错误（ANSWER_HTML_CONTENT无效）");
            log.error("3. 请求JSON体字段缺失或格式错误");
        } else {
            log.error("\n❌ 发布失败（未知错误）：状态码" + statusCode);
        }
        return "发布失败";
    }
}