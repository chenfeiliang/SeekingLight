package seekLight.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import seekLight.dao.impl.PluginTransInfoDaoImpl;
import seekLight.entity.PluginTransInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CacheUtils{
    public static Set<String> questionSet= new HashSet<>();

    public static void init(){
        PluginTransInfoDaoImpl bean = SpringUtils.getBean(PluginTransInfoDaoImpl.class);
        List<PluginTransInfo> pluginTransInfos = bean.listOrderTime();
        pluginTransInfos.forEach(item->{
            JSONObject jsonObject = JSON.parseObject(item.getContent());
            String questionId = jsonObject.getString("zhiHuPublish_questionId");
            questionSet.add(questionId);
        });
        System.out.println(questionSet);
    }
}
