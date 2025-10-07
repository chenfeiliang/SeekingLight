package seekLight.credit.fecther;

import seekLight.credit.flow.Flow;

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
