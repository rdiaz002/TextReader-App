package com.example.textreader;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Intent serv;
    private SharedPreferences preference;
    private TextReaderPreferences prefFrag;
    private String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.READ_CONTACTS, Manifest.permission.RECEIVE_SMS};
    private int MY_PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        prefFrag = new TextReaderPreferences();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, prefFrag)
                    .commitNow();
        }
        serv = new Intent(this, TextReaderService.class);
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreferences();
        preference.registerOnSharedPreferenceChangeListener(this);

        //Permissions Check
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST);
        }

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
        switch (s) {
            case "ACTIVATE_READER":
                if (sharedPreferences.getBoolean(s, false)) {
                    startService(serv);
                } else {
                    stopService(serv);
                }
                break;
            case "HEADPHONE_CHECK":
                TextReaderService.setHeadsetCheck(!sharedPreferences.getBoolean(s, false));
                break;

        }
    }
}
