package com.example.textreader.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TextReaderViewModel extends ViewModel {
    private MutableLiveData<Boolean> serviceState;
    private MutableLiveData<Boolean> headPhoneCheck;

    public MutableLiveData<Boolean> getServiceState() {
        if (serviceState == null) {
            serviceState = new MutableLiveData<>();
        }
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
