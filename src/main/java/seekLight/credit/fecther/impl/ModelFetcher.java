package seekLight.credit.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.ModelReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.flow.QuotaReqFlow;
@Component
public class ModelFetcher implements Fetcher<QuotaReqFlow
        , ModelReport> {

    @Override
    public ModelReport fetch(QuotaReqFlow flow) {
        return new ModelReport("模型入参");
    }
}
