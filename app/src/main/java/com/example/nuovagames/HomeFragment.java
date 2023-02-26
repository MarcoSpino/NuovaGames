package com.example.nuovagames;

import static com.example.nuovagames.Constanti.LAST_UPDATE;
import static com.example.nuovagames.Constanti.TOP_HEADLINES_PAGE_SIZE_VALUE;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.nuovagames.adapter.NewsRecyclerViewAdapter;
import com.example.nuovagames.databinding.FragmentHomeBinding;
import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.GamesApiResponse;
import com.example.nuovagames.model.Result;
import com.example.nuovagames.repository.GamesRepository;
import com.example.nuovagames.repository.IGamesRepository;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that shows the news associated with a Country.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private FragmentHomeBinding fragmentCountryNewsBinding;

    private List<Games> newsList;
    private NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    private NewsViewModel newsViewModel;

    private int totalItemCount; // Total number of news
    private int lastVisibleItem; // The position of the last visible news item
    private int visibleItemCount; // Number or total visible news items

    // Based on this value, the process of loading more news is anticipated or postponed
    // Look at the if condition at line 237 to see how it is used
    private final int threshold = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IGamesRepository newsRepositoryWithLiveData =
                ServiceLocator.getInstance().getNewsRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        // This is the way to create a ViewModel with custom parameters
        // (see NewsViewModelFactory class for the implementation details)
        newsViewModel = new ViewModelProvider(
                requireActivity(),
                new NewsViewModelFactory(newsRepositoryWithLiveData)).get(NewsViewModel.class);

        newsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentCountryNewsBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentCountryNewsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });

        RecyclerView recyclerViewCountryNews =view.findViewById(R.id.recyclerview_country_news);
        LinearLayoutManager layoutManager =
                new WrapContentLinearLayoutManager(requireContext(),
                        LinearLayoutManager.VERTICAL, false);

        newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(newsList,
                requireActivity().getApplication(),
                new NewsRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onNewsItemClick(Games news) {

                    }

                    @Override
                    public void onFavoriteButtonPressed(int position) {
                        newsList.get(position).setFavorite(!newsList.get(position).isFavorite());
                        newsViewModel.updateNews(newsList.get(position));
                    }
                });
        recyclerViewCountryNews.setLayoutManager(layoutManager);
        recyclerViewCountryNews.setAdapter(newsRecyclerViewAdapter);

        long lastUpdate = System.currentTimeMillis();


        fragmentCountryNewsBinding.progressBar.setVisibility(View.VISIBLE);

        // Observe the LiveData associated with the MutableLiveData containing all the news
        // returned by the method getNews(String, long) of NewsViewModel class.
        // Pay attention to which LifecycleOwner you give as value to
        // the method observe(LifecycleOwner, Observer).
        // In this case, getViewLifecycleOwner() refers to
        // androidx.fragment.app.FragmentViewLifecycleOwner and not to the Fragment itself.
        // You can read more details here: https://stackoverflow.com/a/58663143/4255576
        newsViewModel.getNews(Long.parseLong(String.valueOf(lastUpdate))).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {

                        GamesApiResponse newsResponse = ((Result.Success) result).getData();
                        List<Games> fetchedNews = newsResponse.getResults();

                        if (!newsViewModel.isLoading()) {
                            if (newsViewModel.isFirstLoading()) {
                                newsViewModel.setTotalResults(((GamesApiResponse) newsResponse).getNumber_of_total_results());
                                newsViewModel.setFirstLoading(false);
                                this.newsList.addAll(fetchedNews);
                                newsRecyclerViewAdapter.notifyItemRangeInserted(0,
                                        this.newsList.size());
                            } else {
                                // Updates related to the favorite status of the news
                                newsList.clear();
                                newsList.addAll(fetchedNews);
                                newsRecyclerViewAdapter.notifyItemChanged(0, fetchedNews.size());
                            }
                            fragmentCountryNewsBinding.progressBar.setVisibility(View.GONE);
                        } else {
                            newsViewModel.setLoading(false);
                            newsViewModel.setCurrentResults(newsList.size());

                            int initialSize = newsList.size();

                            for (int i = 0; i < newsList.size(); i++) {
                                if (newsList.get(i) == null) {
                                    newsList.remove(newsList.get(i));
                                }
                            }
                            int startIndex = (newsViewModel.getOffset() * TOP_HEADLINES_PAGE_SIZE_VALUE) -
                                    TOP_HEADLINES_PAGE_SIZE_VALUE;
                            for (int i = startIndex; i < fetchedNews.size(); i++) {
                                newsList.add(fetchedNews.get(i));
                            }
                            newsRecyclerViewAdapter.notifyItemRangeInserted(initialSize, newsList.size());
                        }
                    } else {
                        ErrorMessagesUtil errorMessagesUtil =
                                new ErrorMessagesUtil(requireActivity().getApplication());
                        Snackbar.make(view, errorMessagesUtil.
                                        getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                        fragmentCountryNewsBinding.progressBar.setVisibility(View.GONE);
                    }
                });

        recyclerViewCountryNews.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isConnected = isConnected();

                if (isConnected && totalItemCount != newsViewModel.getTotalResults()) {

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    visibleItemCount = layoutManager.getChildCount();

                    // Condition to enable the loading of other news while the user is scrolling the list
                    if (totalItemCount == visibleItemCount ||
                            (totalItemCount <= (lastVisibleItem + threshold) &&
                                    dy > 0 &&
                                    !newsViewModel.isLoading()
                            ) &&
                                    newsViewModel.getNewsResponseLiveData().getValue() != null &&
                                    newsViewModel.getCurrentResults() != newsViewModel.getTotalResults()
                    ) {
                        MutableLiveData<Result> newsListMutableLiveData = newsViewModel.getNewsResponseLiveData();

                        if (newsListMutableLiveData.getValue() != null &&
                                newsListMutableLiveData.getValue().isSuccess()) {

                            newsViewModel.setLoading(true);
                            newsList.add(null);
                            newsRecyclerViewAdapter.notifyItemRangeInserted(newsList.size(),
                                    newsList.size() + 1);

                            int boh = newsList.size();
                            Log.e(TAG, String.valueOf(boh));
                            int page = newsViewModel.getOffset() + boh;
                            Log.e(TAG, String.valueOf(page));
                            newsViewModel.setOffset(page);
                            newsViewModel.fetchNews();
                        }
                    }
                }
            }
        });
    }

    /**
     * It checks if the device is connected to Internet.
     * See: https://developer.android.com/training/monitoring-device-state/connectivity-status-type#DetermineConnection
     *
     * @return true if the device is connected to Internet; false otherwise.
     */
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        newsViewModel.setFirstLoading(true);
        newsViewModel.setLoading(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentCountryNewsBinding = null;
    }
}