package cat.joanpujol.buyingfaces;

import android.app.Application;
import android.util.Log;

import java.io.File;

import cat.joanpujol.buyingfaces.data.FaceApi;
import cat.joanpujol.buyingfaces.data.FaceCache;
import cat.joanpujol.buyingfaces.data.FaceRepository;
import cat.joanpujol.buyingfaces.util.NDJSONConverterFactory;
import io.rx_cache.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by lujop on 22/12/2016.
 */

public class BuyingFacesApplication extends Application {
    private DependencyManager dependencyManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e("faces","Uncaught exception on thread "+t,e);
            }
        });

        initDependencies();
    }

    public DependencyManager dependencyManager() {
        return dependencyManager;
    }

    private void initDependencies() {
        dependencyManager = new DependecyManagerImpl();
    }

    private class DependecyManagerImpl implements DependencyManager {
        private Retrofit retrofit;
        private FaceCache faceCache;

        public DependecyManagerImpl() {
            retrofit = new Retrofit.Builder()
                .baseUrl("http://ascii.rorygarand.com/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(new NDJSONConverterFactory())
                .build();

            File cacheDir = getFilesDir();
            faceCache = new RxCache.Builder().persistence(cacheDir,new GsonSpeaker()).using(FaceCache.class);
        }

        @Override
        public FaceApi provideFaceApi() {
            return retrofit.create(FaceApi.class);
        }

        @Override
        public FaceCache provideFaceCache() {
            return faceCache;
        }

        @Override
        public FaceRepository provideFaceRepository() {
            return new FaceRepository(provideFaceApi(),provideFaceCache());
        }
    }
}
