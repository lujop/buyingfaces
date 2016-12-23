package cat.joanpujol.buyingfaces.data;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.rx_cache.DynamicKey;
import io.rx_cache.LifeCache;
import rx.Observable;

/**
 * Created by lujop on 22/12/2016.
 * Cache provider using RxCache
 */

public interface FaceCache {

    @LifeCache(duration = 1, timeUnit = TimeUnit.HOURS)
    Observable<List<FaceProduct>> getFace(Observable<List<FaceProduct>> apiObservable, DynamicKey key);

    public static class FaceCacheKey {
        private String queryString;
        private boolean onlyInStock;
        private int limit;
        private int start;

        public FaceCacheKey(String queryString,boolean onlyInStock, int limit, int start) {
            this.onlyInStock = onlyInStock;
            this.limit = limit;
            this.queryString = queryString;
            this.start = start;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FaceCacheKey that = (FaceCacheKey) o;

            if (onlyInStock != that.onlyInStock) return false;
            if (limit != that.limit) return false;
            if (start != that.start) return false;
            return queryString != null ? queryString.equals(that.queryString) : that.queryString == null;

        }

        @Override
        public int hashCode() {
            int result = queryString != null ? queryString.hashCode() : 0;
            result = 31 * result + (onlyInStock ? 1 : 0);
            result = 31 * result + limit;
            result = 31 * result + start;
            return result;
        }
    }
}
