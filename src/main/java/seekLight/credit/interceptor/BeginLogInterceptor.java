package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
@Component
public class BeginLogInterceptor implements PluginInterceptor<Flow,Object>{
    private static final BeginLogInterceptor  beginLogInterceptor = new BeginLogInterceptor();

    @Override
    public void beforeFetch(Flow flow, Object data) {
        System.out.printf("[%s][%s]开始执行\n",
                flow.getBusiSno(),flow.getStep());
    }

    public static BeginLogInterceptor getSingle(){
        return beginLogInterceptor;
    };
}
