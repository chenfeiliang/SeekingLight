package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
@Component
public class ModelInterceptor implements PluginInterceptor<Flow,Object>{

    @Override
    public void afterFetch(Flow flow, Object data) {
        System.out.printf("执行[%s]模型\n",flow.getStep());
    }
}
