package cat.joanpujol.buyingfaces;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cat.joanpujol.buyingfaces.data.FaceProduct;
import cat.joanpujol.buyingfaces.util.EndlessRecyclerViewScrollListener;
import cat.joanpujol.buyingfaces.util.SpacesItemDecoration;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Main activity with face searching UI.
 * It follows MVVM architecture: MainactivityViewModel holds the logic that is mostly Android free and
 * communication is made with bindings between the ViewModel and the Activity that holds view logic.
 */
public class MainActivity extends RxAppCompatActivity {
    private MainActivityViewModel viewModel;
    private FaceGridAdapter gridAdapter;

    private SearchView searchView;
    private CheckBox onlyInStock;
    private EndlessRecyclerViewScrollListener scrollListener;

    private Set<Subscription> loadMoreSubscriptions  = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;

        //Init View
        setContentView(R.layout.activity_main);
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(querySearchListener);
        onlyInStock = (CheckBox) findViewById(R.id.onlyInStock);
        onlyInStock.setOnCheckedChangeListener(onlyInStockChangedListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        gridAdapter = new FaceGridAdapter();
        float margin = getResources().getDimension(R.dimen.recyclerViewSpacer);
        recyclerView.addItemDecoration(new SpacesItemDecoration((int)margin));
        recyclerView.setAdapter(gridAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Subscription subscription = viewModel.retrieveMore(page)
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(MainActivity.this.<List<FaceProduct>>bindToLifecycle())
                    .subscribe(new Action1<List<FaceProduct>>() {
                        @Override
                        public void call(List<FaceProduct> faceProducts) {
                            gridAdapter.addResults(faceProducts);
                        }
                    });
                loadMoreSubscriptions.add(subscription);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int displayHeight = (int) (metrics.heightPixels / metrics.density);


        //Init ViewModel
        viewModel = new MainActivityViewModel(this,displayHeight);
        //Bind search results from ViewModel
        viewModel.getFaceResultsObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(this.<List<FaceProduct>>bindToLifecycle())
            .subscribe(new Action1<List<FaceProduct>>() {
                @Override
                public void call(List<FaceProduct> faceProducts) {
                    unsubscribeLoadMoreSubscriptions();
                    gridAdapter.clearResults();
                    gridAdapter.addResults(faceProducts);
                }
            });
        //Bind errors
        viewModel.getErrorObservable()
            .compose(this.<String>bindToLifecycle())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
                @Override
                public void call(String error) {
                    Toast.makeText(MainActivity.this,error,Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Unsubscribes all pending loadMoreSubscriptions
     */
    private void unsubscribeLoadMoreSubscriptions() {
        for(Subscription loadMoreSubscription : loadMoreSubscriptions) {
            loadMoreSubscription.unsubscribe();
        }
        loadMoreSubscriptions.clear();
    }

    private SearchView.OnQueryTextListener querySearchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            viewModel.getSearchTerms().set(new FaceSearchTerms(newText,onlyInStock.isChecked()));
            return true;
        }
    };

    private CompoundButton.OnCheckedChangeListener onlyInStockChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            viewModel.getSearchTerms().set(new FaceSearchTerms(searchView.getQuery().toString(),isChecked));
        }
    };

    public static class FaceSearchTerms {
        public final String searchString;
        public final boolean onlyInStock;

        public FaceSearchTerms(String searchString, boolean onlyInStock) {
            this.onlyInStock = onlyInStock;
            this.searchString = searchString;
        }
    }
}



