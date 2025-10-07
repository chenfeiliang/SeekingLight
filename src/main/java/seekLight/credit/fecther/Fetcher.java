package seekLight.credit.fecther;


import seekLight.credit.flow.Flow;

public interface Fetcher <F extends Flow,D>{
     D fetch(F flow);
}
