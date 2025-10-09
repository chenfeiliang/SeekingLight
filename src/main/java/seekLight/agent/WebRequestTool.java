package seekLight.agent;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import seekLight.agent.Tool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 一个使用 Jsoup 实现的网页请求和解析工具。
 * 它不仅能获取网页内容，还能使用 CSS 选择器提取特定数据。
 */
public class WebRequestTool implements Tool {

    @Override
    public String getName() {
        return "jsoup_web_request"; // 工具名称保持唯一性
    }

    @Override
    public String getDescription() {
        return "使用 Jsoup 发送HTTP/HTTPS请求，获取并解析网页内容。" +
                "参数为一个JSON字符串，包含以下字段：" +
                " - url (必需): 目标URL。" +
                " - method (可选): 请求方法，默认为'GET'。Jsoup主要支持'GET'和'POST'。" +
                " - data (可选): 一个键值对对象，用于设置POST请求的数据或GET请求的查询参数。" +
                " - headers (可选): 一个键值对对象，用于设置请求头（如User-Agent）。" +
                " - timeout (可选): 连接和读取超时时间（毫秒），默认为10000。" +
                " - selector (可选): 一个CSS选择器字符串。如果提供，工具将返回匹配元素的文本内容，而不是整个HTML。" +
                " - returnType (可选): 当提供selector时有效。可选值为 'text' (默认) 或 'html'。'text'返回元素的文本，'html'返回元素的内部HTML。" +
                "示例1 (获取整个页面HTML): " +
                "{\"url\":\"https://www.bing.com\"}" +
                "示例2 (获取百度首页的标题): " +
                "{\"url\":\"https://www.baidu.com\", \"selector\":\"title\"}" +
                "示例3 (获取微博热搜榜的前10条): " +
                "{\"url\":\"https://s.weibo.com/top/summary\",\"selector\":\"#pl_top_realtimehot table tbody tr td.td-02 a\"}";
    }

    @Override
    public String execute(String args) {
        try {
            // 使用Jackson解析JSON参数
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode params = mapper.readTree(args);

            // 1. 提取URL (必需)
            String url = params.path("url").asText();
            if (url == null || url.isEmpty()) {
                return "错误：缺少必需的'url'参数。";
            }

            // 2. 构建 Jsoup 连接
            org.jsoup.Connection connection = Jsoup.connect(url);

            // 3. 设置请求方法 (默认为GET)
            String method = params.path("method").asText("GET").toUpperCase();

            // 4. 设置请求头 (如果有)
            com.fasterxml.jackson.databind.JsonNode headersNode = params.path("headers");
            if (headersNode != null && headersNode.isObject()) {
                headersNode.fieldNames().forEachRemaining(headerName -> {
                    String headerValue = headersNode.path(headerName).asText();
                    connection.header(headerName, headerValue);
                });
            }
            // 默认添加一个User-Agent，否则很多网站会拒绝请求或返回移动端页面
            if (headersNode == null || !headersNode.has("User-Agent")) {
                connection.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            }

            // 5. 设置超时时间 (默认为10秒)
            int timeout = params.path("timeout").asInt(10000);
            connection.timeout(timeout);

            // 6. 设置请求数据 (适用于POST或GET查询参数)
            com.fasterxml.jackson.databind.JsonNode dataNode = params.path("data");
            if (dataNode != null && dataNode.isObject()) {
                Map<String, String> dataMap = new HashMap<>();
                dataNode.fieldNames().forEachRemaining(key -> {
                    dataMap.put(key, dataNode.path(key).asText());
                });
                connection.data(dataMap);
            }

            // 7. 发送请求并获取 Document 对象
            Document doc;
            if ("POST".equals(method)) {
                doc = connection.post();
            } else { // GET 或其他方法
                doc = connection.get();
            }

            // 8. 检查是否需要使用 CSS 选择器提取内容
            String selector = params.path("selector").asText();
            if (selector != null && !selector.isEmpty()) {
                String returnType = params.path("returnType").asText("text").toLowerCase();
                Elements elements = doc.select(selector);

                if (elements.isEmpty()) {
                    return "CSS选择器 '" + selector + "' 未匹配到任何元素。";
                }

                if ("html".equals(returnType)) {
                    // 返回匹配元素的内部HTML
                    return elements.stream()
                            .map(Element::html)
                            .collect(Collectors.joining("\n\n--- 元素分隔线 ---\n\n"));
                } else {
                    // 默认返回匹配元素的文本内容
                    return elements.stream()
                            .map(Element::text)
                            .collect(Collectors.joining("\n"));
                }
            } else {
                // 如果没有提供选择器，返回整个文档的 HTML
                return doc.html();
            }

        } catch (IOException e) {
            return "网络请求或解析失败：" + e.getMessage();
        } catch (Exception e) {
            return "工具执行出错：" + e.getMessage();
        }
    }
}