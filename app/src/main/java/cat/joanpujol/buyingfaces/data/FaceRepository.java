package cat.joanpujol.buyingfaces.data;

import android.util.Log;

import java.util.List;

import io.rx_cache.DynamicKey;
import rx.Observable;

/**
 * Created by lujop on 22/12/2016.
 *
 * Repository pattern. Unifies data loading retriving data from cache or API
 */
public class FaceRepository {
    private FaceApi faceApi;
    private FaceCache faceCache;

    public FaceRepository(FaceApi faceApi,FaceCache faceCache) {
        this.faceApi = faceApi;
        this.faceCache = faceCache;
    }

    public Observable<List<FaceProduct>> getFace(String queryString, boolean onlyInStock,int limit, int start) {
        Log.d("faces","Retrieving faces with q="+queryString+" onlyInStock="+onlyInStock+" start="+start+ " limit="+limit);
        Observable<List<FaceProduct>> apiObs = faceApi.getFace(queryString,onlyInStock,limit,start);
        FaceCache.FaceCacheKey key = new FaceCache.FaceCacheKey(queryString,onlyInStock,limit,start);
        return faceCache.getFace(apiObs,new DynamicKey(key));
    }
}
