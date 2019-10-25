package com.smartlines.laravel_restful.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.smartlines.laravel_restful.R;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splash);
        prefs = getSharedPreferences("Preference", Context.MODE_PRIVATE);
        SystemClock.sleep(500);

        Intent i;

        if (!TextUtils.isEmpty(getkeyPrefs())) {
            i = new Intent(this, MainActivity.class);
        } else {
            i = new Intent(this, LoginActivity.class);
        }
        startActivity(i);
        finish();
    }

    private String getkeyPrefs() {
        return prefs.getString("key", "");
    }

//    private String getnombrePrefs() {
//        return prefs.getString("nombre", "");
//    }
}
