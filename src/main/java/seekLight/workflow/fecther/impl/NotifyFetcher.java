package seekLight.workflow.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.NotifyReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.entity.WorkFlow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class NotifyFetcher implements Fetcher<WorkFlow, NotifyReport> {

    @Override
    public NotifyReport fetch(WorkFlow flow) {
        return new NotifyReport("下发的额度报文");
    }
}
