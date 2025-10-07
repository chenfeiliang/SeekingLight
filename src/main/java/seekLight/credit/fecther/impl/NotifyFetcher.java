package seekLight.credit.fecther.impl;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.NotifyReport;
import seekLight.credit.fecther.Fetcher;
import seekLight.credit.flow.QuotaReqFlow;
@Component
public class NotifyFetcher implements Fetcher<QuotaReqFlow, NotifyReport> {

    @Override
    public NotifyReport fetch(QuotaReqFlow flow) {
        return new NotifyReport("下发的额度报文");
    }
}
