package seekLight.credit.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.ModelReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.fecther.FetcherChain;
import seekLight.credit.fecther.impl.ModelFetcher;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.interceptor.ModelInterceptor;
import seekLight.credit.plugin.AbstractPlugin;
@Component
public  class ModelPlugin extends AbstractPlugin<QuotaReqFlow, ModelReport> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<QuotaReqFlow, ModelReport>().add(new ModelFetcher());
    }

    public ModelPlugin() {
        this.addInterceptor(new ModelInterceptor());
    }

    @Override
    public String getName() {
        return "model";
    }
}
