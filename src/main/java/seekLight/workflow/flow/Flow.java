package seekLight.workflow.flow;

import java.util.Date;

public interface Flow {
    String getBusiSno();

    String getRoute();

    String getStep();

    void setStep(String step);

    String getTransStatus();

    void setTransStatus(String transStatus);

    String getTransType();

    void setTransType(String transType);

    Date getTransTime();

    void setTransTime(Date transTime);
}
