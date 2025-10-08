package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.workflow.dto.report.CrisPbcReport;
import seekLight.workflow.flow.Flow;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class CrisPbcInterceptor implements PluginInterceptor<Flow, CrisPbcReport>{

    @Override
    public void afterFetch(Flow flow, CrisPbcReport data) {
        System.out.printf("解析人行报告,并保存,报告内容为%s\n",
                data.toString());
    }
}
