package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.BaseMapFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.JudgeTypeInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;

import java.util.Map;
@Component
public  class ZhihuJudgeTypePlugin extends AbstractPlugin<WorkFlowContext, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlowContext, Map<String,String>>().add(new BaseMapFetcher());
    }

    public ZhihuJudgeTypePlugin() {
        this.addInterceptor(new JudgeTypeInterceptor());
    }

    @Override
    public String getName() {
        return "judgeType";
    }
}
