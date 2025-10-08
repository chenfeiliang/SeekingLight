package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.CrisPbcReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.CrisPbcFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.CrisPbcInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;
@Component
public  class CrisPbcPlugin extends AbstractPlugin<WorkFlow, CrisPbcReport> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlow,CrisPbcReport>().add(new CrisPbcFetcher());
    }

    public CrisPbcPlugin() {
        this.addInterceptor(new CrisPbcInterceptor());
    }

    @Override
    public String getName() {
        return "crisPbc";
    }
}
