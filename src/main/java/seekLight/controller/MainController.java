package seekLight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import seekLight.credit.engine.impl.QuotaReqFlowCreditEngine;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.service.zhihu.ZhihuApiFetcher;

import java.util.UUID;

@CrossOrigin(origins = "*")
@Controller
public class MainController {
    @Autowired
    private QuotaReqFlowCreditEngine quotaReqFlowCreditEngine;
    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
// 1. 初始页URL（第一页）
        String currentPageUrl = "https://www.zhihu.com/api/v3/feed/topstory/recommend?limit=10&desktop=true";
        // 2. 循环请求下一页（示例：最多请求3页，可调整）
        int maxPage = 30;
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            System.out.println("==================== 正在请求第 " + pageNum + " 页 ====================");
            // 请求当前页并获取下一页URL
            String nextPageUrl = ZhihuApiFetcher.fetchZhihuPage(currentPageUrl);

            // 若没有下一页，终止循环
            if (nextPageUrl == null) {
                System.out.println("已到达最后一页，终止分页请求");
                break;
            }
            // 更新为下一页URL，准备下一次循环
            currentPageUrl = nextPageUrl;
        }
        return "hello";
    }
}
