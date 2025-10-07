package seekLight.credit.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.fecther.FetcherChain;
import seekLight.credit.fecther.impl.BaseMapFetcher;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.interceptor.ZhihuPublishInterceptor;
import seekLight.credit.plugin.AbstractPlugin;

import java.util.Map;
@Component
public  class ZhihuPublishPlugin extends AbstractPlugin<QuotaReqFlow, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<QuotaReqFlow, Map<String,String>>().add(new BaseMapFetcher());
    }

    public ZhihuPublishPlugin() {
        this.addInterceptor(new ZhihuPublishInterceptor());
    }

    @Override
    public String getName() {
        return "zhiHuPublish";
    }
}
