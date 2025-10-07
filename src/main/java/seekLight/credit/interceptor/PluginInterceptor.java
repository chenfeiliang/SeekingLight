package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
@Component
public interface PluginInterceptor <F extends Flow,D>{

    default void beforeFetch(F flow,D data){}

    default void afterFetch(F flow,D data){}
}
