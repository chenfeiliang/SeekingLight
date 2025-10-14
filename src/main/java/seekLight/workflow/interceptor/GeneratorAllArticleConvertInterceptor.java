package seekLight.workflow.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import seekLight.config.ThreadPools;
import seekLight.dto.ArticleDto;
import seekLight.service.model.DeepSeekClient;
import seekLight.workflow.context.WorkFlowContext;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Slf4j

public class GeneratorAllArticleConvertInterceptor implements PluginInterceptor<WorkFlowContext, Map<String, String>> {

    @Override
    public void afterFetch(WorkFlowContext flow, Map<String, String> data) throws Exception {
        List<String> results = JSONArray.parseArray(data.get("GeneratorAllArticle_Content"),String.class);
        //细纲
        String detailedOutlineContent = data.get("GenerateDetailedOutline_content");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(detailedOutlineContent);
        //[{"第X章":{"章节标题":"","章节内容":""}}]
        List<ArticleDto> articleDtos = results.stream().map(sectionStr -> {
            try {
                JSONObject jsonObject = JSON.parseObject(sectionStr);
                String sectionNo = new ArrayList<>(jsonObject.keySet()).get(0);
                JSONObject sectionValueJson = jsonObject.getJSONObject(sectionNo);
                String sectionTitle = sectionValueJson.getString("章节标题");
                String sectionContent = sectionValueJson.getString("章节内容");
                return new ArticleDto(sectionNo, sectionTitle, sectionContent);
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        StringBuffer article = new StringBuffer();
        article.append("<h1>").append(rootNode.get("文章标题").asText()).append("</h1>");
        for(ArticleDto articleDto : articleDtos){
            article.append("<h2>").append(articleDto.getSectionNo()+" "+articleDto.getSectionTitle()).append("</h2>");
            String sectionContent = articleDto.getSectionContent();
            int lastSlashIndex = sectionContent.lastIndexOf("</h1>");
            if (lastSlashIndex != -1) {
                sectionContent = sectionContent.substring(lastSlashIndex + 5);
            }
            article.append(sectionContent);
        }
        String sourceContent = article.toString();
        flow.putParam("GeneratorAllArticleConvert_sourceContent", sourceContent);
        String afterRemove = sourceContent.replaceAll("<p>", "").replaceAll("</p>", "\n");
        flow.putParam("GeneratorAllArticleConvert_Content", afterRemove);
    }
}

