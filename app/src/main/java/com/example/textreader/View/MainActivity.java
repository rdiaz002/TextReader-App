package com.example.textreader.View;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.example.textreader.Model.TextReaderService;
import com.example.textreader.R;
import com.example.textreader.ViewModel.TextReaderViewModel;


public class MainActivity extends AppCompatActivity {

    private Intent serv;
    private SharedPreferences preference;
    private TextReaderPreferences prefFrag;
    private TextReaderService service;
    private TextReaderViewModel textReaderViewModel;
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
        preference.registerOnSharedPreferenceChangeListener(prefFrag);

        //Permissions Check
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST);
        }
        textReaderViewModel = ViewModelProviders.of(this).get(TextReaderViewModel.class);
        textReaderViewModel.getServiceState().observe(this, this::toggleService);


    }

    private void updatePreferences() {
        SharedPreferences.Editor editor = preference.edit();
        if (TextReaderService.isTextreaderActive()) {
            editor.putBoolean("ACTIVATE_READER", true);
        } else {
            editor.putBoolean("ACTIVATE_READER", false);
        }
        editor.apply();
    }

    public void toggleService(boolean state) {
        if (state) {
            //startService(serv);
        } else {
            //stopService(serv);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePreferences();
    }

}
