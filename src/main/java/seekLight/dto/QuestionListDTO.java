package seekLight.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j

public class QuestionListDTO {
    private String question;
    private String answer;
    private String questionId;
}
