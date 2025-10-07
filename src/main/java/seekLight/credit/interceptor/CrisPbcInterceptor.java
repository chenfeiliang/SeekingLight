package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.report.CrisPbcReport;
import seekLight.credit.flow.Flow;
@Component
public class CrisPbcInterceptor implements PluginInterceptor<Flow, CrisPbcReport>{

    @Override
    public void afterFetch(Flow flow, CrisPbcReport data) {
        System.out.printf("解析人行报告,并保存,报告内容为%s\n",
                data.toString());
    }
}
