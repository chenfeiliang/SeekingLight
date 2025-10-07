package seekLight.credit.service;

import seekLight.credit.flow.Flow;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static seekLight.credit.engine.AbstractCreditEngine.END_STEP;

public interface FlowService <F extends Flow> {
    void saveOrUpdateFlow(F flow);

    default String updateStepToNext(F flow){
        String nextStep = getNextStep(flow);
        if(Objects.nonNull(nextStep)){
            flow.setStep(nextStep);
        }else {
            flow.setStep(END_STEP);
        }
        return nextStep;
    }
    default String getNextStep(F flow){
        List<String> routes =
                Arrays.asList(flow.getRoute().split(","));
        if(flow.getStep().equals(END_STEP)){
            return END_STEP;
        }
        int index = routes.indexOf(flow.getStep());
        if(!routes.isEmpty()&&index<routes.size()-1){
            return routes.get(index+1);
        }
        return null;
    }
}
