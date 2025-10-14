package seekLight.service.zhihu;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import seekLight.utils.SnowflakeUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;
import seekLight.entity.WorkFlow;
import seekLight.dto.FeedItem;
import seekLight.dto.Paging;
import seekLight.dto.Target;
import seekLight.dto.ZhihuResponse;
import seekLight.utils.SpringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class ZhihuApiFetcher {

    public static final String COOKIE = "_xsrf=h8TUhbDLVccOpywc5iHcIMVArnj1q9x8; _zap=c8601672-f18b-45b1-bef4-7634541f8551; d_c0=VqeTwlkOsxqPTggPmzwkkqRuzDBAEyfJcEA=|1751472847; edu_user_uuid=edu-v1|ae7fc345-00ff-4c97-a92f-2de1a77b7d43; q_c1=676ce054fa9541968f609a9da25f770e|1758499274000|1758499274000; tst=r; __zse_ck=004_vttNwJeWU6j/=jutezUnEkKJOoyeOBZ85xVRyVdDiLKqLOkR01Sm0HdzevRRZ9//0eV6fVhQSkscV=HJD8l7z6kj6XMZN9iQSP2Uz4Q8rUBKyUmmxt3CXpy27v8TsJtd-71bn8wKgT2swfC/N0G+kg3fX+heSA0O8UMmnV/CVBHTshdBbbCjr8fgiGfM3f/wTEQ0pd9LnjUNvU2OTzIHm2bGID4bBQ3bryx7EYWZZr7nFUAdSVKVFE5wwui9mDV6o; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1758499278,1759726942; HMACCOUNT=7058FF02EBD75FA5; DATE=1751472849189; crystal=U2FsdGVkX185xwCYv1z7qq+isdGkv7MSkZqUxUzmn/kN/ET5nPWyjU6vbOv/Lvb7whssxomDBppPiqOAbWp+qQKGjgqGQg704mnawCUjEOViMfW4EB+GCNJWucdJcI1R/DEyOqfLBTQjieZEWl3r4MC1cYEVq90qpnSlU2F0kM9aT7Ybix7aoekV2he0x1pFdPYPp75fVoadDQ3RhIAbYzuRcr8tNOsXxxBvLZ5g4w8fOg2kPFkS4lKilkStj2SZ; __snaker__id=TkAs7j6dQUmeD7Yh; SUBMIT_0=00bb2a9f-3624-4e77-873c-1d0888c00824; SESSIONID=okfWXfUVKOJmuGKN24zxGyxywpt3pBIbBnmpAhCEode; JOID=W1wSCk1CWa01yRLVHF9at1GTHGQIIDHFbZ55g0QGG-RWs1qTUS2dSV3LHNgdPuA3b2C96cgpSMzVx1-yaRpxZUI=; osd=U1gQB05KXa84yhrRHlJZv1WREWcAJDPIbpZ9gUkFE-BUvlmbVS-QSlXPHtUeNuQ1YmO17cokS8TRxVKxYR5zaEE=; captcha_session_v2=2|1:0|10:1759889770|18:captcha_session_v2|88:QllGVjF1TWl1R0YxZ3YrQytlMjVSZnVzc2xoajBvY0JBV1VJYVUrcDF1VDRmYXRYUnZ0bExmcmpaNzYzeHJYRw==|a0d9e75deeb65933ee9fbf07b4aac7043728c7c84b0e9030825ee427888d2b14; gdxidpyhxdE=GfVEHl4%2B7l63aeQ2jm%2BMb2b0vhRVwp%2FYQXDm5vPkK3gy9H3U%5C7g76nbU75uiqDNozWXXtUQslj8b1xMMJRCNy1NnsgikiNeTRI1rKoafpKs9JkjUlKlOzgoK%5CSHayjygvBmnCoVDVk0Pp051%2Bma%2BczJB5Tx0tbD%2FH%5CUsh%2FXlSAOZnmaj%3A1759890668708; cmci9xde=U2FsdGVkX19cXCE9PA61SOCplEQaq3tRrsw+31OQje+mZy1ZuWbXKeN5Zsi+e/lR8XTI0GLl2si/oWMSCJpv0A==; pmck9xge=U2FsdGVkX19U3ng1qinrXMuaM+blf809bGYUhpeTUVQ=; assva6=U2FsdGVkX1++bBncta4rCaHtvtSSL5RcdlOnq8r0XiM=; assva5=U2FsdGVkX1/OmwzSQZDqOOXqgcWXd9rBKFbbO849D0hhES0ANFx7OjORtWfhCkhof6btZsM4lDRPyHI4M9iCPA==; vmce9xdq=U2FsdGVkX1+aTPQlQ08LJaTl7OVLJHAp8oNjgTR/5vT+yMt91zcX4+U7PMm7L59NvnRFbsmPrUnFMDtMhCbNx7AzhX5nTkRveYl5o++wQIGiT9BARvVulPQuW9phR8AIY8mmbQeYAotkZyMGVnMK8B48qfVPLBMrOAQsKubPLYs=; BEC=6ff32b60f55255af78892ba1e551063a; z_c0=2|1:0|10:1759889785|4:z_c0|92:Mi4xUXJNVEhBQUFBQUJXcDVQQ1dRNnpHaVlBQUFCZ0FsVk5lQmZUYVFBOGVCS1c2T1pYQUFiNnFNZDhwRHJSQlJYNy1n|6f2430c241135ec48e4eb23fbad1b89dd4f501cfb3edfa0a1753507a117faf86; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1759889784";
    // 2. XSRFToken（从Cookie中提取，值与Cookie中的"_xsrf"一致，避免重复维护）
    public static final String X_XSRF_TOKEN = "h8TUhbDLVccOpywc5iHcIMVArnj1q9x8";
    public static final String X_ZSE_96 = "2.0_c4aoVUvhQokBsVQIghu3SUkSie00RoZLth7QfWj3jUSrDGtkz0JOwQivro9xH4B/";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient(); // 复用HttpClient，提升效率
    private static final Set<String> set = new HashSet<>();

    private static WorkFlowCreditEngine workFlowCreditEngine;
    static {
        try {
            workFlowCreditEngine = SpringUtils.getBean(WorkFlowCreditEngine.class);
        }catch (Exception ex){
            log.error("error==>");
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. 初始页URL（第一页）
        String currentPageUrl = "https://www.zhihu.com/api/v3/feed/topstory/recommend?limit=10&desktop=true";
        // 2. 循环请求下一页（示例：最多请求3页，可调整）
        int maxPage = 30;
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            log.info("==================== 正在请求第 " + pageNum + " 页 ====================");
            // 请求当前页并获取下一页URL
            String nextPageUrl = fetchZhihuPage(currentPageUrl);

            // 若没有下一页，终止循环
            if (nextPageUrl == null) {
                log.info("已到达最后一页，终止分页请求");
                break;
            }
            // 更新为下一页URL，准备下一次循环
            currentPageUrl = nextPageUrl;
        }
    }

    /**
     * 请求指定页的知乎推荐数据，并返回下一页URL
     * @param pageUrl 当前页API地址
     * @return 下一页URL（null表示无下一页）
     */
    public static String fetchZhihuPage(String pageUrl) {
        try {


            // 1. 构建请求（复用常量配置，URL动态传入）
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pageUrl))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36")
                    .header("Cookie", COOKIE)
                    .header("x-api-version", "3.0.53")
                    .header("x-requested-with", "fetch")
                    .header("x-zse-93", "101_3_3.0")
                    .header("x-zse-96", X_ZSE_96)
                    .GET()
                    .build();

            // 2. 发送请求
            log.info("请求URL：" + pageUrl);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. 校验响应状态
            if (response.statusCode() != 200) {
                log.error("请求失败，状态码: " + response.statusCode());
                log.error("响应体: " + response.body().substring(0, 200) + "..."); // 打印部分响应，避免过长
                return null;
            }

            // 4. 解析响应数据
            ZhihuResponse zhihuResponse = JSON.parseObject(response.body(), ZhihuResponse.class);
            handleOnePage(zhihuResponse); // 处理当前页内容（提取问题、生成回答等）

            // 5. 获取下一页URL（从paging.next中提取）
            Paging paging = zhihuResponse.getPaging();
            if (paging != null && !paging.isEnd() && paging.getNext() != null) {
                log.info("下一页URL：" + paging.getNext());
                return paging.getNext();
            } else {
                return null; // 无下一页
            }

        } catch (Exception ex) {
            log.error("分页请求异常：" + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    private static void handleOnePage(ZhihuResponse zhihuResponse){
        // 6. 处理解析后的数据
        List<FeedItem> items = zhihuResponse.getData();
        items = items.stream().filter(Objects::nonNull)
                .filter(item-> Objects.nonNull(item.getTarget())
                        &&StringUtils.equals(item.getType(),"feed")&&StringUtils.equals(item.getTarget().getType(),"answer")).collect(Collectors.toList());
        if (items != null && !items.isEmpty()) {
            log.info("成功获取到 " + items.size() + " 条推荐内容：");
            log.info("----------------------------------------");
            for (FeedItem item : items) {
                Target target = item.getTarget();
                if (target != null) {
                    String questionId = getQuestionId(target.getUrl());
                    String title = target.getTitle();
                    if(Objects.nonNull(target.getQuestion())){
                        questionId = getQuestionId(target.getQuestion().getUrl());
                    }
                    if(Objects.nonNull(target.getQuestion())){
                        title = getQuestionId(target.getQuestion().getTitle());
                    }
                    if(set.contains(questionId)){
                        continue;
                    }
                    log.info("ID: " + target.getId());
                    log.info("类型: " + target.getType());
                    log.info("标题: " + title);
                    log.info("questionId: " + questionId);
                    log.info("内容: " + target.getExcerpt());
//                    WorkFlow workFlow = new WorkFlow();
//                    workFlow.setBusiSno( SnowflakeUtils.nextId());
//                    workFlow.setStep("");
//                    workFlow.setRoute("judgeType,zhihuGenerator,zhiHuPublish");//
//                    WorkFlowContext flowContext = new WorkFlowContext(workFlow);
//                    flowContext.putParam("zhiHuGenerator_title", title);
//                   // flowContext.putParam("zhiHuGenerator_excerpt", target.getExcerpt());
//                    flowContext.putParam("zhiHuPublish_questionId",questionId);

                    WorkFlow workFlow = new WorkFlow();
                    workFlow.setBusiSno(SnowflakeUtils.nextId());
                    workFlow.setStep("");
                    workFlow.setRoute("GeneratorOutline,GeneratorDetailedOutline,GeneratorAllArticle,GeneratorAllArticleConvert");
                    WorkFlowContext flowContext = new WorkFlowContext(workFlow);
                    flowContext.putParam("Generator_title", title);
                    flowContext.putParam("Generator_questionId", questionId);
                    workFlowCreditEngine.doFlow(flowContext);
                    set.add(questionId);
                }
            }
        } else {
            log.info("未能获取到推荐内容。");
        }
    }

    public static String getQuestionId(String url){
        // 1. 找到最后一个"/"的索引
        int lastSlashIndex = url.lastIndexOf("/");

        // 2. 截取最后一个"/"后面的字符（即目标数字）
        // 注意：若lastSlashIndex为-1（无"/"），需处理避免越界，此处URL格式固定可简化
        return url.substring(lastSlashIndex + 1);

    }
}