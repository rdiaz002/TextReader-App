package com.example.textreader.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TextReaderViewModel extends ViewModel {
    private MutableLiveData<Boolean> serviceState = new MutableLiveData<>();
    private MutableLiveData<Boolean> headPhoneCheck = new MutableLiveData<>();

    public MutableLiveData<Boolean> getServiceState() {
        return serviceState;
    }

    public void setServiceState(boolean active) {
        serviceState.setValue(active);
    }

    public MutableLiveData<Boolean> getHeadPhoneCheck() {
        return headPhoneCheck;
    }

    public void setHeadPhoneCheck(boolean state) {
        headPhoneCheck.setValue(state);
    }
}
