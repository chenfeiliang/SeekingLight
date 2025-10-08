package seekLight.workflow.interceptor;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import seekLight.dao.PluginInfoDao;
import seekLight.dao.RawFlowInfoDao;
import seekLight.entity.PluginInfo;
import seekLight.entity.RawFlowInfo;
import seekLight.workflow.dto.report.CrisPbcReport;
import seekLight.workflow.flow.Flow;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j

public class RawSaveInputInterceptor implements PluginInterceptor<Flow, Object>{
    @Autowired
    private RawFlowInfoDao rawFlowInfoDao;
    @Override
    public void afterFetch(Flow flow, Object data) {
        try {
            String input = data==null?"{}":JSON.toJSONString(data);
            RawFlowInfo rawFlowInfo = new RawFlowInfo();
            rawFlowInfo.setBusiSno(flow.getBusiSno());
            rawFlowInfo.setType(flow.getStep());
            rawFlowInfo.setInputContent(input);
            rawFlowInfo.setCreateTime(new Date());
            rawFlowInfo.setUpdateTime(new Date());
            rawFlowInfoDao.saveOrUpdateByMultiId(rawFlowInfo);
            log.info("【{}】【{}】保存入参:",flow.getBusiSno(),flow.getStep());
        }catch (Exception ex){
            log.info("【{}】【{}】保存入参异常,error===>",flow.getBusiSno(),flow.getStep(),ex);
        }
    }
}
