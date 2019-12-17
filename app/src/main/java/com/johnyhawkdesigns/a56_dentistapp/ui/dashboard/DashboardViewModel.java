package com.johnyhawkdesigns.a56_dentistapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    // constructor
    public DashboardViewModel(){
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to Dashboard of Dentist App");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
