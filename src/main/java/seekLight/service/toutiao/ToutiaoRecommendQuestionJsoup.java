package seekLight.service.toutiao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.brotli.dec.BrotliInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import seekLight.entity.WorkFlow;
import seekLight.service.zhihu.ZhihuApiFetcher;
import seekLight.utils.SnowflakeUtils;
import seekLight.utils.SpringUtils;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.engine.impl.WorkFlowCreditEngine;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@Slf4j
public class ToutiaoRecommendQuestionJsoup {
    private static final Set<String> set = new HashSet<>();

    private static  int id = 1;

    // -------------------------- 需替换的核心参数 --------------------------
    // 1. 从浏览器捕获的Cookie（登录凭证，过期后需重新获取）
    public static final String COOKIE = "passport_csrf_token=73de2880e1760a7c29e8169b3b951498; gfkadpd=1231,25897; s_v_web_id=verify_mggdwdnp_Sb1qWBFg_7xNa_403p_9haQ_bhV4ovSc6uRJ; n_mh=1Yt03TZHzemOV0C42-TJPDVAJR-nUsvSaY3pSVSt6bU; passport_auth_status=9a694a4979ae209f55d73811a11420b2%2C91fc025d2420181477082456ce086101; passport_auth_status_ss=9a694a4979ae209f55d73811a11420b2%2C91fc025d2420181477082456ce086101; is_staff_user=false; ttcid=9dbe33ab0f0748c881208e149f25bae034; _ga_1Y7TBPV8DE=GS2.1.s1759831318$o1$g0$t1759831323$j55$l0$h0; csrf_session_id=724cdf1e3def9493cc172dc90b6ff041; tt_webid=7525853809529472564; _ga=GA1.1.860931983.1759831318; tt_scid=D6rKQXqK.I95lNIVdxj61O4trsApl9-G8rvExDWAd.cHvNuLuTr7f7YjEHSTnJ8u5507; sso_uid_tt=3a699c5690705850e1b5586aaefd4c0d; sso_uid_tt_ss=3a699c5690705850e1b5586aaefd4c0d; toutiao_sso_user=8659b842cec48f4bea8ffbeb30663c34; toutiao_sso_user_ss=8659b842cec48f4bea8ffbeb30663c34; sid_ucp_sso_v1=1.0.0-KDhjMjM5MjJmNGM3MDMyODVkMDcyN2UyZWFkMDc0MjdlMDVlZDkwYzkKFginr9CXzY2jBRDNzZrHBhjPCTgIQAsaAmhsIiA4NjU5Yjg0MmNlYzQ4ZjRiZWE4ZmZiZWIzMDY2M2MzNA; ssid_ucp_sso_v1=1.0.0-KDhjMjM5MjJmNGM3MDMyODVkMDcyN2UyZWFkMDc0MjdlMDVlZDkwYzkKFginr9CXzY2jBRDNzZrHBhjPCTgIQAsaAmhsIiA4NjU5Yjg0MmNlYzQ4ZjRiZWE4ZmZiZWIzMDY2M2MzNA; sid_guard=5006a5b905b22968a7a3da574f5fd672%7C1759946445%7C3024002%7CWed%2C+12-Nov-2025+18%3A00%3A47+GMT; uid_tt=4041de4e6920cb3d65f11a99e5476324; uid_tt_ss=4041de4e6920cb3d65f11a99e5476324; sid_tt=5006a5b905b22968a7a3da574f5fd672; sessionid=5006a5b905b22968a7a3da574f5fd672; sessionid_ss=5006a5b905b22968a7a3da574f5fd672; session_tlb_tag=sttt%7C4%7CUAaluQWyKWino9pXT1_Wcv________-x89WtFLxTFIQQOu7n7NxNRc9HqMy659dRl7bokJr20ps%3D; sid_ucp_v1=1.0.0-KDk0YWRmNmE1NzdiMTBkOGIzNGJmZjJmYjhjZjVkZmYyYmYwZmFlMzYKFwinr9CXzY2jBRDNzZrHBhgYIAw4CEALGgJobCIgNTAwNmE1YjkwNWIyMjk2OGE3YTNkYTU3NGY1ZmQ2NzI; ssid_ucp_v1=1.0.0-KDk0YWRmNmE1NzdiMTBkOGIzNGJmZjJmYjhjZjVkZmYyYmYwZmFlMzYKFwinr9CXzY2jBRDNzZrHBhgYIAw4CEALGgJobCIgNTAwNmE1YjkwNWIyMjk2OGE3YTNkYTU3NGY1ZmQ2NzI; odin_tt=a8e47a9f8951871dc5474d10808b59a0932af9c4a3d0785ec581d5dfcd98cfffb264ed64da7334cf9c779451cb80dbbf; _ga_QEHZPBE5HH=GS2.1.s1759940713$o2$g1$t1759950476$j60$l0$h0; ttwid=1%7CH7lEJRAAhRzQiu8Wm_TFf6jFxG8HYnlpSkZmEDXXs-4%7C1759950492%7Ce2666fbbc706bd5f34f0bc5cbcc92ab8f159b8a2b5ce0ba1930b6f9b113575be";

    // 2. 从浏览器捕获的x-secsdk-csrf-token（CSRF防护令牌，与Cookie绑定）
    public static final String X_SECSDK_CSRF_TOKEN = "000100000001142630c0ac4fb28a7ce433a5db77a88f42504440cc53eb1c1846eaeb6e4ab29c186c99a5f927e895";
    // ---------------------------------------------------------------------

    // 固定请求配置
    public static final String REQUEST_URL = "https://mp.toutiao.com/wenda/graph/recommend/question/list/";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36";
    public static final String ORIGIN = "https://mp.toutiao.com";
    public static final String REFERER = "https://mp.toutiao.com/profile_v4/wenda/home/recommended";

    // GraphQL固定查询语句（不可修改字段结构）
    public static final String GRAPHQL_QUERY = "query getQuestionList($concernId: String, $limit: Int, $questionType: QuestionType) {\n  questionCellList(\n    concernId: $concernId\n    limit: $limit\n    questionType: $questionType\n  ) {\n    questionCells {\n      question {\n        qid\n        title\n        action {\n          showCount\n          repinCount\n          userRepin\n        }\n        niceAnswerCount\n        text\n        wendaAction {\n          ansId\n        }\n      }\n    }\n    hasMore\n  }\n}";


    public static WorkFlowCreditEngine workFlowCreditEngine;

    static {
        try {
            workFlowCreditEngine = SpringUtils.getBean(WorkFlowCreditEngine.class);
        } catch (Exception ex) {
            log.error("error==>");
        }
    }

    public static void list() {
        Connection.Response response = null;
        try {
            // 1. 构建GraphQL请求体（确保JSON格式正确）
            String requestBody = buildGraphQLRequestBody();

            // 2. 配置Jsoup请求（核心：忽略MIME+获取字节流）
            Connection connection = Jsoup.connect(REQUEST_URL)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true) // 强制忽略MIME类型
                    .ignoreHttpErrors(true)   // 忽略HTTP错误（避免4xx/5xx直接抛异常）
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br") // 告诉服务器支持压缩
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Content-Type", "application/json; charset=utf-8") // 明确请求编码
                    .header("Cookie", COOKIE)
                    .header("Origin", ORIGIN)
                    .header("Referer", REFERER)
                    .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                    .header("Sec-Ch-Ua-Mobile", "?0")
                    .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("User-Agent", USER_AGENT)
                    .header("X-Secsdk-Csrf-Token", X_SECSDK_CSRF_TOKEN)
                    .requestBody(requestBody)
                    .timeout(15000) // 延长超时时间，避免网络波动导致解压中断
                    .sslSocketFactory(SSLUtils.createSSLSocketFactory());

            // 3. 发送请求并获取响应（关键：用bodyAsBytes()获取原始字节，避免编码损耗）
            response = connection.execute();




            byte[] responseBytes = response.bodyAsBytes(); // 全程用字节数组处理

            // 3. 手动解压 Brotli 压缩数据
            byte[] decompressedBytes = decompressBrotli(responseBytes);

            // 4. 用 UTF-8 解码解压后的字节数组，得到正常 JSON 字符串
            String jsonResponse = new String(decompressedBytes, StandardCharsets.UTF_8);

            // 4. 解析JSON（使用Jackson，可选，根据需求提取数据）
            parseHotListJson(jsonResponse);

        } catch (Exception e) {
            try {
                String body = response.body();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(body);
                int size = rootNode.get("data").get("questionCellList").get("questionCells").size();
                if (size == 0) {
                    log.info("没有查到数据,body:{}",body);
                    id++;
                    Thread.sleep(500);
                    return;
                }
                e.printStackTrace();
                System.out.println("模拟请求异常：" + e.getMessage());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * 解析热榜JSON数据，提取关键信息（如排名、标题、热度）
     */
    public static void parseHotListJson(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // 从JSON中提取热榜列表（根据接口返回结构调整路径，需查看实际响应）
        JsonNode dataNode = rootNode.get("data");
        JsonNode questionList = dataNode.get("questionCellList");
        JsonNode questionCells = questionList.get("questionCells");
        if (questionCells == null || !questionCells.isArray()) {
            System.err.println("数据不存在或格式错误");
            return;
        }

        // 遍历热榜数据，提取关键信息
        for (int i = 0; i < questionCells.size(); i++) {
            try {
                JsonNode itemNode = questionCells.get(i);
                // 提取排名（根据实际JSON结构调整，可能在itemNode的某个子节点中）
                JsonNode question = itemNode.get("question");
                String questionId = question.get("qid").asText();
                if(set.contains(questionId)){
                    continue;
                }
                String title = question.get("title").asText();
                // 输出结果
                log.info("【{}】questionId:{},title:{}", i + 1, questionId, title);
                WorkFlow workFlow = new WorkFlow();
                workFlow.setBusiSno(SnowflakeUtils.nextId());
                workFlow.setStep("");
                workFlow.setRoute("touTiaoGenerator,touTiaoPublish");//,touTiaoPublish
                WorkFlowContext flowContext = new WorkFlowContext(workFlow);
                flowContext.putParam("touTiaoGenerator_title", title);
                flowContext.putParam("touTiaoPublish_questionId", questionId);
                workFlowCreditEngine.doFlow(flowContext);
                set.add(questionId);
            } catch (Exception ex) {
                log.error("error==>", ex);
            }
        }
    }

    /**
     * Brotli 手动解压方法：将压缩的字节数组解压为原始字节数组
     *
     * @param compressedBytes 未解压的 Brotli 压缩字节数组
     * @return 解压后的原始字节数组
     * @throws IOException 解压过程中的IO异常
     */
    public static byte[] decompressBrotli(byte[] compressedBytes) throws IOException {
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
     * 安全解压Gzip：支持部分压缩/未压缩场景，避免解压失败
     */
    public static byte[] decompressGzipSafely(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             GZIPInputStream gis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096]; // 增大缓冲区，确保解压完全
            int len;
            while ((len = gis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos.toByteArray();

        } catch (IOException e) {
            // 若解压失败（说明数据未压缩），直接返回原始字节
            System.out.println("数据未压缩或解压失败，使用原始字节：" + e.getMessage().substring(0, 50));
            return bytes;
        }
    }

    /**
     * 检查字符串是否包含无效字符（65533/�）
     */
    public static boolean containsInvalidChar(String str) {
        return str.contains("\uFFFD"); // \uFFFD 即 � 字符
    }

    /**
     * 简单验证是否为有效JSON（避免解析非JSON数据抛异常）
     */
    public static boolean isValidJson(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        String trimStr = str.trim();
        return (trimStr.startsWith("{") && trimStr.endsWith("}"))
                || (trimStr.startsWith("[") && trimStr.endsWith("]"));
    }

    // -------------------------- 以下方法保持不变 --------------------------
    public static String buildGraphQLRequestBody() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("query", GRAPHQL_QUERY);

        Map<String, Object> variablesMap = new HashMap<>();
        variablesMap.put("limit", 100);
        variablesMap.put("concernId", "1");
        variablesMap.put("questionType", "WaitForMe");
        requestMap.put("variables", variablesMap);

        return objectMapper.writeValueAsString(requestMap);
    }

    public static void parseRecommendQuestions(String responseBody) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode dataNode = rootNode.path("data");
        JsonNode questionCellListNode = dataNode.path("questionCellList");
        JsonNode questionCellsNode = questionCellListNode.path("questionCells");

        if (questionCellsNode.isArray() && questionCellsNode.size() > 0) {
            System.out.println("\n===== 解析到 " + questionCellsNode.size() + " 条推荐问题 =====");
            for (int i = 0; i < questionCellsNode.size(); i++) {
                JsonNode questionNode = questionCellsNode.get(i).path("question");
                String qid = questionNode.path("qid").asText();
                String title = questionNode.path("title").asText();
                int showCount = questionNode.path("action").path("showCount").asInt();
                int niceAnswerCount = questionNode.path("niceAnswerCount").asInt();

                System.out.println("[" + (i + 1) + "]");
                System.out.println("问题ID：" + qid);
                System.out.println("问题标题：" + title);
                System.out.println("浏览量：" + showCount);
                System.out.println("优质回答数：" + niceAnswerCount);
                System.out.println("------------------------");
            }
        } else {
            System.out.println("\n未解析到推荐问题数据，可能是权限不足或无推荐内容");
        }
    }

    public static String formatJson(String jsonStr) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Object jsonObj = objectMapper.readValue(jsonStr, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
    }

    // SSL工具类：忽略证书校验（解决HTTPS问题）
    static class SSLUtils {
        public static javax.net.ssl.SSLSocketFactory createSSLSocketFactory() {
            try {
                javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLSv1.3"); // 强制使用TLSv1.3，提升兼容性
                sslContext.init(null, new javax.net.ssl.TrustManager[]{new javax.net.ssl.X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }}, new java.security.SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                throw new RuntimeException("创建SSL Socket Factory失败：" + e.getMessage(), e);
            }
        }
    }
}