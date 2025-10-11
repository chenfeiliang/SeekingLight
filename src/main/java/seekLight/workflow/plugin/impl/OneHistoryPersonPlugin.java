package seekLight.workflow.plugin.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.fecther.FetcherChain;
import seekLight.workflow.fecther.impl.BaseMapFetcher;
import seekLight.workflow.interceptor.ListPersonInterceptor;
import seekLight.workflow.interceptor.OneHistoryPersonInterceptor;
import seekLight.workflow.plugin.AbstractPlugin;

import java.util.Map;

@Component
public  class OneHistoryPersonPlugin extends AbstractPlugin<WorkFlowContext, Map<String,String>> {
    @Override
    public Fetcher getFetcher() {
        return new FetcherChain<WorkFlowContext, Map<String,String>>().add(new BaseMapFetcher());
    }

    public OneHistoryPersonPlugin(OneHistoryPersonInterceptor oneHistoryPersonInterceptor) {
        this.addInterceptor(oneHistoryPersonInterceptor);
    }

    @Override
    public String getName() {
        return "OneHistoryPerson";
    }
}
