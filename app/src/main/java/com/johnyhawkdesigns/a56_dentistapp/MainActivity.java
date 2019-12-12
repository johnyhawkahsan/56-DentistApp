package com.johnyhawkdesigns.a56_dentistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static int SPLASH_TIMEOUT = 2000; //This is 2 seconds
    Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // To add appropriate delay for splashScreen to stay there
        try{
            Thread.sleep(SPLASH_TIMEOUT);
        } catch (Exception e){
            Log.d(TAG, "onCreate: e = " + e);
        }

        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, SPLASH_TIMEOUT);

        getSupportActionBar().setTitle("Dentist App");





    }
}
