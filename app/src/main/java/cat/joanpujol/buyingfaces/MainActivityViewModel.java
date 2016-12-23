package cat.joanpujol.buyingfaces;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.joanpujol.buyingfaces.data.FaceProduct;
import cat.joanpujol.buyingfaces.data.FaceRepository;
import cat.joanpujol.buyingfaces.util.RxVariable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by lujop on 22/12/2016.
 * Holds Face retrieving logic
 *
 * It communicates with the view (MainActivity) throught offered Observables that view binds to
 */
public class MainActivityViewModel {
    private Context context;
    private RxVariable<MainActivity.FaceSearchTerms> searchTerms = new RxVariable<>(new MainActivity.FaceSearchTerms("",false));
    private FaceRepository faceRepository;
    private FaceRetriever faceRetriever;
    private PublishSubject<String> errorObservable = PublishSubject.create();
    private Observable<List<FaceProduct>> faceResultsObservable;

    public MainActivityViewModel(Context context, int displayHeight) {
        this.context = context;

        injectDependencies();

        int retrieverCount = calculateNumberOfCardNeeded(displayHeight);
        Log.d("faces","Retrieving "+retrieverCount+" faces each time");
        faceRetriever = new FaceRetriever(retrieverCount);

        faceResultsObservable = searchTerms.asObservable()
            .debounce(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeOn(Schedulers.io())
            .flatMap(new Func1<MainActivity.FaceSearchTerms, Observable<List<FaceProduct>>>() {
                @Override
                public Observable<List<FaceProduct>> call(MainActivity.FaceSearchTerms searchTerms) {
                    return faceRetriever.retrieve(searchTerms.searchString,searchTerms.onlyInStock)
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                errorObservable.onNext("Network error retrieving faces");
                            }
                        })
                        .onErrorResumeNext(Observable.<List<FaceProduct>>empty());
                }
            });
    }

    /**
     * Calculates the cards needed to fill 1.5 screens in the worst case
     */
    private int calculateNumberOfCardNeeded(int displayHeight) {
        int neededForColumn = (int) Math.ceil(displayHeight * 1.5 / FaceGridAdapter.SMALL_CARD_HEIGHT);
        return neededForColumn * 2; //We have two columns
    }

    private void injectDependencies() {
        DependencyManager dm = ((BuyingFacesApplication)context.getApplicationContext()).dependencyManager();
        faceRepository = dm.provideFaceRepository();
    }

    public Observable<List<FaceProduct>> getFaceResultsObservable() {
        return faceResultsObservable;
    }

    public RxVariable<MainActivity.FaceSearchTerms> getSearchTerms() {
        return searchTerms;
    }

    public Observable<List<FaceProduct>> retrieveMore(int page) {
        return faceRetriever.retrieveMore(page)
            .onErrorResumeNext(Observable.<List<FaceProduct>>empty())
            .subscribeOn(Schedulers.io());
    }

    public Observable<String> getErrorObservable() {
        return errorObservable;
    }

    class FaceRetriever {
        private int retrieveBatch;
        private String queryString;
        private boolean onlyInStock;

        public FaceRetriever(int retrieveBatch) {
            this.retrieveBatch = retrieveBatch;
        }

        public Observable<List<FaceProduct>> retrieve(String queryString, boolean onlyInStock) {
            this.queryString = queryString;
            this.onlyInStock = onlyInStock;
            return faceRepository.getFace(queryString,onlyInStock,retrieveBatch,0);
        }

        public Observable<List<FaceProduct>> retrieveMore(int startFrom) {
            return faceRepository.getFace(queryString,onlyInStock,retrieveBatch,startFrom);
        }
    }
}
