package seekLight.workflow.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.ModelReport;
import seekLight.workflow.fecther.Fetcher;
import seekLight.entity.WorkFlow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class ModelFetcher implements Fetcher<WorkFlow
        , ModelReport> {

    @Override
    public ModelReport fetch(WorkFlow flow) {
        return new ModelReport("模型入参");
    }
}
