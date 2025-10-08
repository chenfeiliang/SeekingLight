package seekLight.service.toutiao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.brotli.dec.BrotliInputStream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ToutiaoPublishAnswerJsoup {

    // -------------------------- 需替换的核心参数（必须从抓包获取） --------------------------
    // 1. 浏览器登录后的完整Cookie（过期后需重新捕获）
    private static final String COOKIE = "passport_csrf_token=73de2880e1760a7c29e8169b3b951498; gfkadpd=1231,25897; s_v_web_id=verify_mggdwdnp_Sb1qWBFg_7xNa_403p_9haQ_bhV4ovSc6uRJ; n_mh=1Yt03TZHzemOV0C42-TJPDVAJR-nUsvSaY3pSVSt6bU; sso_uid_tt=fd6d042e2e87156c1918a3258fd510d6; sso_uid_tt_ss=fd6d042e2e87156c1918a3258fd510d6; toutiao_sso_user=b2108cbecea70f58f7ae3e85338d2620; toutiao_sso_user_ss=b2108cbecea70f58f7ae3e85338d2620; sid_ucp_sso_v1=1.0.0-KGZjYjhkYTA0YzMwZDU5ZWY4OGIxNjBjY2M4MzhmMjE5Y2Q1OWJmN2EKHginr9CXzY2jBRDQx5PHBhjPCSAMMNfetJ4GOAhAJhoCaGwiIGIyMTA4Y2JlY2VhNzBmNThmN2FlM2U4NTMzOGQyNjIw; ssid_ucp_sso_v1=1.0.0-KGZjYjhkYTA0YzMwZDU5ZWY4OGIxNjBjY2M4MzhmMjE5Y2Q1OWJmN2EKHginr9CXzY2jBRDQx5PHBhjPCSAMMNfetJ4GOAhAJhoCaGwiIGIyMTA4Y2JlY2VhNzBmNThmN2FlM2U4NTMzOGQyNjIw; passport_auth_status=9a694a4979ae209f55d73811a11420b2%2C91fc025d2420181477082456ce086101; passport_auth_status_ss=9a694a4979ae209f55d73811a11420b2%2C91fc025d2420181477082456ce086101; sid_guard=3e88a7e9d9d49b8509bb6cf23340c647%7C1759830992%7C5184002%7CSat%2C+06-Dec-2025+09%3A56%3A34+GMT; uid_tt=28991bf4f6e4b2861f44d94d3a71879c; uid_tt_ss=28991bf4f6e4b2861f44d94d3a71879c; sid_tt=3e88a7e9d9d49b8509bb6cf23340c647; sessionid=3e88a7e9d9d49b8509bb6cf23340c647; sessionid_ss=3e88a7e9d9d49b8509bb6cf23340c647; session_tlb_tag=sttt%7C2%7CPoin6dnUm4UJu2zyM0DGR_________-kFEnfC7T4dZI636vbCMMM-iUoQW6koCd8Se5j0wfcRjU%3D; is_staff_user=false; sid_ucp_v1=1.0.0-KDJkODVmZjQwOWI3OTI3N2NiZDQ4ODg2MWRmZGZhODVlM2MwMjA3ODYKGAinr9CXzY2jBRDQx5PHBhjPCSAMOAhAJhoCbHEiIDNlODhhN2U5ZDlkNDliODUwOWJiNmNmMjMzNDBjNjQ3; ssid_ucp_v1=1.0.0-KDJkODVmZjQwOWI3OTI3N2NiZDQ4ODg2MWRmZGZhODVlM2MwMjA3ODYKGAinr9CXzY2jBRDQx5PHBhjPCSAMOAhAJhoCbHEiIDNlODhhN2U5ZDlkNDliODUwOWJiNmNmMjMzNDBjNjQ3; ttcid=9dbe33ab0f0748c881208e149f25bae034; odin_tt=26779254dc9a380c74c4e2145c5551c80560d9df21c64e9a2bebcd614e3087f630b1f22cc775749c1a59538ad0c3ccbc; _ga=GA1.2.860931983.1759831318; _ga_1Y7TBPV8DE=GS2.1.s1759831318$o1$g0$t1759831323$j55$l0$h0; csrf_session_id=724cdf1e3def9493cc172dc90b6ff041; tt_scid=N7gJAUmTks-kYvV2PsQ3SBSyj8kOuMBuaLtSjRNNIy3Xpju7CJC2qPLQ.IM0MOOu0287; ttwid=1%7CH7lEJRAAhRzQiu8Wm_TFf6jFxG8HYnlpSkZmEDXXs-4%7C1759936045%7C3651f48b68497c5a0378be56577b541b6bf9e2fe4df5b0e47ce79e9c144c60e1";

    // 2. 防篡改令牌（从抓包的Request Headers中获取tt-anti-token）
    private static final String TT_ANTI_TOKEN = "EUtypJAEb0nG-41fe79b715467ed3f8645278196845b0404763c09cd862c8ea3522977efb429b";

    // 3. CSRF防护令牌（与Cookie绑定，从抓包获取x-secsdk-csrf-token）
    private static final String X_SECSDK_CSRF_TOKEN = "000100000001c6143c7648104a926a20300e323193a3cae3a48b0556e3958e7753491b47ab9e186c8c821c8b9dab";

    // 4. 问题ID（需发布回答的目标问题ID，从抓包的表单参数qid获取）
    private static final String TARGET_QID = "6424771228849930498";
    // -------------------------------------------------------------------------------------

    // 固定请求配置（与抓包一致，无需修改）
    private static final String PUBLISH_URL = "https://mp.toutiao.com/mp/agw/wenda/publish_answer?app_id=1231";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36";
    private static final String ORIGIN = "https://mp.toutiao.com";
    private static final String REFERER = "https://mp.toutiao.com/profile_v4/wenda/answer?qid=6424771228849930498&group_id=6424771228849930498&enter_from=click_category&category_name=%E5%85%A8%E9%83%A8%E6%8E%A8%E8%8D%90&from=%2Fwenda%2Fhome%2Frecommended";

    public static void main(String[] args) {

    }

    public static String publish(String questionId,String content){
        try {
            // 1. 构建发布回答的表单参数（与抓包的Form Data完全一致）
            Map<String, String> formData = buildPublishFormData(questionId,content);

            // 2. 配置Jsoup请求（关键：表单提交+忽略MIME类型）
            Connection connection = Jsoup.connect(PUBLISH_URL)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true) // 忽略响应MIME类型，避免text/plain拦截
                    .ignoreHttpErrors(true)   // 忽略HTTP错误（如400参数错误）
                    // 设置请求头（严格匹配抓包结果，缺一不可）
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Encoding", "gzip, deflate, br, zstd")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Content-Type", "application/x-www-form-urlencoded") // 表单提交标识
                    .header("Cookie", ToutiaoRecommendQuestionJsoup.COOKIE)
                    .header("Origin", ORIGIN)
                    .header("Referer", REFERER)
                    .header("Sec-Ch-Ua", "\"Google Chrome\";v=\"143\", \"Chromium\";v=\"143\", \"Not A(Brand\";v=\"24\"")
                    .header("Sec-Ch-Ua-Mobile", "?0")
                    .header("Sec-Ch-Ua-Platform", "\"Windows\"")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "same-origin")
                    .header("tt-anti-token", TT_ANTI_TOKEN) // 防篡改令牌，关键参数
                    .header("User-Agent", USER_AGENT)
                    .header("X-Secsdk-Csrf-Token", X_SECSDK_CSRF_TOKEN) // CSRF令牌，关键参数
                    // 设置表单参数（Jsoup会自动编码为x-www-form-urlencoded格式）
                    .data(formData)
                    // 超时时间
                    .timeout(15000)
                    // 忽略SSL证书校验（解决HTTPS问题）
                    .sslSocketFactory(ToutiaoRecommendQuestionJsoup.SSLUtils.createSSLSocketFactory());

            // 3. 发送请求并获取响应
            Connection.Response response = connection.execute();
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发布回答异常：" + e.getMessage());
        }
        return "";
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
     * 构建发布回答的表单参数（与抓包的Form Data完全一致）
     */
    private static Map<String, String> buildPublishFormData(String questionId,String content) {
        Map<String, String> formData = new HashMap<>();
        //formData.put("app_id", "1231"); // 固定参数，与URL中的app_id一致
        formData.put("content", content); // 回答内容（支持HTML标签）
        formData.put("qid", questionId); // 目标问题ID，需替换为实际值
        formData.put("praise", "0"); // 是否点赞（0=不点赞，1=点赞，固定参数）
        formData.put("origin_source", "mp_wenda_tab"); // 来源标识，固定参数
        formData.put("source", "全部推荐"); // 来源分类，固定参数
        return formData;
    }
}
