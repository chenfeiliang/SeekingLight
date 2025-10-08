package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.BaseMapFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.ZhihuPublishInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;

import java.util.Map;
@Component
public  class ZhihuPublishPlugin extends AbstractPlugin<WorkFlowContext, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlowContext, Map<String,String>>().add(new BaseMapFetcher());
    }

    public ZhihuPublishPlugin() {
        this.addInterceptor(new ZhihuPublishInterceptor());
    }

    @Override
    public String getName() {
        return "zhiHuPublish";
    }
}
