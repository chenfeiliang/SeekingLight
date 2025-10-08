package seekLight.workflow.dto.flow;

import lombok.Data;

import lombok.extern.slf4j.Slf4j;
@Data
@Slf4j

public class PublishDto {
    private String questionId;
    private String answer;
}
