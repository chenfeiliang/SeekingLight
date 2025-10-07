package seekLight.credit.dto.report;

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
