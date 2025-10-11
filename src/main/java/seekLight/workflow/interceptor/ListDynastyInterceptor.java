package seekLight.workflow.interceptor;

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
public class ListDynastyInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{
    @Autowired
    private WorkFlowCreditEngine workFlowCreditEngine;

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) {
        String question = "1. 列出华夏文明5000多年的发展历史，并列出每个时代的一个代表性人物，若无代表人物，列出一位其他历史人物，返回每个时代的道教相关的故事，\n" +
                "返回格式为：背景：xxxx，人物：xxxx，冷门故事：xxxxx，热门故事：xxxxx，格式化成一个json字符串,\n" +
                "[{\"background\":\"\",\"person\":\"\",\"cold_story\":\"XXX\"，,\"hot_story\":\"XXX\"}]，" +
                "列出所有时代!列出所有时代!,列出所有时代!列出所有时代!,列出所有时代!列出所有时代!，列出所有时代!列出所有时代!" +
                "，列出所有时代!列出所有时代!，其中每个时代要有故事,仅返回json串,仅返回json串,仅返回json串,不要有```等markdown的形式，直接返回json普通文本\n";
        String normalContent = new OllamaClient()
                .chat(question, NormalTool.getRagInfo());
        flow.putParam("ListDynasty_content",normalContent);
        doNext(flow,normalContent);
    }
    private void doNext(WorkFlowContext flow,String normalContent){
        try {
            log.info("【{}】【{}】开始生成每个时代的10个人物故事:",flow.getBusiSno(),flow.getStep());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(normalContent);
            for(int i = 0 ;i<rootNode.size();i++){
                JsonNode jsonNode = rootNode.get(i);
                WorkFlow workFlowTemp = new WorkFlow();
                workFlowTemp.setBusiSno( SnowflakeUtils.nextId());
                workFlowTemp.setStep("ListPerson");
                workFlowTemp.setRoute("ListPerson");
                workFlowTemp.setRelyBusiSno(flow.getBusiSno());
                WorkFlowContext flowContextTemp = new WorkFlowContext(workFlowTemp);
                flowContextTemp.putParam("ListPerson_background", jsonNode.get("background").asText());
                workFlowCreditEngine.doFlow(flowContextTemp);
            }
        }catch (Exception ex){
            log.error("【{}】【{}】执行异常:",flow.getBusiSno(),flow.getStep(),ex);
        }
    }
}

