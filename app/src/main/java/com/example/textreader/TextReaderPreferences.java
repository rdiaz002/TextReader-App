package com.example.textreader;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;


public class TextReaderPreferences extends PreferenceFragmentCompat {

    public TextReaderPreferences() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
