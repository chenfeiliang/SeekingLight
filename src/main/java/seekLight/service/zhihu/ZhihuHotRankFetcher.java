package seekLight.service.zhihu;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.brotli.dec.BrotliInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import seekLight.entity.WorkFlow;
import seekLight.utils.SnowflakeUtils;
import seekLight.utils.SpringUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Slf4j
public class ZhihuHotRankFetcher {
    private static WorkFlowCreditEngine workFlowCreditEngine;
    static {
        try {
            workFlowCreditEngine = SpringUtils.getBean(WorkFlowCreditEngine.class);
        }catch (Exception ex){
            log.error("error==>");
        }
    }

    public static void list(){
        // 1. 目标 API URL
        String url = "https://www.zhihu.com/api/v4/creators/rank/hot";

        try {
            // 2. 创建 Jsoup 连接
            Connection connection = Jsoup.connect(url)
                    .ignoreContentType(true)
                    // 设置请求方法为 GET (Jsoup 默认就是 GET)
                    .method(Connection.Method.GET)
                    // 设置请求参数
                    .data("domain", "0")
                    .data("period", "hour")

                    // 3. 设置关键请求头 (Headers)
                    // 必须严格按照浏览器中的请求头设置，这是反爬的关键
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br, zstd")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Cache-Control", "no-cache")
                    .header("Connection", "keep-alive")

                    // --- 核心 Headers ---
                    // 1. Cookie: 包含你的登录凭证，必须有效
                    .header("Cookie", ZhihuApiFetcher.COOKIE) // <-- 请替换为你自己的完整Cookie

                    // 2. User-Agent: 模拟浏览器身份
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Mobile Safari/537.36")

                    // 3. Referer: 表明请求来源页面
                    .header("Referer", "https://www.zhihu.com/creator/hot-question/hot/0/hour")

                    // 4. 知乎特定加密头: 非常重要，直接复制即可
                    .header("x-zse-93", "101_3_3.0")
                    .header("x-zse-96", ZhihuApiFetcher.X_ZSE_96)
                    .header("x-requested-with", "fetch")

                    // 其他安全相关的 Headers
                    .header("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                    .header("sec-ch-ua-mobile", "?1")
                    .header("sec-ch-ua-platform", "\"Android\"")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin");

            // 4. 执行请求并获取响应
            // 使用 execute() 而不是 get()，因为我们需要的是原始 JSON 字符串，而不是解析成 Document
            Connection.Response response = connection.execute();



            // 5. 检查响应状态
            if (response.statusCode() == 200) {
                System.out.println("请求成功！");

                // 2. 获取 Brotli 压缩的字节数组
                byte[] compressedBytes = response.bodyAsBytes();

                // 3. 手动解压 Brotli 压缩数据
                byte[] decompressedBytes = decompressBrotli(compressedBytes);

                // 4. 用 UTF-8 解码解压后的字节数组，得到正常 JSON 字符串
                String jsonResponse = new String(decompressedBytes, StandardCharsets.UTF_8);

                System.out.println("返回的JSON数据:");
                System.out.println(jsonResponse);

                // 接下来，你可以使用任何JSON库（如Jackson, Gson, Fastjson）来解析这个jsonResponse字符串
                // 例如：
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonResponse);
                JsonNode data = rootNode.get("data");
                for(int i = 0 ;i<data.size();i++){
                    JsonNode question = data.get(i).get("question");
                    String qid = ZhihuApiFetcher.getQuestionId(question.get("url").asText());
                    String title = question.get("title").asText();
                    log.info("qid:{},title:{}",qid,title);
                    WorkFlow workFlow = new WorkFlow();
                    workFlow.setBusiSno( SnowflakeUtils.nextId());
                    workFlow.setStep("");
                    workFlow.setRoute("judgeType,zhihuGenerator,zhiHuPublish");//
                    WorkFlowContext flowContext = new WorkFlowContext(workFlow);
                    flowContext.putParam("zhiHuGenerator_title", title);
                    flowContext.putParam("zhiHuPublish_questionId",qid);
                    workFlowCreditEngine.doFlow(flowContext);
                }
            } else {
                System.err.println("请求失败，状态码: " + response.statusCode());
                System.err.println("响应信息: " + response.statusMessage());
                System.err.println("响应体: " + response.body()); // 打印错误信息，可能包含验证码或登录提示
            }

        } catch (IOException e) {
            System.err.println("网络请求异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }


    /**
     * Brotli 手动解压方法：将压缩的字节数组解压为原始字节数组
     * @param compressedBytes 未解压的 Brotli 压缩字节数组
     * @return 解压后的原始字节数组
     * @throws IOException 解压过程中的IO异常
     */
    private static byte[] decompressBrotli(byte[] compressedBytes) throws IOException {
        // 字节数组输入流：读取压缩数据
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedBytes);
             // Brotli 输入流：处理解压逻辑
             BrotliInputStream bris = new BrotliInputStream(bis);
             // 字节数组输出流：存储解压后的原始数据
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024]; // 缓冲数组，提高读写效率
            int len;
            // 循环读取解压后的字节数据，写入输出流
            while ((len = bris.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            baos.flush(); // 确保所有数据写入完成
            return baos.toByteArray(); // 返回解压后的原始字节数组
        }
    }
}