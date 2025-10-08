package seekLight.workflow.fecther.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.fecther.Fetcher;
import seekLight.entity.WorkFlow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class BaseMapFetcher implements Fetcher<WorkFlowContext
        , Map<String,String>> {
    @Override
    public Map<String,String> fetch(WorkFlowContext flow) {
        List<String> params = Arrays.asList("","");
        Map<String,String> result = flow.getFlowParams();
        for(String key: flow.getFlowParams().keySet()){
            if(params.contains(key)){
                result.put(key,flow.getFlowParams().get(key));
            }
        }
        return result;
    }
}