package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.workflow.flow.Flow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class EndLogInterceptor implements PluginInterceptor<Flow,Object>{

    private static final EndLogInterceptor endLogInterceptor = new EndLogInterceptor();

    @Override
    public void afterFetch(Flow flow, Object data) {
        log.info("【{}】【{}】执行完成:",flow.getBusiSno(),flow.getStep());
    }

    public static EndLogInterceptor getSingle(){
        return endLogInterceptor;
    };
}
