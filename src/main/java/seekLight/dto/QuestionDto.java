package seekLight.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String questionId;
    private String title;

    public QuestionDto(String questionId, String title) {
        this.questionId = questionId;
        this.title = title;
    }
}
