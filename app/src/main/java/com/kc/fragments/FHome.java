package com.kc.fragments;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.database.DBHelper;
import com.kc.database.DBHelper.dbType;
import com.kc.database.TNoticeboard;
import com.kc.database.TTimetable;
import com.kc.other.Lecture;
import com.kc.utilites.ShrPref;

import java.util.Calendar;

public class FHome extends MyFragment {

    Context stupidContext;

    int noof_notices;

    public FHome() {
        titleBar_text = "Home";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        stupidContext = context;
        AHome.CURRENT_FRAGMENT = AHome.F_HOME;
        noof_notices = ShrPref.readData(context, "noof_notice", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.f_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LocalTimetableFetcher(view).execute(null, null, null);

        new NoticeDisplayer().execute(null, null, null);

    }

    private class LocalTimetableFetcher extends AsyncTask<Void, Void, Lecture[]> {

        View view;

        public LocalTimetableFetcher(View view) {
            this.view = view;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Lecture[] doInBackground(Void... params) {

            // load today's schedule
            int dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

            DBHelper dbh = new DBHelper(stupidContext, dbType.READ);

            Cursor c = dbh.rawQuery("SELECT * FROM " + TTimetable.NAME + " WHERE " + TTimetable.DOW + " = " + dayToday);

            Lecture[] lecs = new Lecture[c.getCount()];

            while (c.moveToNext()) {

                Lecture l = new Lecture();

                l.sub_id = c.getInt(c.getColumnIndex(TTimetable.SUB_ID));
                l.full_name = c.getString(c.getColumnIndex(TTimetable.SUB_full));
                l.short_name = c.getString(c.getColumnIndex(TTimetable.SUB_short));
                l.dow = c.getInt(c.getColumnIndex(TTimetable.DOW));
                l.teacher = c.getString(c.getColumnIndex(TTimetable.TEACHER));
                l.start_time = c.getInt(c.getColumnIndex(TTimetable.START_TIME));
                l.end_time = c.getInt(c.getColumnIndex(TTimetable.END_TIME));

                lecs[c.getPosition()] = l;

            }


            return lecs;
        }

        @Override
        protected void onPostExecute(final Lecture[] lecs) {

            LinearLayout container = (LinearLayout) view.findViewById(R.id.fhome_schedules_container);

            for (int i = 0; i < lecs.length; i++) {
                CardView lectureCardview = (CardView) FHome.this.getActivity().getLayoutInflater().inflate(R.layout.x_lec_data, null);

                /**
                 * setup the lecture cardview
                 */
//                CardView lectureCardview = (CardView) cardView.findViewById(R.id.xScheduleData_actual_container);
                int sub_color_id = (lecs[i].sub_id % 5) + 1;
                int sub_color = getColor(sub_color_id);
                Log.d("log", "sub_id " + sub_color_id);
                lectureCardview.setCardBackgroundColor(getResources().getColor(sub_color));// TODO color based on attendance here

                TextView n = (TextView) lectureCardview.findViewById(R.id.xScheduleData_sub_name);
                n.setText(lecs[i].short_name);

                TextView t = (TextView) lectureCardview.findViewById(R.id.xScheduleData_sub_stime);
                t.setText(lecs[i].getFormatedStartTime());

                final int finalI = i;
                lectureCardview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(stupidContext,
                                "Lecture name: " + lecs[finalI].full_name + "\n" +
                                        "Subject ID: " + lecs[finalI].sub_id + "\n" +
                                        "teacher: " + lecs[finalI].teacher + "\n" +
                                        "Start time: " + lecs[finalI].getFormatedStartTime() + "\n" +
                                        "End time: " + lecs[finalI].getFormatedEndTime(),

                                Toast.LENGTH_LONG).show();

                    }
                });

                container.addView(lectureCardview);

            }

        }

        public int getColor(int code) {
            switch (code) {
                case 1:
                    return R.color.sub_1;
                case 2:
                    return R.color.sub_2;
                case 3:
                    return R.color.sub_3;
                case 4:
                    return R.color.sub_4;
                case 5:
                    return R.color.sub_5;
                default:
                    return R.color.sub_1;// TODO change this default color
            }
        }
    }

    private class NoticeDisplayer extends AsyncTask<Void, Void, Cursor> {

        Activity stupidActivity;

        @Override
        protected void onPreExecute() {
            stupidActivity = FHome.this.getActivity();
        }

        @Override
        protected Cursor doInBackground(Void... params) {

            DBHelper dbh = new DBHelper(stupidContext, dbType.READ);

            return dbh.rawQuery(
                    "SELECT * FROM " + TNoticeboard.TABLE_NAME + " WHERE " + TNoticeboard.READ + " = 0 ORDER BY "
                            + TNoticeboard.READ + " ASC,"
                            + TNoticeboard.FAV + " DESC,"
                            + TNoticeboard.DATE + " DESC,"
                            + TNoticeboard.TIME + " DESC");

        }

        @Override
        protected void onPostExecute(Cursor c) {
            LinearLayout home_notice_container = (LinearLayout) stupidActivity.findViewById(R.id.fhome_Notification_container);
            if (c.getCount() > 0) {
                stupidActivity.findViewById(R.id.fhome_Notification_header).setVisibility(View.VISIBLE);
                c.moveToFirst();
                do {

                    CardView cardView = (CardView) stupidActivity.getLayoutInflater().inflate(R.layout.x_notice_header, null);

                    TextView tv = (TextView) cardView.findViewById(R.id.notice_header);

                    String notice_header = c.getString(c.getColumnIndex(TNoticeboard.HEADER));

                    tv.setText(notice_header);

                    home_notice_container.addView(cardView);

                    cardView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FragmentTransaction ft = FHome.this.getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.ahome_framelayout, new FNoticeboard());
                            ft.addToBackStack(null);
                            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            ft.commit();

                        }
                    });


                } while (c.moveToNext());
            } else {

                stupidActivity.findViewById(R.id.fhome_Notification_header).setVisibility(View.GONE);

            }
            c.close();
        }
    }

}
