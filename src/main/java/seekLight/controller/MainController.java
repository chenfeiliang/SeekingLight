package seekLight.controller;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import seekLight.entity.WorkFlow;
import seekLight.service.model.OllamaClient;
import seekLight.service.toutiao.ToutiaoRecommendQuestionJsoup;
import seekLight.service.zhihu.ZhihuHotListCrawler;
import seekLight.service.zhihu.ZhihuHotRankFetcher;
import seekLight.utils.SnowflakeUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;
import seekLight.service.zhihu.ZhihuApiFetcher;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*")
@Controller
@Slf4j

public class MainController {
    @Autowired
    private WorkFlowCreditEngine workFlowCreditEngine;
    @ResponseBody
    @RequestMapping("/zhihuRecommend")
    public String hello(){
        for(int i = 0 ;i<10000;i++){
            try {
                // 1. 初始页URL（第一页）
                String currentPageUrl = "https://www.zhihu.com/api/v3/feed/topstory/recommend?limit=10&desktop=true";
                // 2. 循环请求下一页（示例：最多请求3页，可调整）
                int maxPage = 10000;
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
            }catch (Exception ex){
                log.info("error==>",ex);
            }
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

    @ResponseBody
    @RequestMapping("/history")
    public String history(){
        new Thread(()->{
            try {
              WorkFlow workFlow = new WorkFlow();
                workFlow.setBusiSno( SnowflakeUtils.nextId());
                workFlow.setStep("ListDynasty");
                workFlow.setRoute("ListDynasty");
                WorkFlowContext flowContext = new WorkFlowContext(workFlow);
                workFlowCreditEngine.doFlow(flowContext);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
        return "touTiaoRecomment";
    }
}
