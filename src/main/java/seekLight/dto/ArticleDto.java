package seekLight.dto;

import lombok.Data;

@Data
public class ArticleDto {
    private String sectionNo;
    private String sectionTitle;
    private String sectionContent;

    public ArticleDto(String sectionNo, String sectionTitle, String sectionContent) {
        this.sectionNo = sectionNo;
        this.sectionTitle = sectionTitle;
        this.sectionContent = sectionContent;
    }
}
