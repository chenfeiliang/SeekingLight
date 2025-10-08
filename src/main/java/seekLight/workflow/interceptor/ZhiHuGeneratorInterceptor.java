package seekLight.workflow.interceptor;

import org.springframework.stereotype.Component;
import seekLight.entity.WorkFlow;
import seekLight.service.model.DeepSeekClient;
import seekLight.service.model.DoubaoClient;
import seekLight.service.model.OllamaClient;
import seekLight.service.zhihu.ZhihuGeneratorTool;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import seekLight.workflow.context.WorkFlowContext;

@Component
@Slf4j

public class ZhiHuGeneratorInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
        String zhiHuGeneratorContent = new OllamaClient()
                .chat(String.format("请根据问题，写一个5000字以上的故事，请保证故事有头有尾，不要中途续写，" +
                                "开头参考现有的经典小说开头范例,问题: %s,具体问题内容: %s",
                data.get("zhiHuGenerator_title"), data.get("zhiHuGenerator_excerpt")),
                        ZhihuGeneratorTool.getRagInfo(data.get("judgeType_type")));
        flow.putParam("zhiHuPublish_content",zhiHuGeneratorContent);
    }
}
