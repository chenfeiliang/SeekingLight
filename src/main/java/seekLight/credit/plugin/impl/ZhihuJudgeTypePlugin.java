package seekLight.credit.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.fecther.FetcherChain;
import seekLight.credit.fecther.impl.BaseMapFetcher;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.interceptor.JudgeTypeInterceptor;
import seekLight.credit.plugin.AbstractPlugin;

import java.util.Map;
@Component
public  class ZhihuJudgeTypePlugin extends AbstractPlugin<QuotaReqFlow, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<QuotaReqFlow, Map<String,String>>().add(new BaseMapFetcher());
    }

    public ZhihuJudgeTypePlugin() {
        this.addInterceptor(new JudgeTypeInterceptor());
    }

    @Override
    public String getName() {
        return "judgeType";
    }
}
