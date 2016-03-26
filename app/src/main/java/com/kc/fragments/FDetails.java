package com.kc.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kc.C;
import com.kc.R;
import com.kc.activities.AHome;

public class FDetails extends MyFragment {

    boolean getEmailInput = false;
    private Context context;

    public FDetails() {
        // Required empty public constructor
        AHome.CURRENT_FRAGMENT = AHome.F_DETAILS;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.f_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.fdetails_name)).setText(C.MY_NAME);
        ((TextView) view.findViewById(R.id.fdetails_rollno)).setText("" + C.MY_ROLL);
        ((TextView) view.findViewById(R.id.fdetails_id)).setText(C.MY_ID);
        ((TextView) view.findViewById(R.id.fdetails_sem)).setText("" + C.MY_SEM);
        ((TextView) view.findViewById(R.id.fdetails_email)).setText(C.MY_EMAIL);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
