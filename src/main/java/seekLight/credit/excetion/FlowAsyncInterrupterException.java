package seekLight.credit.excetion;

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
