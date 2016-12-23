package cat.joanpujol.buyingfaces;

import cat.joanpujol.buyingfaces.data.FaceApi;
import cat.joanpujol.buyingfaces.data.FaceCache;
import cat.joanpujol.buyingfaces.data.FaceRepository;

/**
 * Created by lujop on 22/12/2016.
 */

public interface DependencyManager {
    FaceApi provideFaceApi();
    FaceCache provideFaceCache();
    FaceRepository provideFaceRepository();
}
