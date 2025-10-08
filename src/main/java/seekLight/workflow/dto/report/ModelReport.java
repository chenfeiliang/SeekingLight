package seekLight.workflow.dto.report;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class ModelReport {
    private String content;

    public ModelReport(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ModelReport{" +
                "content='" + content + '\'' +
                '}';
    }
}
