package seekLight.workflow.engine.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.AbstractCreditEngine;
import seekLight.workflow.service.impl.WorkFlowServiceImpl;
@Component
@Slf4j
public class WorkFlowCreditEngine extends AbstractCreditEngine<WorkFlowServiceImpl, WorkFlowContext> {

}
