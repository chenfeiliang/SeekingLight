package seekLight.workflow.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.service.model.OllamaClient;
import seekLight.service.toutiao.NormalTool;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j

public class GenerateDetailedOutlineInterceptor implements PluginInterceptor<WorkFlowContext, Map<String,String>>{

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String,String> data) throws Exception {
        List<String> rules2 = Arrays.asList(
                "是一个构思精妙绝伦的故事,故事中往往会揭露复杂的人性，并有不同的人物怀揣着不同的目的参与其中，尽量用人名，" +
                        "至少出现6个角色，1个男主，至少1个女主，通过人物对话推动情节",
                "脑洞大开、设定新颖、荒诞不羁的故事，能以跳出俗套的故事设计赢得读者的青睐,不要有重复的内容！",
                "所有的历史人物，道具，功法必须有历史依据.可以虚构部分情节，通过一些黑暗、危机、恐怖、变态、犯罪等负面元素的使用，为读者提供警示",
                "情节遵循爽文底层逻辑:“情绪操纵术”:\n" +
                        "-“沉冤昭雪”的爽： 主角被冤枉、被鄙视、被践踏，然后用绝对的实力或智商，把所有看不起他的人的脸，狠狠地踩在地上摩擦;\n" +
                        "-“扮猪吃虎”的爽： 主角明明是满级大佬，却伪装成新手村菜鸟，在众人嘲笑中，不动声色地碾压全场，最后亮出身份，惊掉所有人的下巴。\n"+
                        "-“逆天改命”的爽： 主角出身底层，一无所有，但通过一个契机（异能、系统、重生、奇遇），打破阶层，将曾经遥不可及的人和物，全部收入囊中。\n" +
                        "-“快意恩仇”的爽： 坏人必须死，而且要立刻、马上、用最解气的方式死。不搞原谅，不搞宽恕，主打一个“你动我一根毫毛，我灭你满门”。”\n" +
                        "-“认知碾压”的爽： 主角拥有超越当前世界维度的知识或能力（比如现代人穿越回古代，或者地球人带系统进入游戏世界）\n",
                "剧情跌宕起伏，疯狂的“打脸”与“剧情推进\n”，" +
                        "-第一个巴掌（小试牛刀）： 获得金手指后，立刻解决开局的第一个小麻烦，给读者一点甜头\n。" +
                        "-矛盾升级（打了小的，来了老的）： 解决了小角色，他背后的大靠山必然会出场。反派的等级要像打游戏一样，一关比一关强。\n" +
                        "-能力展示与资源积累： 在一次次打脸的过程中，主角的金手指要不断变强，或者主角利用金手指积累资源（财富、人脉、名声、势力）。\n"+
                        "-情感拉扯（增加粘性）： 纯打脸会腻。中间穿插一条情感线或亲情线，让主角的动机更丰满。可以是一个默默守护的忠犬男/女配，也可以是为了保护家人/爱人而战。\n",
                "终极的“高潮”与“圆满结局”，所有矛盾汇集于此，给读者一个酣畅淋漓的最终释放。\n" +
                        "-最终对决： 主角与最大的反派（渣男、皇帝、最终BOSS）进行终极PK。主角必须将之前积累的所有资源、能力、人脉全部用上，进行一场漂亮的翻身仗。\n" +
                        "-真相大白： 揭露所有阴谋，把反派的脸皮彻底撕下来，让他社会性死亡或物理性死亡。\n" +
                        "-圆满收官（给读者一个交代）\n",
                "生成的内容包括:\n" +
                        "文章标题: 根据上下文生成合适的标题\n"+
                        "-第一章: 1.情节描述;2.爽点;3.悬念;\n" +
                        "-第二章: 1.情节描述;2.爽点;3.悬念;依次类推,生成约7-12章，要求仅返回的一个json串，不要包含markdown语法和对应的###,```等特殊符号，格式如下：\n" +
                        "{\"文章标题\":\"\",\"细纲\":[{\"第X章\":{\"标题\":\"\",\"情节描述\":\"\",\"爽点\":\"\",\"悬念\":\"\"}},{\"第X章\":{\"标题\":\"\",\"情节描述\":\"\",\"爽点\":\"\",\"悬念\":\"\"}}]}" +
                        "\n注意X是动态的,从一开始，如第一章，第二章...，章节标题是概括本章内容,不要出现人名的10字以内的文字，情节描述要求100字以上"
        );
        String role2 = "你是一名思想天马行空的资深悬疑小说作家，你擅长构思精妙绝伦的悬疑故事，并拥有独特的工作步骤来完成构思";
        String question2 = "根据提示，写一个故事的细纲，提示: "+data.get("Generator_title")+"，参考大纲和主要角色信息为: \n" + data.get("GenerateOutline_content");
        String result2 = new OllamaClient(role2).chat(rules2, question2, role2, 2);
        log.info("GenerateDetailedOutline-最终结果: \n{}",result2);
        flow.putParam("GenerateDetailedOutline_content",result2);
    }
}

