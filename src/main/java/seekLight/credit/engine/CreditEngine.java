package seekLight.credit.engine;

import seekLight.credit.flow.Flow;

public interface CreditEngine <F extends Flow>{
    void doFlow(F flow);
}
