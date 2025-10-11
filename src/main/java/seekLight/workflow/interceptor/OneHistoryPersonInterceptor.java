package seekLight.workflow.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.entity.WorkFlow;
import seekLight.service.model.DoubaoClient;
import seekLight.service.model.OllamaClient;
import seekLight.service.toutiao.NormalTool;
import seekLight.utils.SnowflakeUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.util.Map;

@Component
@Slf4j

public class OneHistoryPersonInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{
    @Autowired
    private WorkFlowCreditEngine workFlowCreditEngine;
    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) throws Exception {
        String oneHistoryPersonInfo = data.get("OneHistoryPerson_info");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(oneHistoryPersonInfo);
        String question = String.format("背景:%s,人物:%s,冷门故事:%s,热门故事%s.根据以上信息，结合冷门故事，" +
                "热门故事,扩写故事成为一个3000字以上的玄幻小说，不要有重复的内容！，你是一位专业的历史类玄幻作家，基于历史改编，" +
                        "写出令人赞叹的天马行空的故事，融合至少3种脑洞元素，运用起承转合的结构，让故事迭代起伏" +
                        "，要求有百分之四十的成分是虚构的玄幻内容，要有迭代起伏的故事情节",rootNode.get("background").asText(),rootNode.get("person").asText(),
                rootNode.get("cold_story").asText(),rootNode.get("hot_story").asText());
        String normalContent = new OllamaClient()
                .chat(question, NormalTool.getRagInfoStory());
        flow.putParam("OneHistoryPerson_content",normalContent);
    }
}

