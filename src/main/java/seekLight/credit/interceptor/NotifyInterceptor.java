package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
@Component
public class NotifyInterceptor implements PluginInterceptor<Flow,Object>{

    @Override
    public void afterFetch(Flow flow, Object data) {
        System.out.printf("下发额度结果\n",flow.getStep());
    }
}
