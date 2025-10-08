package seekLight.workflow.excetion;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class FlowInterrupterException extends RuntimeException {
    public FlowInterrupterException(String message) {
        super(message);
    }
}
