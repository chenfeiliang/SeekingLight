package seekLight.credit.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.CrisPbcReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.flow.QuotaReqFlow;
@Component
public class CrisPbcFetcher implements Fetcher<QuotaReqFlow, CrisPbcReport> {

    @Override
    public CrisPbcReport fetch(QuotaReqFlow flow) {
        return new CrisPbcReport("人行报告");
    }
}
