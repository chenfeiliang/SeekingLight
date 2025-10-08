package seekLight.workflow.runner;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class ContextRunner {
    private Runnable runnable;

    private String busiSno;

    private ThreadLocal<String> threadLocal =
            new ThreadLocal<>();

    public ContextRunner(Runnable runnable,String busiSno) {
        this.runnable = runnable;
        this.busiSno = busiSno;
    }

    public void run(){
        try {
            initMdc(busiSno);
            runnable.run();
        }finally {
            removeMdc();
        }
    }

    private void initMdc(String busiSno){
        threadLocal.set(busiSno);
    }

    private void removeMdc(){
        threadLocal.remove();
    }
}
