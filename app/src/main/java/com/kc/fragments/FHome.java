package com.kc.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.database.DBHelper;
import com.kc.database.TTimetable;
import com.kc.other.Lecture;

import java.util.Calendar;

public class FHome extends MyFragment {

    Context stupidContext;
    
    public FHome() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        stupidContext = context;
        AHome.CURRENT_FRAGMENT = AHome.F_HOME;
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
    }

    class LocalTimetableFetcher extends AsyncTask<Void, Void, Lecture[]> {

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
            int dayToday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            DBHelper dbh = new DBHelper(stupidContext);
            SQLiteDatabase db = dbh.getReadableDatabase();

            Cursor c = db.rawQuery("SELECT * FROM " + TTimetable.NAME + " WHERE " + TTimetable.DOW + " = " + dayToday, null);
            c.moveToFirst();

            Lecture[] lecs = new Lecture[c.getCount()];

            do {

                Lecture l = new Lecture();

                l.sub_id = c.getInt(c.getColumnIndex(TTimetable.SUB_ID));
                l.full_name = c.getString(c.getColumnIndex(TTimetable.SUB_full));
                l.short_name = c.getString(c.getColumnIndex(TTimetable.SUB_short));
                l.dow = c.getInt(c.getColumnIndex(TTimetable.DOW));
                l.teacher = c.getString(c.getColumnIndex(TTimetable.TEACHER));
                l.start_time = c.getInt(c.getColumnIndex(TTimetable.START_TIME));
                l.end_time = c.getInt(c.getColumnIndex(TTimetable.END_TIME));

                lecs[c.getPosition()] = l;

            } while (c.moveToNext());


            return lecs;
        }

        @Override
        protected void onPostExecute(Lecture[] lecs) {

            LinearLayout container = (LinearLayout) view.findViewById(R.id.fhome_schedules_container);

            for (int i = 0; i < lecs.length; i++) {
                LinearLayout scheduleData = (LinearLayout) FHome.this.getActivity().getLayoutInflater().inflate(R.layout.x_schedule_data, null);

                CardView actualContainer = (CardView) scheduleData.findViewById(R.id.xScheduleData_actual_container);
                int sub_color_id = lecs[i].sub_id % 5;
                int sub_color = getColor(sub_color_id);
                actualContainer.setCardBackgroundColor(getResources().getColor(sub_color));

                TextView n = (TextView) scheduleData.findViewById(R.id.xScheduleData_sub_name);
                n.setText(lecs[i].short_name);

                TextView t = (TextView) scheduleData.findViewById(R.id.xScheduleData_sub_stime);
                t.setText(lecs[i].getFormatedTime(lecs[i].start_time));

                // todo add clickListener to cardView and pass in lec details

                container.addView(scheduleData);

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

}
