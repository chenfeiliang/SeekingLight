package seekLight.workflow.fecther;

import seekLight.workflow.flow.Flow;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class IndexFetcher<F extends Flow,D> implements Fetcher<F,D> {
    private int index;
    Fetcher<F,D> fetcher;

    public IndexFetcher(int index, Fetcher<F, D> fetcher) {
        this.index = index;
        this.fetcher = fetcher;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public D fetch(F flow) {
        return fetcher.fetch(flow);
    }
}
