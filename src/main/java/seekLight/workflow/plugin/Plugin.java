package seekLight.workflow.plugin;

import seekLight.workflow.flow.Flow;

public interface Plugin <F extends Flow>{

    boolean support(Flow flow);

    void run(F flow);

    String getName();
}
