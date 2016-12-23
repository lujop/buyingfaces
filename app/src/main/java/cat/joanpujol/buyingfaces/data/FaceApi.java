package cat.joanpujol.buyingfaces.data;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lujop on 22/12/2016.
 * Face Api using Retrofit
 */
public interface FaceApi {
    @GET("api/products")
    Observable<List<FaceProduct>> getFace(@Query("q") String queryString, @Query("onlyInStock") boolean onlyInStock, @Query("limit") int limit, @Query("skip") int skip);
}
