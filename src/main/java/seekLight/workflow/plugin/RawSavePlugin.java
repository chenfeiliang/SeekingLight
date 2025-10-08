package seekLight.workflow.plugin;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.CrisPbcReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.CrisPbcFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.CrisPbcInterceptor;

@Component
public  class RawSavePlugin extends AbstractPlugin<WorkFlow, CrisPbcReport> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlow,CrisPbcReport>().add(new CrisPbcFetcher());
    }

    public RawSavePlugin() {
        this.addInterceptor(new CrisPbcInterceptor());
    }

    @Override
    public String getName() {
        return "crisPbc";
    }
}
