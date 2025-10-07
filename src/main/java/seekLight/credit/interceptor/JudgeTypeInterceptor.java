package seekLight.credit.interceptor;

import org.springframework.stereotype.Component;
import seekLight.credit.flow.QuotaReqFlow;
import seekLight.service.model.OllamaClient;

import java.util.Map;
@Component
public class JudgeTypeInterceptor implements PluginInterceptor<QuotaReqFlow, Map<String,String>>{

    @Override
    public void afterFetch(QuotaReqFlow flow, Map<String,String> data) {
        String result = new OllamaClient().chat("根据下面问题，判断适合用什么类型内容来回答。1-青春故事，" +
                "2-恐怖悬疑故事。结果仅返回数字,不要包含换行，空格等任何特殊符号.问题："+data.get("judgeType_question"));
        result = result.replaceAll("\n","");
        System.out.println("result: "+result);
        flow.putParam("judgeType_type",result);
    }
}
