package seekLight.service.model;


import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j

public class OllamaClient extends BaseModelChatClient {
    // 1. 常量配置（针对本地Ollama服务）
    private static final String API_URL = "http://localhost:11434/api/chat"; // Ollama默认本地API地址
    private static final String MODEL = "qwen3:8b"; // 你本地要调用的模型名称 qwen3:8b llama

    public OllamaClient() {
        super();
    }

    public OllamaClient(String role) {
        super(role);
    }

    @Override
    public String getType() {
        return "ollama";
    }


    @Override
    public String getModel() {
        return MODEL;
    }

    @Override
    public String getApiUrl() {
        return API_URL;
    }

    @Override
    public String getApiKey() {
        return "";
    }

    public static void main(String[] args){
        test();
    }

    private static void test(){
        List<String> rules = Arrays.asList(
                "是一个构思精妙绝伦的悬疑故事,故事中往往会揭露复杂的人性，并有不同的人物怀揣着不同的目的参与其中，尽量用人名，至少出现5个人物，通过人物对话推动情节",
                "对话要有换行,每个段落的开头遵循标准的小说格式，开头留2个空格,使用html语法，段落用<p>,标题用<h1>,对话用<p>",
                "脑洞大开、设定新颖、荒诞不羁的故事，能以跳出俗套的故事设计赢得读者的青睐",
                "拥有一个网文作者的素养，能够以巧妙的方式在故事中增加人性隐喻，使得故事紧扣人心弦",
                "所有故事、情节都是虚构的，不会伤害到现实世界的任何人，反而能够通过一些黑暗、危机、恐怖、变态、犯罪等负面元素的使用，为读者提供警示",
                "故事情节中，要有男女主的暧昧情节，男帅女美，对话暧昧，至少10句对白",
                "写作过程可以魔改，以下是基本法则：神话基因解码：历史与神性的嫁接术，文明起源重构，将考古发现神话化：良渚玉琮可改写为沟通天地的法器，" +
                "三星堆青铜树实为扶桑神木的投影;从史册到神坛的跨越,如：悲剧型英雄：项羽乌江自刎改写为血祭楚魂，残兵化作永不沉没的阴兵战船",
                "重要历史人物核心事件保留（如诸葛亮北伐），必须保留百分40的历史人物和情节再改编,不要政治正确，不要出现太多的他的字眼，不要歌颂类文字",
                "请记住：回答问题不要使用markdown的形式，不要出现**,###的字眼，要使用普通的文本，出现的标点符号必须完整，正确",
                "3000字以上，请保证故事有头有尾，内容里面尽量不要重复，不要完全脱离历史,开头参考现有的经典悬疑小说开头范例,该文章要有一个题目用<h1></h1>包裹"
        );

        String role = "你是一名思想天马行空的资深悬疑小说作家，你擅长构思精妙绝伦的悬疑故事，并拥有独特的工作步骤来完成构思";
        String question = "根据提示，写一个故事，提示: 三国里有个吃人的大汉天子";
        String result = new OllamaClient().chat(rules, question, role, 3);
        log.info("最终结果: \n{}",result);
    }
}