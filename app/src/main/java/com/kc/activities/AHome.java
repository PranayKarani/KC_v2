package com.kc.activities;

import android.content.ContentValues;
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

        /**
         * This runnable contains fragment loading code that will run only when our
         * infoUpdater has finished its job.
         */
        Runnable loadFragment = new Runnable() {
            @Override
            public void run() {
                /* If notification is tapped, set notification fragment as starting fragment */
                Log.d(TAG, "loading fragments");
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
        };
        new MyInfoUpdateChecker(loadFragment).execute(null, null, null);


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
        Runnable    r;

        public MyInfoUpdateChecker(Runnable r) {
            this.r = r;
        }

        @Override
        protected void onPreExecute() {
            bar = (ProgressBar) findViewById(R.id.ahome_progressBar);
            bar.setVisibility(View.VISIBLE);
            bar.setProgress(0);
        }

        @Override
        public Void doInBackground(Void... params) {

            publishProgress(10);
            ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conman.getActiveNetworkInfo();
            isConnected = netInfo != null && netInfo.getState().equals(State.CONNECTED);

            if (isConnected) {
                String url = GET_STUDENT_INFO + "student_id=" + MY_ID;

                try {
                    RemoteDatabaseConnecter rdc = new RemoteDatabaseConnecter("GET", url).connect(null);
                    JSONObject jo = rdc.getJSONObject();
                    publishProgress(30);

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
                        int progress = 30;
                        for (int i = 1; i <= jo2.length(); i++) {

                            JSONObject tmpJo = jo2.getJSONObject("" + i);

                            int sub_id = tmpJo.getInt("sub_id");
                            String sub_full_name = tmpJo.getString("full_name");
                            String sub_short_name = tmpJo.getString("short_name");
                            int dow = tmpJo.getInt("dow");
                            String teacher_name = tmpJo.getString("teacher");
//                            String stringStart = extractTimefrom(tmpJo.getString("start_time"));
                            int start_time = tmpJo.getInt("start_time");
//                            String stringEnd = extractTimefrom(tmpJo.getString("end_time"));
                            int end_time = tmpJo.getInt("end_time");
//                            Log.d(TAG, "stringStart = " + stringStart + ", stringEnd = " + stringEnd);


                            ContentValues cv = new ContentValues();
                            cv.put(TTimetable.SUB_ID, sub_id);
                            cv.put(TTimetable.SUB_full, sub_full_name);
                            cv.put(TTimetable.SUB_short, sub_short_name);
                            cv.put(TTimetable.DOW, dow);
                            cv.put(TTimetable.TEACHER, teacher_name);
                            cv.put(TTimetable.START_TIME, start_time);
                            cv.put(TTimetable.END_TIME, end_time);

                            db.insert(TTimetable.NAME, null, cv);
                            Log.d(TAG, i + ") Inserted value for day " + dow + " start: " + start_time + " end: " + end_time);

                            publishProgress(progress + (i * 2));

                        }

                        db.close();
                        dbHelper.close();

                    }

                    MY_ID = jo.getString("my_id");
                    MY_GCM_ID = jo.getString("my_gcm_id");
                    MY_SEM = jo.getInt("my_sem");
                    MY_ROLL = jo.getInt("my_roll");
                    MY_NAME = jo.getString("my_name");

                    SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                    Editor editor = sp.edit();
                    editor.putString("my_student_id", MY_ID);
                    editor.putString("my_gcm_id", MY_GCM_ID);
                    editor.putInt("my_sem", MY_SEM);
                    editor.putInt("my_roll", MY_ROLL);
                    editor.putString("my_name", MY_NAME);
                    editor.apply();

                    publishProgress(80);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            publishProgress(90);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            bar.setProgress(100);
            bar.setVisibility(View.GONE);
            if (r != null) {
                r.run();
            }
        }

    }

}
