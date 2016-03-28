package com.kc.fragments; // 21 Feb, 01:05 AM

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MyFragment extends Fragment {

    protected String CLASS = this.getClass().getSimpleName();
    String titleBar_text;
    private   String TAG   = "Frag_Life";

    public MyFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG, CLASS + " attached to context " + context.toString());
        ((AppCompatActivity) context).getSupportActionBar().setTitle(titleBar_text);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, CLASS + " created");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, CLASS + "'s view inflating");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, CLASS + "'s view created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, CLASS + " destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, CLASS + "'s view destroyed");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, CLASS + " detached");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, CLASS + " paused");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, CLASS + " started");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, CLASS + " stopped");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, CLASS + " resumed");
    }
}
