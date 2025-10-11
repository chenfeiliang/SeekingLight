package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.BaseMapFetcher;
import seekLight.workflow.interceptor.ToutiaoPublishInterceptor;
import seekLight.workflow.interceptor.ZhihuPublishInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;

import java.util.Map;

@Component
public  class TouTiaoPublishPlugin extends AbstractPlugin<WorkFlowContext, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlowContext, Map<String,String>>().add(new BaseMapFetcher());
    }

    public TouTiaoPublishPlugin(ToutiaoPublishInterceptor toutiaoPublishInterceptor) {
        this.addInterceptor(toutiaoPublishInterceptor);
    }

    @Override
    public String getName() {
        return "touTiaoPublish";
    }
}
