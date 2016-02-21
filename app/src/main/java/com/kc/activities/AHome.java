package com.kc.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.kc.R;
import com.kc.database.DBHelper;
import com.kc.database.TTimetable;
import com.kc.fragments.FHome;
import com.kc.utilites.RemoteDatabaseConnecter;
import org.json.JSONObject;

import static com.kc.C.FILE_NAME;
import static com.kc.C.GET_STUDENT_INFO;
import static com.kc.C.GET_TIMETABLE;
import static com.kc.C.MY_GCM_ID;
import static com.kc.C.MY_ID;
import static com.kc.C.MY_NAME;
import static com.kc.C.MY_ROLL;
import static com.kc.C.MY_SEM;

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

        new MyInfoUpdateChecker().execute(null, null, null);

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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    /**
     * Needed for:
     * 1. my semester change
     */
    class MyInfoUpdateChecker extends AsyncTask<Void, Integer, Void> {

        boolean isConnected = false;
        ProgressBar bar;

        @Override
        protected void onPreExecute() {
            bar = (ProgressBar) findViewById(R.id.ahome_progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(0);
        }

        @Override
        public Void doInBackground(Void... params) {

            publishProgress(20);
            ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conman.getActiveNetworkInfo();
            isConnected = netInfo != null && netInfo.getState().equals(State.CONNECTED);

            publishProgress(40);
            if (isConnected) {
                String url = GET_STUDENT_INFO + "student_id=" + MY_ID;

                try {
                    RemoteDatabaseConnecter rdc = new RemoteDatabaseConnecter("GET", url).connect(null);
                    JSONObject jo = rdc.getJSONObject();
                    publishProgress(60);

                    if (MY_SEM == jo.getInt("my_sem")) {// todo change to sem < jo

                        MY_SEM = jo.getInt("my_sem");


                        // and delete previous one first
                        DBHelper dbHelper = new DBHelper(AHome.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM " + TTimetable.NAME);

                        // todo load new time table for new sem
                        url = GET_TIMETABLE + "my_sem=" + MY_SEM;
                        RemoteDatabaseConnecter rdc2 = new RemoteDatabaseConnecter("GET", url).connect(null);
                        JSONObject jo2 = rdc2.getJSONObject();
                        for (int i = 1; i <= jo2.length(); i++) {

                            JSONObject tmpJo = jo2.getJSONObject("" + i);

                            String subject_name = tmpJo.getString("full_name");
                            Log.d(TAG, i + "> " + subject_name);


                        }

                    }

                    MY_ID = jo.getString("my_id");
                    MY_GCM_ID = jo.getString("my_gcm_id");
                    MY_SEM = jo.getInt("my_sem");
                    MY_ROLL = jo.getInt("my_roll");
                    MY_NAME = jo.getString("my_name");

                    publishProgress(80);
                    SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString("my_student_id", MY_ID);
                    editor.putString("my_gcm_id", MY_GCM_ID);
                    editor.putInt("my_sem", MY_SEM);
                    editor.putInt("my_roll", MY_ROLL);
                    editor.putString("my_name", MY_NAME);
                    editor.apply();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bar.setVisibility(View.GONE);
        }
    }

}
