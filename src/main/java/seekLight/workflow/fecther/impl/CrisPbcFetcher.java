package seekLight.workflow.fecther.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.CrisPbcReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.entity.WorkFlow;
@Slf4j
@Component
public class CrisPbcFetcher implements Fetcher<WorkFlow, CrisPbcReport> {

    @Override
    public CrisPbcReport fetch(WorkFlow flow) {
        return new CrisPbcReport("人行报告");
    }
}
