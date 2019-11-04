package com.example.textreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Intent serv;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new TextReaderPreferences())
                    .commitNow();
        }
        serv = new Intent(this, TextReaderService.class);
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreferences();
        preference.registerOnSharedPreferenceChangeListener(this);
    }

    private void updatePreferences() {
        SharedPreferences.Editor editor = preference.edit();
        if (TextReaderService.TEXTREADER_ACTIVE) {
            editor.putBoolean("ACTIVATE_READER", true);
        } else {
            editor.putBoolean("ACTIVATE_READER", false);
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if (sharedPreferences.getBoolean(s, false)) {
            startService(serv);
        } else {
            stopService(serv);
        }
    }
}
