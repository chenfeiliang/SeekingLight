package seekLight.workflow.dto.report;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class CrisPbcReport {
    private String content;

    public CrisPbcReport() {
    }

    public CrisPbcReport(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ModelReport{" +
                "content='" + content + '\'' +
                '}';
    }
}
