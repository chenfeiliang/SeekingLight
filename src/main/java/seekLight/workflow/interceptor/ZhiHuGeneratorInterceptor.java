package seekLight.workflow.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import seekLight.entity.WorkFlow;
import seekLight.service.model.*;
import seekLight.service.toutiao.TouTiaoGeneratorTool;
import seekLight.service.zhihu.ZhihuGeneratorTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import lombok.extern.slf4j.Slf4j;
import seekLight.workflow.context.WorkFlowContext;

@Component
@Slf4j

public class ZhiHuGeneratorInterceptor implements PluginInterceptor<WorkFlowContext, Map<String, String>> {
    // 1. 定义线程池（建议作为类级别的静态变量，避免频繁创建/销毁线程
    //
    // ）
    // 核心线程数：根据 CPU 核心数配置（一般为 CPU 核心数 * 2），可根据业务调整
    private static final ExecutorService executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,  // 核心线程数
            Runtime.getRuntime().availableProcessors() * 4,  // 最大线程数
            60,  // 空闲线程存活时间
            TimeUnit.SECONDS,  // 时间单位
            new LinkedBlockingQueue<>(100),  // 任务队列（避免队列无界导致内存溢出）
            Executors.defaultThreadFactory(),  // 线程工厂
            new ThreadPoolExecutor.AbortPolicy()  // 拒绝策略（任务满时抛异常，便于监控）
    );

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String, String> data) {

        List<String> rules = Arrays.asList(
                "语言诙谐幽默，能触动心灵。",
                "要求模拟人的回答，不要太AI，200字内。",
                "不要说教的话语，不要出现，毕竟，XX才是真本事;就得XXX;别让XX,不要XX;等说教话语"
        );

        String role = "知乎资深用户";
        String question = "你是一个知乎资深用户，根据问题的上下文情感" +
                "，用适合的话语，模拟人的回答，最好用真实口语化的语气，回答这个问题: " + data.get("zhiHuGenerator_title");
        String result = new OllamaClient().chat(rules, question, role, 3);
        if (StringUtils.isEmpty(result)) {
            result = new OllamaClient().chat(rules, question, role, 3);
        }

        flow.putParam("zhiHuPublish_content", result);
    }

    public static void main(String[] args) throws JsonProcessingException {
        List<String> rules = Arrays.asList(
                "语言诙谐幽默，能触动心灵",
                "要求模拟人的回答，不要太AI，200字内。"
        );

        String role = "user";
        String question = "你是一个知乎资深用户，回答这个问题: " + "跟喜欢的男生一起住双床房，怎样才能自然的睡到男生的床上去呢?\n";
        String result = new OllamaClient().chat(rules, question, role, 3);
        if (StringUtils.isEmpty(result)) {
            result = new OllamaClient().chat(rules, question, role, 3);
        }
        System.out.println(result);

//        String result = "{\"大纲\":{\"开篇\":\"东汉末年，天下大乱，群雄割据。曹操挟天子以令诸侯，而刘备、孙权亦各据一方。然而，曹操并非真正的天子，而是以‘天子’之名行‘吃人’之实。传言他每夜都要饮下‘人血酒’，以维持其‘天命’。传言虽荒诞，却在民间流传甚广。\",\"铺垫\":\"曹操的‘人血酒’并非空穴来风，而是源自一个古老的仪式——‘天食人肉’。传说中，唯有饮下人血，方能获得‘天命’，并掌控天下。曹操的谋士郭嘉、司马懿皆知此秘，却因不同目的而选择不同立场。与此同时，一位神秘的女子——‘玉女’，悄然出现在曹操的宫廷之中，她似乎知晓一切。\",\"高潮\":\"曹操在一次‘天食人肉’仪式中，意外发现‘玉女’竟是当年被他所杀的‘天命之女’。她以‘人血酒’为引，揭露了曹操的真面目：他并非天子，而是‘吃人’的恶魔，而‘玉女’则是他当年‘人血酒’的唯一祭品。曹操在愤怒与恐惧中，最终选择自尽，以血祭‘天命’，而‘玉女’则化作‘天命之魂’，游荡于世间。\",\"结尾\":\"曹操死后，‘天命之魂’化作‘天命之书’，流传于后世。而‘玉女’的故事，则被后人编入神话，成为‘天食人肉’的传说。然而，真正的‘天命’，或许并非来自天子，而是来自人心。\"},\"主要角色\":[{\"角色标识\":\"角色1\",\"姓名\":\"曹操\",\"外貌\":\"高大威猛，面容冷峻，双目如炬，手持玉玺\",\"性格特征\":\"权谋多端，冷酷无情，内心充满对‘天命’的渴望\",\"角色动机\":\"通过‘人血酒’获得‘天命’，以掌控天下\",\"角色关系\":\"与郭嘉、司马懿为谋士，与玉女为宿敌\"},{\"角色标识\":\"角色2\",\"姓名\":\"玉女\",\"外貌\":\"清冷绝美，眉如远山，眼如寒星，身着白纱\",\"性格特征\":\"神秘莫测，冷静睿智，对‘天命’有着深刻的理解\",\"角色动机\":\"寻找‘天命之书’，揭露曹操的真面目\",\"角色关系\":\"与曹操为宿敌，与郭嘉、司马懿为盟友\"}]}\n";
//        String result2 = "{\"细纲\":[{\"第一章\":{\"标题\":\"血色玉琮现世\",\"情节描述\":\"良渚遗址出土的玉琮被神秘商人高价收购，玉琮内刻有古老符文，能沟通天地。商人将玉琮献给曹操，曹操在梦中见到古代帝王，得知‘天命’需以血祭为引。曹操开始秘密筹备血祭仪式，而玉琮的真正来历却暗藏玄机。\",\"爽点\":\"玉琮的神秘力量与曹操的野心形成强烈冲突，预示天命与人性的较量。\",\"悬念\":\"玉琮为何会出现在良渚？它是否真的能沟通天地？\"}},{\"第二章\":{\"标题\":\"玉女入宫\",\"情节描述\":\"神秘女子‘玉女’以商贾之女身份入宫，她精通占星术与符咒，举止优雅，言辞犀利。玉女在宫中游走，与曹操、郭嘉、司马懿等展开暗中较量，众人皆对她存疑，却又无法忽视她的影响力。\",\"爽点\":\"玉女的神秘气质与智慧令众人震撼，暗示她可能掌握关键秘密。\",\"悬念\":\"玉女为何对曹操的血祭仪式了如指掌？她是否与玉琮有某种联系？\"}},{\"第三章\":{\"标题\":\"血祭初启\",\"情节描述\":\"曹操在密室中举行首次血祭，以活人为祭品，玉琮发出红光，血祭仪式初见成效。玉女现身，直言‘天命’并非来自天，而是来自人。曹操怒斥她，玉女却冷静应对，暗示她知晓更多真相。\",\"爽点\":\"玉女的直言不讳挑战曹操的权威，展现人性与信仰的冲突。\",\"悬念\":\"玉女是否真的知晓天命的真相？她为何要阻止曹操？\"}},{\"第四章\":{\"标题\":\"玉琮之谜\",\"情节描述\":\"玉女向曹操展示玉琮的真正用途，它并非沟通天地，而是封印着‘血魂’，一旦开启，将释放无数亡魂。曹操震惊，郭嘉与司马懿则暗中调查玉琮的来源，发现其与三星堆青铜树有某种联系。\",\"爽点\":\"玉琮的真正用途颠覆众人认知，揭示历史与神话的交织。\",\"悬念\":\"玉琮为何被封印？它是否与三星堆的青铜树有关？\"}},{\"第五章\":{\"标题\":\"血魂觉醒\",\"情节描述\":\"玉琮在血祭中失控，释放出无数亡魂，曹操陷入疯狂，试图用更多鲜血镇压。玉女现身，与亡魂对抗，她自称是‘血魂’的守护者，誓要阻止曹操的疯狂。郭嘉与司马懿趁机策划，意图夺取玉琮。\",\"爽点\":\"亡魂的出现将故事推向高潮，玉女的身份逐渐揭晓。\",\"悬念\":\"玉女是否真的是‘血魂’的守护者？她为何要阻止曹操？\"}},{\"第六章\":{\"标题\":\"血祭之乱\",\"情节描述\":\"曹操在血祭中失控，亡魂化作阴兵，攻入京城。玉女带领亡魂对抗阴兵，郭嘉与司马懿趁机夺取玉琮，意图控制‘血魂’。曹操在混乱中试图自尽，却被玉女阻止，她警告他‘天命’将毁于贪婪。\",\"爽点\":\"亡魂与阴兵的战斗展现黑暗与恐怖，玉女的警告引发众人反思。\",\"悬念\":\"玉琮是否真的能控制‘血魂’？曹操的死亡是否意味着天命的终结？\"}},{\"第七章\":{\"标题\":\"玉琮之谜终解\",\"情节描述\":\"玉女带领众人深入良渚遗址，找到玉琮的起源，发现它本是封印‘血魂’的法器，由古代帝王所造。玉女揭示‘天命’并非来自天，而是来自人，真正的天命在于选择。曹操在真相面前崩溃，最终选择自尽，以血祭终结‘血魂’的封印。\",\"爽点\":\"玉琮的起源揭示历史与神话的真相，玉女的智慧与曹操的堕落形成鲜明对比。\",\"悬念\":\"玉女是否真的能终结‘血魂’？‘天命’是否真的可以被选择？\"}},{\"第八章\":{\"标题\":\"血魂归寂\",\"情节描述\":\"曹操自尽后，‘血魂’被封印，玉琮重归良渚遗址。玉女将玉琮埋藏，留下‘天命’的警示，她化作‘天命之魂’，游荡于世间，寻找真正的天命之主。郭嘉与司马懿则在混乱中崛起，试图掌控未来。\",\"爽点\":\"玉女的牺牲与‘天命’的警示为故事画上句点，留下无尽思考。\",\"悬念\":\"‘天命’是否真的可以被选择？玉女的‘天命之魂’是否还会归来？\"}}]}\n";
//        ObjectMapper objectMapper = new ObjectMapper();
//        //JsonNode rootNode = objectMapper.readTree(result2);
//        JsonNode rootNode = objectMapper.readTree(result2);
//        JsonNode dataNode = rootNode.get("细纲");
//        List<String> results = getStory(dataNode, result, result2);
//        log.info("最终结果: \n{}", results);
    }

    private static List<String> getStory(JsonNode dataNode, String result, String result2) {
        List<String> results = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        for (int j = 0; j < dataNode.size(); j++) {

            int finalJ = j;
            tasks.add(() -> {
                List<String> rules4 = Arrays.asList(
                        "是一个构思精妙绝伦的悬疑故事,故事中往往会揭露复杂的人性，并有不同的人物怀揣着不同的目的参与其中，尽量用人名，至少出现6个角色，1个男主，至少1个女主，通过人物对话推动情节",
                        "脑洞大开、设定新颖、荒诞不羁的故事，能以跳出俗套的故事设计赢得读者的青睐",
                        "可以虚构部分情节，可以通过一些黑暗、危机、恐怖、变态、犯罪等负面元素的使用，为读者提供警示",
                        "写作过程可以魔改，以下是基本法则：神话基因解码：历史与神性的嫁接术，文明起源重构，将考古发现神话化：良渚玉琮可改写为沟通天地的法器，" +
                                "三星堆青铜树实为扶桑神木的投影;从史册到神坛的跨越,如：悲剧型英雄：项羽乌江自刎改写为血祭楚魂，残兵化作永不沉没的阴兵战船",
                        "重要历史人物核心事件保留（如诸葛亮北伐），所有的历史人物，道具，功法必须有历史依据,不要政治正确，对话驱动： 能用对话说的，别用叙述。对话要针锋相对，充满火药味",
                        "对话要有换行,每个段落开头留2个空格,使用html语法，段落用<p>,标题用<h1>,对话用<p>,使用的所有标点符号必须正确!,不要有重复的内容！",
                        "生成的内容包括:\n" +
                                "-第X章: 1.章节内容\n" +
                                "要求仅返回的一个json串，不要包含markdown语法和对应的###,```等特殊符号，格式如下：\n" +
                                "{\"第X章\":{\"章节标题\":\"\",\"章节内容\":\"\"}}" +
                                "\n注意X是动态的中文数字,从一开始，章节内容要求1000字以上，所有章节内容按顺序连贯自然，逻辑通顺，情节逐步迭代。注意仅返回本章的内容",
                        "文风与技巧:\n" +
                                "-用人名作为人称：。多用心理活动，比如“XXX心头一紧”、“一个恶毒的念头在XXX脑中浮现”、“XXX冷笑一声”。\n" +
                                "-短句！短段！ 手机屏幕阅读，长句子就是灾难。一句话一行，三五行一段。\n" +
                                "-节奏钩子： 每一段结尾都要留个小悬念，让读者忍不住想看下一段发生了什么。比如：“他话音刚落，门外就传来了一个我意想不到的声音。”\n" +
                                "-情绪词拉满： “震惊”、“不敢置信”、“撕心裂肺”、“欣喜若狂”、“杀意沸腾”。不要吝啬这些词。\n"
                );
                String role4 = "你是一名思想天马行空的资深悬疑小说作家，你擅长构思精妙绝伦的悬疑故事，并拥有独特的工作步骤来完成构思";
                String question4 = "根据提示和细纲中的情节描述扩写" + dataNode.get(finalJ).fields().next().getKey() + "的内容，注意仅返回本章的内容，提示: 三国里有个吃人的大汉天子，参考大纲和主要角色信息为: \n" + result + "\n 参考的细纲为：" + result2;
                // 执行 chat 调用并返回结果（若执行异常，此处会抛出）
                return new DeepSeekClient(role4).chat(rules4, question4, role4, 2);
            });
        }
        try {
            // 3. 提交所有任务并获取 Future 列表（Future 用于接收异步结果）
            List<Future<String>> futureList = executor.invokeAll(tasks);

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
