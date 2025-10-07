package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
@Component
public class EndLogInterceptor implements PluginInterceptor<Flow,Object>{

    private static final EndLogInterceptor endLogInterceptor = new EndLogInterceptor();

    @Override
    public void afterFetch(Flow flow, Object data) {
        System.out.printf("[%s][%s]执行完成\n\n",
                flow.getBusiSno(),flow.getStep());
    }

    public static EndLogInterceptor getSingle(){
        return endLogInterceptor;
    };
}
