package com.kc.activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.kc.R;
import com.kc.database.DBHelper;
import com.kc.database.DBHelper.dbType;
import com.kc.database.TTimetable;
import com.kc.fragments.*;
import com.kc.utilites.MyJson;
import com.kc.utilites.Network;
import com.kc.utilites.RemoteDatabaseConnecter;
import com.kc.utilites.ShrPref;
import org.json.JSONException;
import org.json.JSONObject;

import static com.kc.C.*;

public class AHome extends MyActivity {

    public static final int F_HOME = 1;
    public static final int F_NOTICEBOARD = 2;
    public static final int F_TIMETABLE = 3;
    public static final int F_DETAILS = 4;
    public static final int F_ATTENDANCE = 5;
    public static int CURRENT_FRAGMENT = 0;


    String[] drawerTitles;
    DrawerLayout drawerLayout;
    ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ahome_toolbar);
        setSupportActionBar(toolbar);

        /*
         * This is a runnable that contains fragment loading code that will run only when our
         * infoUpdater has finished its job.
         */
        FragmentLoader loadFragmentAfterInfoRefresh = new FragmentLoader();
        new InfoRefresher(loadFragmentAfterInfoRefresh).execute(null, null, null);


        /**
         * Drawer setup
         */
        drawerTitles = getResources().getStringArray(R.array.drawer_titles);
        drawerLayout = (DrawerLayout) findViewById(R.id.ahome_layout);

        drawerList = (ListView) findViewById(R.id.ahome_drawer_listview);
        ArrayAdapter<String> aa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerTitles);
        drawerList.setAdapter(aa);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.info:
                Toast.makeText(
                        this,
                        "My ID:  " + MY_ID + "\n" +
                                "My NAME:  " + MY_NAME + "\n" +
                                "My SEM:  " + MY_SEM + "\n" +
                                "My ROLL:  " + MY_ROLL + "\n\n" +
                                "My_GCM_ID:\n" + MY_GCM_ID,
                        Toast.LENGTH_LONG).show();
                return true;

            case R.id.logout:
                SharedPreferences sp = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                Toast.makeText(this, "logged out!", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {

        if (CURRENT_FRAGMENT == F_HOME) {
            new DialogFragment() {

                /* Exit */

                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {

                    AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                    ab.setMessage("Exit app?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                }
                            });

                    return ab.create();
                }

            }.show(getSupportFragmentManager(), "ExitConfirmation_tag");

        } else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.ahome_framelayout, new FHome());
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
            invalidateOptionsMenu();

        }
    }

    /// Other classes

    /**
     * Initial fragment loading code
     */
    class FragmentLoader implements Runnable {

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
    }

    /**
     * Needed for:
     * 1. my semester change
     * 2. download new timetable on semester change
     * 3. attendance update
     */
    class InfoRefresher extends AsyncTask<Void, Integer, Void> {

        //        boolean isConnected = false;
        ProgressBar bar;
        Runnable r;

        public InfoRefresher(Runnable r) {
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

            if (new Network().isConnectedToInternet(AHome.this)) {

                String url;

                // Get student details
                try {
                    url = GET_STUDENT_INFO + "student_id=" + MY_ID;
                    RemoteDatabaseConnecter rdc1 = new RemoteDatabaseConnecter("GET", url).connect(null);
                    JSONObject jo = rdc1.getJSONObject();
                    publishProgress(30);

                    MY_ID = jo.getString("my_id");
                    MY_GCM_ID = jo.getString("my_gcm_id");
                    MY_SEM = jo.getInt("my_sem");
                    MY_ROLL = jo.getInt("my_roll");
                    MY_NAME = jo.getString("my_name");
                    MY_EMAIL = jo.getString("my_email") == null ? "-" : jo.getString("my_email");
                    // TODO read student batch too
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // read time table from the external database
                DBHelper dbHelper = new DBHelper(AHome.this, dbType.WRITE);

                Log.d(TAG, "doInBackground: deleting timetable...");

                // but empty the time table first before inserting new
                dbHelper.execSQL("DELETE FROM " + TTimetable.NAME);

                Log.d(TAG, "doInBackground: deleted!");

                url = GET_TIMETABLE + "my_sem=" + MY_SEM;
                RemoteDatabaseConnecter rdc2 = new RemoteDatabaseConnecter("GET", url).connect(null);

                JSONObject jo2 = rdc2.getJSONObject();
                int progress = 30;
                for (int i = 1; i <= jo2.length(); i++) {

                    JSONObject tmpJo = null;
                    try {
                        tmpJo = jo2.getJSONObject("" + i);
                        int sub_id = tmpJo.getInt("sub_id");
                        String sub_full_name = tmpJo.getString("full_name");
                        String sub_short_name = tmpJo.getString("short_name");
                        int dow = tmpJo.getInt("dow");
                        String teacher_name = tmpJo.getString("teacher");
                        int start_time = tmpJo.getInt("start_time");
                        int end_time = tmpJo.getInt("end_time");


                        ContentValues cv = new ContentValues();
                        cv.put(TTimetable.SUB_ID, sub_id);
                        cv.put(TTimetable.SUB_full, sub_full_name);
                        cv.put(TTimetable.SUB_short, sub_short_name);
                        cv.put(TTimetable.DOW, dow);
                        cv.put(TTimetable.TEACHER, teacher_name);
                        cv.put(TTimetable.START_TIME, start_time);
                        cv.put(TTimetable.END_TIME, end_time);

                        dbHelper.insert(TTimetable.NAME, cv);
                        Log.d(TAG, i + ") Inserted value for day " + dow + " start: " + start_time + " end: " + end_time);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    publishProgress(progress + (i * 2));

                }

                dbHelper.close();


                // Get attendance details and write them in file in json format
                url = GET_MY_ATTENDANCE + "current_sem=" + MY_SEM + "&student_id=" + MY_ID;
                RemoteDatabaseConnecter rdc3 = new RemoteDatabaseConnecter("GET", url).connect(null);
                MyJson.saveData(AHome.this, rdc3.rawData, ATTENDANCE_FILE);

                ShrPref.writeData(AHome.this, "my_student_id", MY_ID);
                ShrPref.writeData(AHome.this, "my_gcm_id", MY_GCM_ID);
                ShrPref.writeData(AHome.this, "my_sem", MY_SEM);
                ShrPref.writeData(AHome.this, "my_roll", MY_ROLL);
                ShrPref.writeData(AHome.this, "my_name", MY_NAME);
                ShrPref.writeData(AHome.this, "my_email", MY_EMAIL);

                Log.d(TAG, rdc3.rawData);

                publishProgress(80);


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

    class DrawerItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            FragmentManager fragMa = getSupportFragmentManager();
            Fragment f;

            switch (position) {
                case 0:
                    // NOticeboard
                    f = new FNoticeboard();
                    break;
                case 1:
                    // Attendance
                    f = new FAttendance();
                    break;
                case 2:
                    // time table
                    f = new FTimeTable();
                    break;
                case 3:
                    // My Details
                    f = new FDetails();
                    break;

                default:
                    // home
                    f = new FHome();
                    break;
            }

            FragmentTransaction fragTa = fragMa.beginTransaction();
            fragTa.replace(R.id.ahome_framelayout, f);
            fragTa.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragTa.commit();

            setTitle(drawerTitles[position]);
            drawerList.setItemChecked(position, true);

            LinearLayout dll = (LinearLayout) findViewById(R.id.ahome_drawer_layout);
            drawerLayout.closeDrawer(dll);

        }

    }
}
