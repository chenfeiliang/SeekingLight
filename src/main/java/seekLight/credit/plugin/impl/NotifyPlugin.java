package seekLight.credit.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.NotifyReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.fecther.FetcherChain;
import seekLight.credit.fecther.impl.NotifyFetcher;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.interceptor.NotifyInterceptor;
import seekLight.credit.plugin.AbstractPlugin;
@Component
public  class NotifyPlugin extends AbstractPlugin<QuotaReqFlow, NotifyReport> {

    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<QuotaReqFlow, NotifyReport>().add(new NotifyFetcher());
    }

    public NotifyPlugin() {
        addInterceptor(new NotifyInterceptor());
    }

    @Override
    public String getName() {
        return "notify";
    }
}
