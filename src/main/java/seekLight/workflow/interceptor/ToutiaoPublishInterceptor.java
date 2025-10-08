package seekLight.workflow.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.service.toutiao.ToutiaoPublishAnswerJsoup;
import seekLight.service.zhihu.ZhihuAnswerPublisher;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.excetion.FlowAsyncInterrupterException;

import java.util.Map;

@Component
@Slf4j

public class ToutiaoPublishInterceptor implements PluginInterceptor<WorkFlowContext,Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
       // throw new FlowAsyncInterrupterException("临时中断");
        String publish = ToutiaoPublishAnswerJsoup.publish(data.get("touTiaoPublish_questionId"), data.get("touTiaoPublish_content"));
        log.info("publish结果："+publish);
        flow.putParam("touTiao_result",publish);
        if("发布失败".equals(publish)){
            throw new FlowAsyncInterrupterException("发布失败");
        }
    }
}
