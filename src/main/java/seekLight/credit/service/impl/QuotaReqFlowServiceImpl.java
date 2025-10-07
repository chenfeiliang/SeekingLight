package seekLight.credit.service.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.credit.service.FlowService;
@Component
public class QuotaReqFlowServiceImpl implements FlowService<QuotaReqFlow> {
    @Override
    public void saveOrUpdateFlow(QuotaReqFlow flow) {
        System.out.println("保存流水:"+flow.getBusiSno());
    }
}
