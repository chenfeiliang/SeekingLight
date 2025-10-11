package seekLight.workflow.plugin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.utils.SpringUtils;
import seekLight.workflow.fecther.Fetcher;
import seekLight.workflow.flow.Flow;
import seekLight.workflow.interceptor.*;

import java.util.LinkedList;
import java.util.List;
@Slf4j
@Component
public abstract class AbstractPlugin<F extends Flow,D> implements Plugin<F>{
    List<PluginInterceptor> pluginInterceptors =
            new PluginInterceptors();
    private static RawSaveInputInterceptor rawSaveInputInterceptor = SpringUtils.getBean(RawSaveInputInterceptor.class);

    private static RawSaveOutputInterceptor rawSaveOutputInterceptor = SpringUtils.getBean(RawSaveOutputInterceptor.class);

    static class PluginInterceptors extends LinkedList<PluginInterceptor> {
        public PluginInterceptors() {
            this.add(BeginLogInterceptor.getSingle());
            this.add(rawSaveInputInterceptor);
            this.add(rawSaveOutputInterceptor);
            this.add(EndLogInterceptor.getSingle());
        }
    }
    public void addInterceptor(PluginInterceptor interceptor){
        pluginInterceptors.add(pluginInterceptors.size()-3,
                interceptor);
    }

    @Override
    public boolean support(Flow flow) {
        return true;
    }

    @Override
    public void run(F flow) throws Exception {
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
