package seekLight.workflow.context;

import com.baomidou.mybatisplus.annotation.TableField;
import seekLight.entity.WorkFlow;
import seekLight.workflow.flow.Flow;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WorkFlowContext implements Flow {

    private WorkFlow flow;

    public WorkFlowContext(WorkFlow flow) {
        this.flow = flow;
    }

    public WorkFlowContext(WorkFlow flow, Map<String, String> flowParams) {
        this.flow = flow;
        if(Objects.nonNull(flowParams)){
            this.flowParams = flowParams;
        }
    }

    public WorkFlow getFlow() {
        return flow;
    }

    public void setFlow(WorkFlow flow) {
        this.flow = flow;
    }

    @TableField(exist = false)
    private Map<String,String> flowParams = new ConcurrentHashMap<>();

    public String getBusiSno() {
        return flow.getBusiSno();
    }


    public String getStep() {
        return flow.getStep();
    }


    public String getTransStatus() {
        return flow.getTransStatus();
    }


    public String getTransType() {
        return flow.getTransType();
    }


    public String getRoute() {
        return flow.getRoute();
    }


    public Date getTransTime() {
        return flow.getTransTime();
    }


    public Date getCreateTime() {
        return flow.getCreateTime();
    }


    public Date getUpdateTime() {
        return flow.getUpdateTime();
    }


    public void setBusiSno(String busiSno) {
        flow.setBusiSno(busiSno);
    }


    public void setStep(String step) {
        flow.setStep(step);
    }


    public void setTransStatus(String transStatus) {
        flow.setTransStatus(transStatus);
    }


    public void setTransType(String transType) {
        flow.setTransType(transType);
    }


    public void setRoute(String route) {
        flow.setRoute(route);
    }


    public void setTransTime(Date transTime) {
        flow.setTransTime(transTime);
    }


    public void setCreateTime(Date createTime) {
        flow.setCreateTime(createTime);
    }


    public void setUpdateTime(Date updateTime) {
        flow.setUpdateTime(updateTime);
    }

    public String getParam(String key){
        return flowParams.get(key);
    }

    public Map<String, String> getFlowParams() {
        return flowParams;
    }

    public void putParam(String key,String value){
        if(Objects.nonNull(value)){
            flowParams.put(key,value);
        }
    }
}
