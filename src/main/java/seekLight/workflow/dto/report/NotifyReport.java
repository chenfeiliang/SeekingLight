package seekLight.workflow.dto.report;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class NotifyReport {
    private String content;

    public NotifyReport(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NotifyReport{" +
                "content='" + content + '\'' +
                '}';
    }
}
