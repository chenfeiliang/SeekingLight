package seekLight.service.zhihu;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import seekLight.credit.engine.impl.QuotaReqFlowCreditEngine;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.dto.FeedItem;
import seekLight.dto.Paging;
import seekLight.dto.Target;
import seekLight.dto.ZhihuResponse;
import seekLight.service.model.OllamaClient;
import seekLight.utils.SpringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class ZhihuApiFetcher {

    private static final String COOKIE = "_xsrf=h8TUhbDLVccOpywc5iHcIMVArnj1q9x8; _zap=c8601672-f18b-45b1-bef4-7634541f8551; d_c0=VqeTwlkOsxqPTggPmzwkkqRuzDBAEyfJcEA=|1751472847; edu_user_uuid=edu-v1|ae7fc345-00ff-4c97-a92f-2de1a77b7d43; q_c1=676ce054fa9541968f609a9da25f770e|1758499274000|1758499274000; tst=r; z_c0=2|1:0|10:1758499487|4:z_c0|92:Mi4xUXJNVEhBQUFBQUJXcDVQQ1dRNnpHaVlBQUFCZ0FsVk56ZC05YVFBVXVKUDBaN1FQVzc1MzJEelJHNWRfbFBQdm5B|bda639106bfefe05594bdc27b4a2de92a6a5736267bbcb62ffe436d1a632b6e4; __zse_ck=004_vttNwJeWU6j/=jutezUnEkKJOoyeOBZ85xVRyVdDiLKqLOkR01Sm0HdzevRRZ9//0eV6fVhQSkscV=HJD8l7z6kj6XMZN9iQSP2Uz4Q8rUBKyUmmxt3CXpy27v8TsJtd-71bn8wKgT2swfC/N0G+kg3fX+heSA0O8UMmnV/CVBHTshdBbbCjr8fgiGfM3f/wTEQ0pd9LnjUNvU2OTzIHm2bGID4bBQ3bryx7EYWZZr7nFUAdSVKVFE5wwui9mDV6o; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1758499278,1759726942; HMACCOUNT=7058FF02EBD75FA5; BEC=244e292b1eefcef20c9b81b1d9777823; SESSIONID=EVd3QmFoeox9hWsN5TLHsIQItIiIjbyy2T9HtPSMMlm; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1759752440; JOID=VlkVC0uHevUZUk3TLJZ65XkJT2Eyxy2ZIgcllEvmEIwoMh6lY3nDFn1TSdos6zUZxOtbbDNVnzfp70kjnpYCtqQ=; osd=VVkXAE-EevcSVk7TLp1-5nkLRGUxxy-SJgQllkDiE4wqORqmY3vIEn5TS9Eo6DUbz-9YbDFemzTp7UInnZYAvaA=";
    private static final String X_ZSE_96 = "2.0_NMivLybv4AQ7hMZ5=HPeJCiQUBr2GCheS9+IwK=k92TKe9Yz/3hJFWvWZTnuL9Zc";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient(); // 复用HttpClient，提升效率
    private static final Set<String> set = new HashSet<>();

    private static QuotaReqFlowCreditEngine quotaReqFlowCreditEngine = SpringUtils.getBean(QuotaReqFlowCreditEngine.class);

    public static void main(String[] args) throws Exception {
        // 1. 初始页URL（第一页）
        String currentPageUrl = "https://www.zhihu.com/api/v3/feed/topstory/recommend?limit=10&desktop=true";
        // 2. 循环请求下一页（示例：最多请求3页，可调整）
        int maxPage = 30;
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            System.out.println("==================== 正在请求第 " + pageNum + " 页 ====================");
            // 请求当前页并获取下一页URL
            String nextPageUrl = fetchZhihuPage(currentPageUrl);

            // 若没有下一页，终止循环
            if (nextPageUrl == null) {
                System.out.println("已到达最后一页，终止分页请求");
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
            System.out.println("请求URL：" + pageUrl);
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. 校验响应状态
            if (response.statusCode() != 200) {
                System.err.println("请求失败，状态码: " + response.statusCode());
                System.err.println("响应体: " + response.body().substring(0, 200) + "..."); // 打印部分响应，避免过长
                return null;
            }

            // 4. 解析响应数据
            ZhihuResponse zhihuResponse = JSON.parseObject(response.body(), ZhihuResponse.class);
            handleOnePage(zhihuResponse); // 处理当前页内容（提取问题、生成回答等）

            // 5. 获取下一页URL（从paging.next中提取）
            Paging paging = zhihuResponse.getPaging();
            if (paging != null && !paging.isEnd() && paging.getNext() != null) {
                System.out.println("下一页URL：" + paging.getNext());
                return paging.getNext();
            } else {
                return null; // 无下一页
            }

        } catch (Exception ex) {
            System.err.println("分页请求异常：" + ex.getMessage());
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
            System.out.println("成功获取到 " + items.size() + " 条推荐内容：");
            System.out.println("----------------------------------------");
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
                    System.out.println("ID: " + target.getId());
                    System.out.println("类型: " + target.getType());
                    System.out.println("标题: " + title);
                    System.out.println("questionId: " + questionId);
                    System.out.println("内容: " + target.getExcerpt());
                    QuotaReqFlow quotaReqFlow = new QuotaReqFlow();
                    quotaReqFlow.setBusiSno(UUID.randomUUID().toString());
                    quotaReqFlow.setStep("");
                    quotaReqFlow.setRoute("judgeType,zhihuGenerator,zhiHuPublish");
                    quotaReqFlow.putParam("zhihu_question",String.format("问题: %s,具体问题内容: %s", title, target.getExcerpt()));
                    quotaReqFlow.putParam("zhiHuGenerator_title", title);
                    quotaReqFlow.putParam("zhiHuGenerator_excerpt", target.getExcerpt());
                    quotaReqFlow.putParam("zhiHuPublish_questionId",questionId);
                    quotaReqFlowCreditEngine.doFlow(quotaReqFlow);
                    set.add(questionId);
                }
            }
        } else {
            System.out.println("未能获取到推荐内容。");
        }
    }

    private static String getQuestionId(String url){
        // 1. 找到最后一个"/"的索引
        int lastSlashIndex = url.lastIndexOf("/");

        // 2. 截取最后一个"/"后面的字符（即目标数字）
        // 注意：若lastSlashIndex为-1（无"/"），需处理避免越界，此处URL格式固定可简化
        return url.substring(lastSlashIndex + 1);

    }
}