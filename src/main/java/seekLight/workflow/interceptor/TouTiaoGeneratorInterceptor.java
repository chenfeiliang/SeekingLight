package seekLight.workflow.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.service.model.OllamaClient;
import seekLight.service.toutiao.TouTiaoGeneratorTool;
import seekLight.service.zhihu.ZhihuGeneratorTool;
import seekLight.workflow.context.WorkFlowContext;

import java.util.Map;

@Component
@Slf4j

public class TouTiaoGeneratorInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
        String zhiHuGeneratorContent = new OllamaClient()
                .chat(String.format("请根据问题，用中文写一个2000字以上的回答,分成3-4个段落,问题: %s",
                data.get("touTiaoGenerator_title")),
                        TouTiaoGeneratorTool.getRagInfo());
        flow.putParam("touTiaoPublish_content",zhiHuGeneratorContent);
    }
}
