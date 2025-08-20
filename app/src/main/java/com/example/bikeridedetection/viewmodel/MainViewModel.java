package com.example.bikeridedetection.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bikeridedetection.data.PreferencesRepository;

public class MainViewModel extends ViewModel{
    private final PreferencesRepository repo;
    private final MutableLiveData<Boolean> bikeModeEnabled = new MutableLiveData<>(false);

    public MainViewModel(PreferencesRepository repo) {
        this.repo = repo;
        // Load initial state from storage
        bikeModeEnabled.setValue(repo.isBikeModeEnabled());
    }

    public LiveData<Boolean> getBikeModeEnabled() {
        return bikeModeEnabled;
    }

    public void setBikeModeEnabled(boolean enabled) {
        Boolean current = bikeModeEnabled.getValue();
        if (current != null && current == enabled) return;
        repo.setBikeModeEnabled(enabled);
        bikeModeEnabled.setValue(enabled);
    }

    public void toggleBikeMode() {
        boolean next = !(bikeModeEnabled.getValue() != null && bikeModeEnabled.getValue());
        setBikeModeEnabled(next);
    }

}
