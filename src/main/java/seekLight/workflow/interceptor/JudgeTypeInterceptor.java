package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.entity.WorkFlow;
import seekLight.enums.ZhihuGeneratorTypeEnum;
import seekLight.service.model.DoubaoClient;

import java.util.Map;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import seekLight.service.model.OllamaClient;
import seekLight.workflow.context.WorkFlowContext;

@Component
@Slf4j

public class JudgeTypeInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
//        String result = new OllamaClient().chat("根据下面问题，判断适合用什么类型内容来回答。1-青春故事，" +
//                "2-恐怖悬疑故事。结果仅返回数字,不要包含换行，空格等任何特殊符号.问题："+data.get("judgeType_question"));
//        result = result.replaceAll("\n","");
        String result = String.valueOf(new Random().nextInt(6) + 1);
        //String result="6";
        log.info("result: {},desc:{}",result, ZhihuGeneratorTypeEnum.getByCode(result));
        flow.putParam("judgeType_type",result);
    }
}
