package seekLight.credit.flow;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class QuotaReqFlow implements Flow{
    private String busiSno;
    private String route;
    private String step;
    private String transStatus;
    private String transType;
    private Date transTime;
    private Map<String,String> fowParams = new ConcurrentHashMap<>();

    @Override
    public String getBusiSno() {
        return busiSno;
    }

    public void setBusiSno(String busiSno) {
        this.busiSno = busiSno;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String getRoute() {
        return route;
    }

    @Override
    public String getStep() {
        return step;
    }

    @Override
    public void setStep(String step) {
        this.step = step;
    }

    @Override
    public String getTransStatus() {
        return transStatus;
    }

    @Override
    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    @Override
    public String getTransType() {
        return transType;
    }

    @Override
    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Override
    public Date getTransTime() {
        return transTime;
    }

    @Override
    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public void putParam(String key,String value){
        if(Objects.nonNull(value)){
            fowParams.put(key,value);
        }
    }

    public String getParam(String key){
       return fowParams.get(key);
    }

    public Map<String, String> getFowParams() {
        return fowParams;
    }

}
