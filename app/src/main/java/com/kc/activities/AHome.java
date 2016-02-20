package com.kc.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.kc.R;
import com.kc.fragments.FHome;

public class AHome extends MyActivity {

    public static final int F_HOME           = 1;
    public static final int F_NOTICEBOARD    = 2;
    public static       int CURRENT_FRAGMENT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ahome_toolbar);
        setSupportActionBar(toolbar);

        /* If notification is tapped, set notification fragment as starting fragment */
        int show_this_fragment = getIntent().getIntExtra("show_this", F_HOME);

        Fragment opening_fragment;
        switch (show_this_fragment) {

            case F_HOME:
                opening_fragment = new FHome();
                break;

            default:
                opening_fragment = new FHome();
                break;
        }
        FragmentManager fragma = getSupportFragmentManager();
        FragmentTransaction fragta = fragma.beginTransaction();
        fragta.replace(R.id.ahome_framelayout, opening_fragment);
        fragta.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_home, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
