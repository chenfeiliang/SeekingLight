package seekLight.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import seekLight.config.ThreadPools;
import seekLight.entity.WorkFlow;
import seekLight.service.toutiao.ToutiaoRecommendQuestionJsoup;
import seekLight.service.zhihu.ZhihuApiFetcher;
import seekLight.service.zhihu.ZhihuHotListCrawler;
import seekLight.service.zhihu.ZhihuHotRankFetcher;
import seekLight.utils.SnowflakeUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;
import seekLight.workflow.service.impl.WorkFlowServiceImpl;

import javax.validation.constraints.NotBlank;

@CrossOrigin(origins = "*")
@Controller
@Slf4j

public class MainController {
    @Autowired
    private WorkFlowCreditEngine workFlowCreditEngine;
    @Autowired
    private WorkFlowServiceImpl workFlowService;
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

    /**
     * 头条推荐问题重试接口（GET 方式）
     * @param busiSno 业务编号（必传，用于标识具体重试业务场景）
     * @return 接口响应提示
     */
    @ResponseBody
    @GetMapping("/retryFlow") // GET 请求方式，符合查询/触发类接口设计
    // @NotBlank 注解：校验 busiSno 不为空（需配合 Spring Validation 依赖）
    public String retryFlow(@RequestParam(value = "busiSno", required = true)
                                   @NotBlank(message = "业务编号 busiSno 不能为空") String busiSno) {
            //20251011142213764734586595840000
        WorkFlowContext flow = workFlowService.getFlow(busiSno);
        workFlowCreditEngine.doFlow(flow);
        return "retryFlow";
    }


    @ResponseBody
    @GetMapping("/test") // GET 请求方式，符合查询/触发类接口设计
    // @NotBlank 注解：校验 busiSno 不为空（需配合 Spring Validation 依赖）
    public String test() {
        ThreadPools.executor.execute(()->{
            WorkFlow workFlow = new WorkFlow();
            workFlow.setBusiSno(SnowflakeUtils.nextId());
            workFlow.setStep("");
            workFlow.setRoute("GeneratorOutline,GeneratorDetailedOutline,GeneratorAllArticle");
            WorkFlowContext flowContext = new WorkFlowContext(workFlow);
            flowContext.putParam("Generator_title", "我还是亵渎了神明");
            flowContext.putParam("Generator_questionId", "");
            workFlowCreditEngine.doFlow(flowContext);
        });
        return "test";
    }

    /**
     * 头条推荐问题重试接口（GET 方式）
     * @param busiSno 业务编号（必传，用于标识具体重试业务场景）
     * @return 接口响应提示
     */
    @ResponseBody
        @GetMapping("/getArticle") // GET 请求方式，符合查询/触发类接口设计
    // @NotBlank 注解：校验 busiSno 不为空（需配合 Spring Validation 依赖）
    public String getArticle(@RequestParam(value = "busiSno", required = true)
                            @NotBlank(message = "业务编号 busiSno 不能为空") String busiSno) {
        //20251011142213764734586595840000
        WorkFlowContext flow = workFlowService.getFlow(busiSno);
        return StringUtils.defaultIfBlank(flow.getParam("GeneratorAllArticleConvert_sourceContent"),flow.getParam("zhiHuPublish_content"));
    }


}
