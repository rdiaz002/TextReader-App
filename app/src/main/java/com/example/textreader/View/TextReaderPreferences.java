package com.example.textreader.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;

import com.example.textreader.Model.TextReaderService;
import com.example.textreader.R;
import com.example.textreader.ViewModel.TextReaderViewModel;


public class TextReaderPreferences extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private TextReaderViewModel textReaderViewModel;
    private Intent serv;

    public TextReaderPreferences() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        serv = new Intent(getContext(), TextReaderService.class);
        textReaderViewModel = ViewModelProviders.of(getActivity()).get(TextReaderViewModel.class);
        textReaderViewModel.getServiceState().observe(this, this::toggleService);
        textReaderViewModel.getHeadPhoneCheck().observe(this, this::toggleHeadPhoneCheck);
        if (TextReaderService.isTextreaderActive()) {
            getPreferenceManager().getSharedPreferences().edit().putBoolean("ACTIVATE_READER", true).apply();
        } else {
            getPreferenceManager().getSharedPreferences().edit().putBoolean("ACTIVATE_READER", false).apply();
        }
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case "ACTIVATE_READER":
                textReaderViewModel.setServiceState(sharedPreferences.getBoolean(s, false));
                break;
            case "HEADPHONE_CHECK":
                textReaderViewModel.setHeadPhoneCheck(sharedPreferences.getBoolean(s, false));
                break;

        }
    }

    private void toggleService(boolean state) {
        if (state) {
            getContext().startService(serv);
        } else {
            getContext().stopService(serv);
        }
    }

    private void toggleHeadPhoneCheck(boolean state) {
        TextReaderService.setHeadsetCheck(state);
    }

}
