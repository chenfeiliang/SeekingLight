package seekLight.workflow.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.config.ThreadPools;
import seekLight.dto.ArticleDto;
import seekLight.service.model.DeepSeekClient;
import seekLight.service.model.DoubaoClient;
import seekLight.service.model.OllamaClient;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Slf4j

public class GeneratorAllArticleInterceptor implements PluginInterceptor<WorkFlowContext, Map<String, String>> {

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String, String> data) throws Exception {
        //大纲
        String outlineContent = data.get("GenerateOutline_content");
        //细纲
        String detailedOutlineContent = data.get("GenerateDetailedOutline_content");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(detailedOutlineContent);
        JsonNode dataNode = rootNode.get("细纲");
        List<String> results = getStory(dataNode, outlineContent, detailedOutlineContent,data);
        flow.putParam("GeneratorAllArticle_Content", JSON.toJSONString(results));
    }

    private  List<String> getStory(JsonNode dataNode, String result, String result2,Map<String, String>  data) {
        List<String> results = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        for (int j = 0; j < dataNode.size(); j++) {
            int finalJ = j;
            tasks.add(() -> {
                List<String> rules4 = Arrays.asList(
                        "对话驱动,能用对话说的，别用叙述。对话要针锋相对，充满火药味。对各个人物对话，表情，动作描述要细腻",
                        "脑洞大开、设定新颖、荒诞不羁的故事，能以跳出俗套的故事设计赢得读者的青睐,不要有重复的内容！",
                        "所有的历史人物，道具，功法必须有历史依据.可以虚构部分情节，可以通过一些黑暗、危机、恐怖、变态、犯罪等负面元素的使用，为读者提供警示",
                        "对话要有换行，但是同一个人的动作和说话，标签要放在同一行，不同人放在不同行,使用markdow语法,如：### 标题 ,使用的所有标点符号必须正确!",
                        "生成的内容包括:\n" +
                                "-第X章: 1.章节内容\n" +
                                "要求仅返回的一个json串，不要包含markdown语法和对应的###,```等特殊符号，格式如下：\n" +
                                "{\"第X章\":{\"章节标题\":\"\",\"章节内容\":\"\"}}" +
                                "\n注意X是动态的中文数字,从一开始，章节内容要求1000字以上，所有章节内容按顺序连贯自然，逻辑通顺，情节逐步迭代。" +
                                "注意仅返回本章的内容，格式一定要正确！！！",
                        "文风与技巧:\n" +
                                "-用人名作为人称：。多用心理活动，比如“XXX心头一紧”、“一个恶毒的念头在XXX脑中浮现”、“XXX冷笑一声”。\n" +
                                "-对话驱动： 能用对话说的，别用叙述。对话要针锋相对，充满火药味"+
                                "-短句！短段！ 手机屏幕阅读，长句子就是灾难。一句话一行，三五行一段。\n" +
                                "-节奏钩子： 每一段结尾都要留个小悬念，让读者忍不住想看下一段发生了什么。比如：“他话音刚落，门外就传来了一个我意想不到的声音。”\n" +
                                "-情绪词拉满： “震惊”、“不敢置信”、“撕心裂肺”、“欣喜若狂”、“杀意沸腾”。不要吝啬这些词。\n"
                );
                String role4 = "你是一名思想天马行空的资深悬疑小说作家，你擅长构思精妙绝伦的悬疑故事，并拥有独特的工作步骤来完成构思";
                String question4 = "根据提示和细纲中的情节描述扩写" + dataNode.get(finalJ).fields().next().getKey() + "的内容，注意仅返回本章的内容，提示: "+data.get("Generator_title")+"，参考大纲和主要角色信息为: \n" + result + "\n 参考的细纲为：" + result2;
                // 执行 chat 调用并返回结果（若执行异常，此处会抛出）
                return new OllamaClient(role4).chat(rules4, question4, role4, 3);
            });
        }
        try {
            // 3. 提交所有任务并获取 Future 列表（Future 用于接收异步结果）
            List<Future<String>> futureList = ThreadPools.executor.invokeAll(tasks);

            // 4. 遍历 Future 列表，获取每个任务的结果并拼接到 suggestion
            for (Future<String> future : futureList) {

                // 获取结果（若任务未完成，会阻塞直到完成；若任务异常，会抛出 ExecutionException）
                String resultTemp = future.get();
                // 拼接结果（每个结果后加换行，与原逻辑一致）
                if (resultTemp != null && !resultTemp.isEmpty()) {
                    results.add(resultTemp);
                }
            }
        } catch (Exception e) {
            log.error("error===>", e);
        }
        return results;
    }
}

