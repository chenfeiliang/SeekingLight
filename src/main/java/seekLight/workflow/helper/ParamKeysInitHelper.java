package seekLight.workflow.helper;

import org.springframework.stereotype.Component;
import seekLight.workflow.flow.Flow;
import seekLight.workflow.flow.ModelParamCacheable;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class ParamKeysInitHelper {
    public <E extends ModelParamCacheable & Flow> void setParamKeysCache(E flow){
        log.info("初始化要解析哪些变量");
    }
}
