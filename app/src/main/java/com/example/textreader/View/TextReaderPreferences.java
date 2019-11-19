package com.example.textreader.View;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceFragmentCompat;

import com.example.textreader.Model.TextReaderService;
import com.example.textreader.R;
import com.example.textreader.ViewModel.TextReaderViewModel;


public class TextReaderPreferences extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private TextReaderViewModel textReaderViewModel;

    public TextReaderPreferences() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        textReaderViewModel = ViewModelProviders.of(getActivity()).get(TextReaderViewModel.class);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case "ACTIVATE_READER":
                textReaderViewModel.setServiceState(sharedPreferences.getBoolean(s, false));
                break;
            case "HEADPHONE_CHECK":
                TextReaderService.setHeadsetCheck(!sharedPreferences.getBoolean(s, false));
                break;

        }
    }
}
