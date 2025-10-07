package seekLight.credit.dto.report;

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
