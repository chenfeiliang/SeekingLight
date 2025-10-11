package seekLight.service.zhihu;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.brotli.dec.BrotliInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import seekLight.agent.Tool;
import seekLight.dto.QuestionDto;
import seekLight.entity.WorkFlow;
import seekLight.utils.SnowflakeUtils;
import seekLight.utils.SpringUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class ZhihuHotListCrawler implements Tool {
    // 知乎热榜接口URL
    private static final String ZHIHU_HOT_LIST_URL = "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50&desktop=true";

    private static WorkFlowCreditEngine workFlowCreditEngine;
    static {
        try {
            workFlowCreditEngine = SpringUtils.getBean(WorkFlowCreditEngine.class);
        }catch (Exception ex){
            log.error("error==>");
        }
    }


    @Override
    public String getName() {
        return "知乎热榜查询工具";
    }

    @Override
    public String getDescription() {
        return "知乎热榜查询工具";
    }

    @Override
    public String execute(String args) {
        return list().toString();
    }

    public static void main(String[] args) {
        list();
    }

    public static List<QuestionDto>  list ()  {
        try {
        // 1. 构建请求头（关键：复现浏览器的请求头，尤其是Cookie和User-Agent）
        Map<String, String> headers = buildHeaders();

        // 2. 发送GET请求（Jsoup的connect方法）
        Connection.Response response = Jsoup.connect(ZHIHU_HOT_LIST_URL)
                .headers(headers) // 设置请求头
                .ignoreContentType(true) // 关键：忽略内容类型，避免Jsoup尝试解析HTML（接口返回JSON）
                .timeout(10000) // 超时时间（10秒）
                .method(Connection.Method.GET)
                .execute(); // 执行请求，获取响应

        // 3. 处理响应（接口返回JSON字符串）
        // 在 .execute() 之后立即添加
        System.out.println("响应状态码: " + response.statusCode());
        System.out.println("Content-Type: " + response.header("Content-Type"));
        System.out.println("Content-Encoding: " + response.header("Content-Encoding"));
        // 2. 获取 Brotli 压缩的字节数组
        byte[] compressedBytes = response.bodyAsBytes();

        // 3. 手动解压 Brotli 压缩数据
        byte[] decompressedBytes = decompressBrotli(compressedBytes);

        // 4. 用 UTF-8 解码解压后的字节数组，得到正常 JSON 字符串
        String jsonResponse = new String(decompressedBytes, StandardCharsets.UTF_8);

        // 4. 解析JSON（使用Jackson，可选，根据需求提取数据）
        return parseHotListJson(jsonResponse);
        } catch (Exception e) {
            System.err.println("请求失败：" + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
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

    /**
     * 构建请求头：从浏览器开发者工具中复制真实请求头
     */
    private static Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();

        // 1. 核心头：User-Agent（模拟Chrome浏览器）
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36");

        // 2. 核心头：Cookie（从浏览器中复制你的Cookie，替换下方内容！）
        headers.put("Cookie", ZhihuApiFetcher.COOKIE);

        // 3. 知乎接口特有头：x-zse-93、x-zse-96（从浏览器请求头复制，不可缺少）
        headers.put("x-zse-93", "101_3_3.0");
        headers.put("x-zse-96", ZhihuApiFetcher.X_ZSE_96);
        headers.put("x-api-version", "3.0.76");
        headers.put("x-requested-with", "fetch");

        // 4. 其他辅助头（从浏览器复制，可选但建议添加，提高真实性）
        headers.put("accept", "*/*");
        headers.put("accept-encoding", "gzip, deflate, br, zstd");
        headers.put("accept-language", "zh-CN,zh;q=0.9");
        headers.put("referer", "https://www.zhihu.com/hot");
        headers.put("sec-ch-ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"");
        headers.put("sec-ch-ua-mobile", "?0");
        headers.put("sec-ch-ua-platform", "\"Windows\"");
        headers.put("sec-fetch-dest", "empty");
        headers.put("sec-fetch-mode", "cors");
        headers.put("sec-fetch-site", "same-origin");
        headers.put("referrer-policy", "no-referrer-when-downgrade");

        return headers;
    }

    /**
     * 解析热榜JSON数据，提取关键信息（如排名、标题、热度）
     */
    private static List<QuestionDto>  parseHotListJson(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // 从JSON中提取热榜列表（根据接口返回结构调整路径，需查看实际响应）
        JsonNode dataNode = rootNode.get("data");
        if (dataNode == null || !dataNode.isArray()) {
            System.err.println("热榜数据不存在或格式错误");
            return new ArrayList<>();
        }

        // 遍历热榜数据，提取关键信息
        List<QuestionDto> result = new ArrayList<>();
        log.info("===== 知乎热榜TOP50 =====");
        for (int i = 0; i < dataNode.size(); i++) {
            try {
                JsonNode itemNode = dataNode.get(i);
                // 提取排名（根据实际JSON结构调整，可能在itemNode的某个子节点中）
                JsonNode target =  itemNode.get("target");
                // 提取标题（热榜内容的标题，路径需根据实际响应调整）
                String title = target.get("title_area").get("text").asText();
                // 提取热度（如"100万+ 热度"，路径需根据实际响应调整）
                String metrics_area = target.get("metrics_area").get("text").asText();
                //详情
                String detail = target.get("excerpt_area").get("text").asText();
                //questionId
                String questionId = ZhihuApiFetcher.getQuestionId(target.get("link").get("url").asText());
                // 输出结果
                log.info("【{}】questionId:{},title:{},热度:{},detail:{}",i+1,questionId,title,metrics_area,detail);
                WorkFlow workFlow = new WorkFlow();
                workFlow.setBusiSno( SnowflakeUtils.nextId());
                workFlow.setStep("");
                workFlow.setRoute("judgeType,zhihuGenerator");//,zhiHuPublish
                WorkFlowContext flowContext = new WorkFlowContext(workFlow);
                flowContext.putParam("zhiHuGenerator_title", title);
                flowContext.putParam("zhiHuPublish_questionId",questionId);
                workFlowCreditEngine.doFlow(flowContext);
                result.add(new QuestionDto(questionId,title));
            }catch (Exception ex){
                log.error("error==>",ex);
            }
        }
        return result;
    }
}