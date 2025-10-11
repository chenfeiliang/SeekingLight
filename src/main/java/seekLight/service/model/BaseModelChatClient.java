package seekLight.service.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import seekLight.dto.ChatMessage;
import seekLight.utils.SeekFileUtils;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Data
@Slf4j
public abstract class BaseModelChatClient {

    // 1. 定义线程池（建议作为类级别的静态变量，避免频繁创建/销毁线程）
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

    private File file = new File("E:\\ideawork\\SeekingLight\\files\\chat.txt");

    private String role;

    public BaseModelChatClient() {
    }

    public BaseModelChatClient(String role) {
        this.role = role;
    }

    public abstract String getModel();

    public abstract String getType();

    public abstract String getApiUrl();

    public abstract String getApiKey();

    public static void main(String[] args) throws Exception {
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
                "3000字以上，请保证故事有头有尾，,必须要有一个完整的结局,内容里面尽量不要重复，不要完全脱离历史,开头参考现有的经典悬疑小说开头范例,该文章要有一个题目用<h1></h1>包裹"
        );

        String role = "你是一名思想天马行空的资深悬疑小说作家，你擅长构思精妙绝伦的悬疑故事，并拥有独特的工作步骤来完成构思";
        String question = "根据提示，写一个故事，提示: 三国里有个吃人的大汉天子";
        String result = new OllamaClient().chat(rules, question, role, 3);
        log.info("最终结果: \n{}", result);
    }

    public String chat(List<String> rules, String question, String role, int checkNum) {
        BaseModelChatClient mainUser = ModelManager.getModel(getType(), role);
        String mainUserAnswer = "";
        String finalQuestion = "";
        for (int i = 0; i < checkNum; i++) {
            StringBuffer finalQuestionSb = new StringBuffer(question).append("\n你的回答需要满足下面规则:\n");
            for (int k = 0; k < rules.size(); k++) {
                finalQuestionSb.append((k + 1) + ". " + rules.get(k)).append("\n");
            }
            finalQuestion = finalQuestionSb.toString();

            SeekFileUtils.writeLines(file, Arrays.asList(String.format("问 [%s] : \n%s\n", mainUser.getRole(), finalQuestion)), true);
            mainUserAnswer = mainUser.chat(finalQuestion);
            SeekFileUtils.writeLines(file, Arrays.asList(String.format("[%s] 答: \n%s\n", mainUser.getRole(), mainUserAnswer)), true);
            //获取建议
            String suggestion = getSuggestion(rules,mainUserAnswer,role);
            question = String.format("你之前的答案是:%s\n,请根据我另外一个专家朋友的建议:%s\n" +
                            "请在原答案的基础上优化内容，如偏差较大可以重新生成"
                    , mainUserAnswer, suggestion);
        }
        return mainUserAnswer;
    }

    private String getSuggestion(List<String> rules,String mainUserAnswer,String role){
        StringBuffer suggestion = new StringBuffer();
        List<Callable<String>> tasks = new ArrayList<>();
        for (int j = 0; j < rules.size(); j++) {
            int index = j;
            String finalMainUserAnswer = mainUserAnswer;
            tasks.add(() -> {
                // 每个任务内执行：获取模型 -> 构建问题 -> 调用 chat 方法
                BaseModelChatClient checkUser = ModelManager.getModel(getType(), role + (index + 1));
                String checkQuestion = String.format(
                        "你是一个%s,请检查下面的内容:%s\n" +
                                "是否满足规则: %s,请记住：满足则返回\"\",不要回复其他字符，不满足从专业的角度给出对应的建议给出对应的1-3条建议,要求在300字内。请记住，不要扩散。" +
                                "不展示任何思考过程、推导步骤或解释性前言，普通文本即可，不要加上html或者markdown等语法",
                        role, finalMainUserAnswer, rules.get(index)
                );
                // 执行 chat 调用并返回结果（若执行异常，此处会抛出）
                return checkUser.chat(checkQuestion);
            });
        }
        try {
            // 3. 提交所有任务并获取 Future 列表（Future 用于接收异步结果）
            List<Future<String>> futureList = executor.invokeAll(tasks);

            // 4. 遍历 Future 列表，获取每个任务的结果并拼接到 suggestion
            for (Future<String> future : futureList) {

                // 获取结果（若任务未完成，会阻塞直到完成；若任务异常，会抛出 ExecutionException）
                String checkResult = future.get();
                // 拼接结果（每个结果后加换行，与原逻辑一致）
                if (checkResult != null && !checkResult.isEmpty()) {
                    suggestion.append(checkResult).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("error===>", e);
        }
        return suggestion.toString();
    }

    public String chat(String question) {
        log.info("问题: " + question);
        question = question + "/no_think";
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", role == null ? "你是一个高级AI助手,迅速回答用户问题" : role));
        messages.add(new ChatMessage("user", question));
        String chat = chat(messages);
        log.info("答案: " + chat);
        return chat;
    }


    public String chat(String question, String ragInfo) {
        question = question + "/no_think";
        log.info("问题: " + question);
        log.info("ragInfo: " + ragInfo);
        if (ragInfo == null) {
            return chat(question);
        }
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", ragInfo));
        messages.add(new ChatMessage("user", question));
        String result = chat(messages);
        log.info("答案: " + result);
        return result;
    }

    public String chat(List<ChatMessage> messages) {
        try {
            // 构建Ollama请求体，包含模型名、消息和流式响应设置
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("model", getModel());
            requestBodyJson.put("messages", messages);
            requestBodyJson.put("stream", false); // 设置为false，获取完整响应，而非流式输出

            String requestBody = requestBodyJson.toString();
            //log.info("请求体：\n" + JSON.toJSONString(JSON.parseObject(requestBody), true));

            // 3. 创建HttpClient并发送POST请求
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl()))
                    // 无需Authorization头
                    .header("Authorization", "Bearer " + getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 4. 发送请求并获取响应
            log.info("\n正在请求模型: " + getModel() + " ...");
            HttpResponse<String> response = httpClient.
                    send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // 5. 处理响应结果
            return handleResponse(response);

        } catch (Exception e) {
            log.error("API请求异常：" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 处理Ollama API响应，提取模型生成的内容
     */
    public String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        String responseBody = response.body();
        if (statusCode == 200) {
            String answer = getAnswer(responseBody);
            int lastSlashIndex = answer.lastIndexOf("</think>");
            if (lastSlashIndex != -1) {
                answer = answer.substring(lastSlashIndex + 8);
            }
            return answer;
        } else if (statusCode == 404) {
            // 404错误通常意味着模型未找到
            log.error("请求失败，状态码：" + statusCode);
            log.error("最可能的原因是模型未在本地找到");
        } else {
            log.error("API请求失败，状态码：" + statusCode);
        }
        return "";
    }

    public String getAnswer(String responseBody) {
        JSONObject jsonObject = JSON.parseObject(responseBody);
        // 直接从根节点获取message对象
        JSONObject messageObject = jsonObject.getJSONObject("message");
        if (messageObject != null) {
            return messageObject.getString("content");
        } else {
            log.error("响应中无有效message字段");
        }
        return "";
    }
}