package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.NotifyReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.NotifyFetcher;
import seekLight.entity.WorkFlow;
import seekLight.workflow.interceptor.NotifyInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;
@Component
public  class NotifyPlugin extends AbstractPlugin<WorkFlow, NotifyReport> {

    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlow, NotifyReport>().add(new NotifyFetcher());
    }

    public NotifyPlugin(NotifyInterceptor notifyInterceptor) {
        addInterceptor(notifyInterceptor);
    }

    @Override
    public String getName() {
        return "notify";
    }
}
