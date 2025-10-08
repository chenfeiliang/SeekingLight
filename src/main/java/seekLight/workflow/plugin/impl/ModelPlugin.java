package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.ModelReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.ModelFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.ModelInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;
@Component
public  class ModelPlugin extends AbstractPlugin<WorkFlow, ModelReport> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlow, ModelReport>().add(new ModelFetcher());
    }

    public ModelPlugin() {
        this.addInterceptor(new ModelInterceptor());
    }

    @Override
    public String getName() {
        return "model";
    }
}
