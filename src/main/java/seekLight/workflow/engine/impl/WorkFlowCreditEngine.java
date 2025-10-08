package seekLight.workflow.engine.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.AbstractCreditEngine;
import seekLight.entity.WorkFlow;
import seekLight.workflow.service.impl.WorkFlowServiceImpl;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class WorkFlowCreditEngine extends AbstractCreditEngine<WorkFlowServiceImpl, WorkFlowContext> {

}
