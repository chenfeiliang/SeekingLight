package seekLight.credit.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.CrisPbcReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.fecther.FetcherChain;
import seekLight.credit.fecther.impl.CrisPbcFetcher;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.interceptor.CrisPbcInterceptor;
import seekLight.credit.plugin.AbstractPlugin;
@Component
public  class CrisPbcPlugin extends AbstractPlugin<QuotaReqFlow, CrisPbcReport> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<QuotaReqFlow,CrisPbcReport>().add(new CrisPbcFetcher());
    }

    public CrisPbcPlugin() {
        this.addInterceptor(new CrisPbcInterceptor());
    }

    @Override
    public String getName() {
        return "crisPbc";
    }
}
