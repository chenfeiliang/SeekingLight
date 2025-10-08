package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.workflow.flow.Flow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class ModelInterceptor implements PluginInterceptor<Flow,Object>{

    @Override
    public void afterFetch(Flow flow, Object data) {
        System.out.printf("执行[%s]模型\n",flow.getStep());
    }
}
