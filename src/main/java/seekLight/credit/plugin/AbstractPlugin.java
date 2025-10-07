package seekLight.credit.plugin;

import seekLight.credit.fecther.Fetcher;
import seekLight.credit.flow.Flow;
import seekLight.credit.interceptor.BeginLogInterceptor;
import seekLight.credit.interceptor.EndLogInterceptor;
import seekLight.credit.interceptor.PluginInterceptor;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPlugin<F extends Flow,D> implements Plugin<F>{
    List<PluginInterceptor> pluginInterceptors =
            new PluginInterceptors();

    static class PluginInterceptors extends LinkedList<PluginInterceptor> {
        public PluginInterceptors() {
            this.add(BeginLogInterceptor.getSingle());
            this.add(EndLogInterceptor.getSingle());
        }
    }
    public void addInterceptor(PluginInterceptor interceptor){
        pluginInterceptors.add(pluginInterceptors.size()-2,
                interceptor);
    }

    @Override
    public boolean support(Flow flow) {
        return true;
    }

    @Override
    public void run(F flow) {
        if(!support(flow)){
            return;
        }
        Fetcher<F, D> fetcher = getFetcher();
        D data = fetcher.fetch(flow);
        for(PluginInterceptor pluginInterceptor:
                pluginInterceptors){
            pluginInterceptor.beforeFetch(flow,data);
        }
        for(PluginInterceptor pluginInterceptor:
                pluginInterceptors){
            pluginInterceptor.afterFetch(flow,data);
        }
    }

    public abstract Fetcher<F,D> getFetcher();


}
