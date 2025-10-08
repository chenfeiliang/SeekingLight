package seekLight.workflow.fecther;


import seekLight.workflow.flow.Flow;

public interface Fetcher <F extends Flow,D>{
     D fetch(F flow);
}
