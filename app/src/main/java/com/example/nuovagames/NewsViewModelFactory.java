package com.example.nuovagames;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.nuovagames.repository.IGamesRepository;


/**
 * Custom ViewModelProvider to be able to have a custom constructor
 * for the NewsViewModel class.
 */
public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private final IGamesRepository iNewsRepositoryWithLiveData;

    public NewsViewModelFactory(IGamesRepository iNewsRepositoryWithLiveData) {
        this.iNewsRepositoryWithLiveData = iNewsRepositoryWithLiveData;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsViewModel(iNewsRepositoryWithLiveData);
    }
}
