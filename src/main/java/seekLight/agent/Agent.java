package seekLight.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import seekLight.dto.ChatMessage;
import seekLight.service.model.BaseModelChatClient;
import seekLight.service.model.OllamaClient;
import seekLight.service.zhihu.ZhihuAnswerPublisher;
import seekLight.service.zhihu.ZhihuHotListCrawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
//从 https://weibo.com/newlogin?tabtype=weibo&gid=102803&openLoginLayer=0&url=https%3A%2F%2Fweibo.com%2F 获取当前热搜前十的名称
//获取知乎热榜内容，并根据内容生成问题答案，并用知乎问题发布工具发布答案，返回ZhihuAnswerPublisher方法执行结果的字符串。
public class Agent {

    private final BaseModelChatClient llmClient;
    private final Memory memory;
    private final Map<String, Tool> tools;
    private final ObjectMapper mapper = new ObjectMapper();
    private final int memoryLimit = 50; // 限制记忆中消息的数量

    public static void main(String[] args) {
        // 2. 初始化组件
        BaseModelChatClient llmClient = new OllamaClient();
        Memory memory = new InMemoryMemory();
        List<Tool> tools = List.of(new CurrentTimeTool(), new SystemCommandTool(),new WebRequestTool(),new ZhihuHotListCrawler(),new ZhihuAnswerPublisher());

        // 3. 创建智能体
        Agent agent = new Agent(llmClient, memory, tools);

        // 4. 使用JLine创建交互式命令行
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(new DefaultParser())
                    .build();

            System.out.println("--- Java 人工智能体框架 ---");
            System.out.println("输入 'exit' 即可退出程序");
            System.out.println("你可以询问任何问题，我可使用 'current_time'（获取当前时间）或 'system_command'（执行系统命令）等工具");
            System.out.println("--------------------------");

            while (true) {
                String userInput;
                try {
                    userInput = reader.readLine("请输入：");
                } catch (UserInterruptException e) {
                    // 捕获 Ctrl-C 中断信号
                    break;
                } catch (EndOfFileException e) {
                    // 捕获 Ctrl-D 结束信号
                    break;
                }

                if (userInput == null || userInput.equalsIgnoreCase("exit")) {
                    break;
                }

                if (userInput.trim().isEmpty()) {
                    continue;
                }

                // 5. 运行智能体并获取回答
                String response = agent.run(userInput);

                // 6. 打印智能体的回答
                System.out.println("\n智能体：" + response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("再见！");
    }

    public Agent(BaseModelChatClient llmClient, Memory memory, List<Tool> tools) {
        this.llmClient = llmClient;
        this.memory = memory;
        this.tools = tools.stream().collect(Collectors.toMap(Tool::getName, tool -> tool));
    }

    /**
     * 运行智能体的主循环（感知-思考-行动）
     * @param userInput 用户的初始输入内容
     * @return 智能体的最终回答
     */
    public String run(String userInput) {
        // 1. 将用户输入存入记忆系统
        memory.addMessage("user", userInput);

        // 2. 主循环：思考 -> 行动 -> 观察结果 -> 更新记忆
        while (true) {
            // 3. 准备发送给大语言模型（LLM）的系统提示词
            String systemPrompt = createSystemPrompt();
            List<String> recentMessages = memory.getRecentMessages(memoryLimit);
            List<ChatMessage> chatMessages = recentMessages.stream()
                    .map(content -> new ChatMessage("user", content)).collect(Collectors.toList());
            chatMessages.add(new ChatMessage("system", systemPrompt));

            // 4. 调用大语言模型，获取思考结果
            String llmResponse = llmClient.chat(chatMessages);

            // 5. 检查LLM是否决定调用工具（通过JSON格式判断）
            if (llmResponse.trim().startsWith("{")) {
                try {
                    JsonNode toolCall = mapper.readTree(llmResponse);
                    String toolName = toolCall.path("action").asText();
                    String toolArgs = toolCall.path("args").asText();

                    if (tools.containsKey(toolName)) {
                        System.out.println("智能体正在使用工具：" + toolName + "，参数：" + toolArgs);
                        // 6. 执行工具并获取结果
                        Tool tool = tools.get(toolName);
                        String toolResult = tool.execute(toolArgs);

                        // 7. 将工具执行结果存入记忆
                        memory.addMessage("tool", "工具：" + toolName + "，执行结果：" + toolResult);
                    } else {
                        String errorMsg = "未知工具：" + toolName;
                        System.out.println(errorMsg);
                        memory.addMessage("system", errorMsg);
                        // 工具不存在时，跳出循环并告知用户
                        return "很抱歉，我尝试使用的工具不存在，请您重新提问或检查工具配置。";
                    }
                } catch (Exception e) {
                    String errorMsg = "工具调用解析或执行失败：" + e.getMessage();
                    System.out.println(errorMsg);
                    memory.addMessage("system", errorMsg);
                    return errorMsg;
                }
            } else {
                // 8. 若LLM未调用工具，说明已生成直接回答，存入记忆后返回
                memory.addMessage("assistant", llmResponse);
                return llmResponse;
            }
        }
    }

    /**
     * 创建系统提示词（告知LLM其角色定位和可使用的工具）
     * @return 格式化的中文系统提示词
     */
    private String createSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个乐于助人的人工智能助手，目前可使用以下工具：\n\n");

        for (Tool tool : tools.values()) {
            sb.append("- 工具名称：'").append(tool.getName()).append("'\n");
            sb.append("  工具描述：").append(tool.getDescription()).append("\n\n");
        }

        sb.append("若需要调用工具，请严格按照以下JSON格式返回结果：\n");
        sb.append("{\n");
        sb.append("  \"action\": \"<工具名称>\",\n");
        sb.append("  \"args\": \"<工具所需参数>\",\n");
        sb.append("}\n\n");
        sb.append("你可以按顺序多次调用工具，当通过工具获取到足够信息后，再用自然语言向用户提供最终回答。");

        return sb.toString();
    }
}