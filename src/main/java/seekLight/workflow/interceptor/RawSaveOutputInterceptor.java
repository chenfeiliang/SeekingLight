package seekLight.workflow.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.dao.PluginInfoDao;
import seekLight.dao.RawFlowInfoDao;
import seekLight.entity.PluginInfo;
import seekLight.entity.RawFlowInfo;
import seekLight.workflow.context.WorkFlowContext;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class RawSaveOutputInterceptor implements PluginInterceptor<WorkFlowContext, Object>{
    @Autowired
    private RawFlowInfoDao rawFlowInfoDao;
    @Autowired
    private PluginInfoDao pluginInfoDao;
    @Override
    public void afterFetch(WorkFlowContext flow, Object data) {
        try {
            PluginInfo pluginInfo = pluginInfoDao.getById(flow.getStep());
            if(Objects.nonNull(pluginInfo)&& StringUtils.isNotEmpty(pluginInfo.getOutput())){
                List<String> outputKeys = Arrays.asList(pluginInfo.getOutput().split(","));
                Map<String,String> map = new HashMap<>();
                for(String key: flow.getFlowParams().keySet()){
                    if(outputKeys.contains(key)){
                        map.put(key, flow.getParam(key));
                    }
                }
                RawFlowInfo rawFlowInfo = new RawFlowInfo();
                rawFlowInfo.setBusiSno(flow.getBusiSno());
                rawFlowInfo.setType(flow.getStep());
                rawFlowInfo.setOutputContent(JSON.toJSONString(map));
                rawFlowInfo.setUpdateTime(new Date());
                rawFlowInfoDao.saveOrUpdateByMultiId(rawFlowInfo);
                log.info("【{}】【{}】保存出参成功:",flow.getBusiSno(),flow.getStep());
            }
        }catch (Exception ex){
            log.info("【{}】【{}】保存出参异常,error===>",flow.getBusiSno(),flow.getStep(),ex);
        }
    }
}
