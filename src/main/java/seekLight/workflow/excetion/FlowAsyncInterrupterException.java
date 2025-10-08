package seekLight.workflow.excetion;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class FlowAsyncInterrupterException extends RuntimeException {
    private Runnable asyncRunnable;

    public FlowAsyncInterrupterException(String message) {
        super(message);
    }

    public FlowAsyncInterrupterException(Runnable runnable) {
        this.asyncRunnable = runnable;
    }

    public Runnable getRunnable(){
        return asyncRunnable;
    }
}
