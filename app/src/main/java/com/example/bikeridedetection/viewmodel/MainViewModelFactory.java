package com.example.bikeridedetection.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.bikeridedetection.data.PreferencesRepository;

public class MainViewModelFactory implements ViewModelProvider.Factory{

    private final PreferencesRepository repo;

    public MainViewModelFactory(PreferencesRepository repo) {
        this.repo = repo;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
    }

}
