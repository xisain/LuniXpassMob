package com.example.lunixpassmob.ui.lunixpass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LunixpassViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public LunixpassViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}