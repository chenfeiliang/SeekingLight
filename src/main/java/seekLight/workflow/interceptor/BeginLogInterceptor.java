package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.workflow.flow.Flow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class BeginLogInterceptor implements PluginInterceptor<Flow,Object>{
    private static final BeginLogInterceptor  beginLogInterceptor = new BeginLogInterceptor();

    @Override
    public void beforeFetch(Flow flow, Object data) {
        log.info("【{}】【{}】开始执行:",flow.getBusiSno(),flow.getStep());
    }

    public static BeginLogInterceptor getSingle(){
        return beginLogInterceptor;
    };
}
