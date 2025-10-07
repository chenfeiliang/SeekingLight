package seekLight.credit.plugin;

import seekLight.credit.flow.Flow;

public interface Plugin <F extends Flow>{

    boolean support(Flow flow);

    void run(F flow);

    String getName();
}
