package seekLight.credit.engine.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.engine.AbstractCreditEngine;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.service.impl.QuotaReqFlowServiceImpl;
@Component
public class QuotaReqFlowCreditEngine extends AbstractCreditEngine<QuotaReqFlowServiceImpl,QuotaReqFlow> {
    public QuotaReqFlowCreditEngine() {

        this.flowService = new QuotaReqFlowServiceImpl();
    }
}
