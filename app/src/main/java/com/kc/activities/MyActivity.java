package com.kc.activities; // 21 Feb, 12:00 AM

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class MyActivity extends AppCompatActivity {

    protected String TAG   = "A";
    protected String CLASS = this.getClass().getSimpleName();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, CLASS + " onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, CLASS + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, CLASS + " onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, CLASS + " onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, CLASS + " onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, CLASS + " onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, CLASS + " onDestroy");
    }

}
