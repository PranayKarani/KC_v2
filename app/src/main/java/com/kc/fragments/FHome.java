package com.kc.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.kc.R;
import com.kc.activities.AHome;

public class FHome extends Fragment {
    
    
    public FHome() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AHome.CURRENT_FRAGMENT = AHome.F_HOME;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.f_home, container, false);
        return view;
    }
    
    
}
