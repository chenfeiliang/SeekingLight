package seekLight.workflow.interceptor;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import seekLight.entity.WorkFlow;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import seekLight.service.zhihu.ZhihuAnswerPublisher;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.excetion.FlowAsyncInterrupterException;

@Component
@Slf4j

public class ZhihuPublishInterceptor implements PluginInterceptor<WorkFlowContext,Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
        if(!StringUtils.equals(flow.getParam("publishByMyself"),"1")){
            throw new FlowAsyncInterrupterException("临时中断");
        }
        String publish = ZhihuAnswerPublisher.publish(data.get("zhiHuPublish_questionId"), data.get("zhiHuPublish_content"));
        log.info("publish结果："+publish);
        flow.putParam("zhihuPublish_result",publish);
        if("发布失败".equals(publish)){
            throw new FlowAsyncInterrupterException("发布失败");
        }
    }
}
