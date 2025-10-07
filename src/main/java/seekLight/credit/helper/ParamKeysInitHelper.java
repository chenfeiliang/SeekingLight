package seekLight.credit.helper;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.Flow;
import seekLight.credit.flow.ModelParamCacheable;
@Component
public class ParamKeysInitHelper {
    public <E extends ModelParamCacheable & Flow> void setParamKeysCache(E flow){
        System.out.println("初始化要解析哪些变量");
    }
}
