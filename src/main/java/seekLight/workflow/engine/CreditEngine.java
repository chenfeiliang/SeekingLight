package seekLight.workflow.engine;

import seekLight.workflow.flow.Flow;

public interface CreditEngine <F extends Flow>{
    void doFlow(F flow);
}
