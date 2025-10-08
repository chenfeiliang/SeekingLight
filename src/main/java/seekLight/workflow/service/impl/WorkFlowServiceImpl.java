package seekLight.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.dao.PluginTransInfoDao;
import seekLight.dao.WorkFlowDao;
import seekLight.entity.PluginTransInfo;
import seekLight.entity.WorkFlow;
import seekLight.workflow.context.WorkFlowContext;
import seekLight.workflow.service.FlowService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WorkFlowServiceImpl implements FlowService<WorkFlowContext> {
    @Autowired
    private WorkFlowDao workFlowDao;
    @Autowired
    private PluginTransInfoDao pluginTransInfoDao;

    @Override
    public WorkFlowContext getFlow(String busiSno) {
        WorkFlow workFlow = workFlowDao.getById(busiSno);
        if(Objects.isNull(workFlow)){
            return null;
        }
        PluginTransInfo transInfo = pluginTransInfoDao.getById(busiSno);
        Map<String, String> params = new HashMap<>();
        if(Objects.nonNull(transInfo)){
            String content = transInfo.getContent();
            params = JSON.parseObject(content, new TypeReference<ConcurrentHashMap<String, String>>() {});
        }
        WorkFlowContext workFlowContext = new WorkFlowContext(workFlow,params);
        return workFlowContext;
    }

    @Override
    public void saveOrUpdateFlow(WorkFlowContext flowContext) {
        log.info("【{}】【{}】保存流水:",flowContext.getBusiSno(),flowContext.getStep());
        workFlowDao.saveOrUpdate(flowContext.getFlow());
        PluginTransInfo pluginTransInfo = new PluginTransInfo();
        pluginTransInfo.setBusiSno(flowContext.getBusiSno());
        pluginTransInfo.setContent(JSON.toJSONString(flowContext.getFlowParams()));
        pluginTransInfo.setUpdateTime(new Date());
        pluginTransInfoDao.saveOrUpdate(pluginTransInfo);
    }
}
