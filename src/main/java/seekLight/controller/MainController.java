package seekLight.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import seekLight.service.toutiao.ToutiaoRecommendQuestionJsoup;
import seekLight.service.zhihu.ZhihuHotListCrawler;
import seekLight.service.zhihu.ZhihuHotRankFetcher;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;
import seekLight.service.zhihu.ZhihuApiFetcher;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*")
@Controller
@Slf4j

public class MainController {
    @Autowired
    private WorkFlowCreditEngine WorkFlowCreditEngine;
    @ResponseBody
    @RequestMapping("/zhihuRecommend")
    public String hello(){
// 1. 初始页URL（第一页）
        String currentPageUrl = "https://www.zhihu.com/api/v3/feed/topstory/recommend?limit=10&desktop=true";
        // 2. 循环请求下一页（示例：最多请求3页，可调整）
        int maxPage = 30;
        for (int pageNum = 1; pageNum <= maxPage; pageNum++) {
            log.info("==================== 正在请求第 " + pageNum + " 页 ====================");
            // 请求当前页并获取下一页URL
            String nextPageUrl = ZhihuApiFetcher.fetchZhihuPage(currentPageUrl);

            // 若没有下一页，终止循环
            if (nextPageUrl == null) {
                log.info("已到达最后一页，终止分页请求");
                break;
            }
            // 更新为下一页URL，准备下一次循环
            currentPageUrl = nextPageUrl;
        }
        return "zhihuRecommend";
    }

    @ResponseBody
    @RequestMapping("/zhihuHotList")
    public String hello2(){
        ZhihuHotListCrawler.list();
        return "zhihuHotList";
    }

    @ResponseBody
    @RequestMapping("/zhihuHotRank")
    public String hotRank(){
        new Thread(ZhihuHotRankFetcher::list).start();
        return "zhihuHotRank";
    }

    @ResponseBody
    @RequestMapping("/touTiaoRecomment")
    public String touTiao(){
        new Thread(()->{
            while (true){
                ToutiaoRecommendQuestionJsoup.list();
            }
        }).start();
        return "touTiaoRecomment";
    }
}
