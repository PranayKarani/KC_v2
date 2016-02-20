package com.kc.activities; // 21 Feb, 12:00 AM

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MyActivity extends AppCompatActivity {

    protected String TAG = this.getClass().getSimpleName();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, TAG + " onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, TAG + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, TAG + " onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, TAG + " onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, TAG + " onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, TAG + " onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, TAG + " onDestroy");
    }

}
