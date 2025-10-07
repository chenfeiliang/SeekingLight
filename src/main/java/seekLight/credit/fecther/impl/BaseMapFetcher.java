package seekLight.credit.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.flow.QuotaReqFlow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
@Component
public class BaseMapFetcher implements Fetcher<QuotaReqFlow
        , Map<String,String>> {
    @Override
    public Map<String,String> fetch(QuotaReqFlow flow) {
        List<String> params = Arrays.asList("","");
        Map<String,String> result = flow.getFowParams();
        for(String key: flow.getFowParams().keySet()){
            if(params.contains(key)){
                result.put(key,flow.getFowParams().get(key));
            }
        }
        return result;
    }
}