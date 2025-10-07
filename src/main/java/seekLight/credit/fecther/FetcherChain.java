package seekLight.credit.fecther;

import seekLight.credit.flow.Flow;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class FetcherChain<F extends Flow,D> implements Fetcher<F, D> {
    List<Fetcher<F,D>> fetchers =
            new LinkedList<>();

    public FetcherChain<F,D> add(Fetcher<F,D> fetcher){
        if(fetcher==null){
            return this;
        }
        //fetcher instanceof IndexFetcher<F,D>
        if(fetcher instanceof IndexFetcher){
            fetchers.add(((IndexFetcher<F, D>) fetcher).getIndex(),fetcher);
        }else {
            fetchers.add(fetcher);
        }
        return this;
    }

    @Override
    public  D fetch(F flow) {
        for(Fetcher<F,D> fetcher : fetchers){
            D data = fetcher.fetch(flow);
            if(Objects.nonNull(data)){
                return data;
            }
        }
        return null;
    }
}
