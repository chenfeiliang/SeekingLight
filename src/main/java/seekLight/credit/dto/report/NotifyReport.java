package seekLight.credit.dto.report;

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
