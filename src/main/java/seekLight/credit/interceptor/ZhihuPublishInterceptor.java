package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.dto.flow.PublishDto;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.service.model.OllamaClient;
import seekLight.service.zhihu.ZhihuAnswerPublisher;

import java.util.Map;
@Component
public class ZhihuPublishInterceptor implements PluginInterceptor<QuotaReqFlow,Map<String,String>>{

    @Override
    public void afterFetch(QuotaReqFlow flow, Map<String,String> data) {
        String publish = ZhihuAnswerPublisher.publish(data.get("zhiHuPublish_questionId"), data.get("zhiHuPublish_content"));
        System.out.printf("publish结果："+publish);
    }
}
