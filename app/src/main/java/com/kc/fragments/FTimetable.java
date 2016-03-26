package com.kc.fragments;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.kc.database.TTimetable;
import com.kc.utilites.Time;

public class FTimeTable extends MyFragment {

    private static final int DAYS = 6;

    private MyAdapter adapter;
    private ViewPager viewPager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AHome.CURRENT_FRAGMENT = AHome.F_TIMETABLE;// CAUTION! particluar activity dependency
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f_timetable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MyAdapter(getChildFragmentManager());

        viewPager = (ViewPager) getActivity().findViewById(R.id.ftimetable_pager);
        viewPager.setAdapter(adapter);


    }

    private static class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int i) {
            Log.d("log", i + ",");
            return FTimeTableDay_page.newInstance(i);
        }

        @Override
        public int getCount() {
            return DAYS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Monday";
                case 1:
                    return "Tuesday";
                case 2:
                    return "Wednesday";
                case 3:
                    return "Thrusday";
                case 4:
                    return "Friday";
                case 5:
                    return "Saturday";
                default:
                    return "Sunday";
            }
        }

    }

    public static class FTimeTableDay_page extends MyFragment {

        int ID;

        static FTimeTableDay_page newInstance(int ID) {
            FTimeTableDay_page ftd = new FTimeTableDay_page();
            Bundle b = new Bundle();
            b.putInt("ID", ID);
            ftd.setArguments(b);
            return ftd;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getArguments() != null) {
                ID = getArguments().getInt("ID");
            } else {
                ID = 0;
            }

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.x_timetable_day, container, false);

            // set the day of the week
            int dayToday = ID + 1;

            // open local db connection and launcg query
            DBHelper dbh = new DBHelper(getContext(), DBHelper.dbType.READ);
            Cursor c = dbh.rawQuery("SELECT * FROM " + TTimetable.NAME + " WHERE " + TTimetable.DOW + " = " + dayToday);

            // go through all cursor results
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {

                // first get the subject id
                int sub_id = c.getInt(c.getColumnIndex(TTimetable.SUB_ID));

                // inflate the layouts
                LinearLayout layout = (LinearLayout) v.findViewById(R.id.tt_holder);
                CardView lec_cell = inflateLectureCell(c);// this is main
                int sub_color_id = (sub_id % 5) + 1;
                int sub_color = getColor(sub_color_id);
                lec_cell.setCardBackgroundColor(getResources().getColor(sub_color));
                layout.addView(lec_cell);

                c.moveToNext();

            }


            return v;
        }

        CardView inflateLectureCell(final Cursor c) {

            // extract the necessary ingo from the cursor
            final Time startTime = new Time(c.getInt(c.getColumnIndex(TTimetable.START_TIME)));
            final Time endTime = new Time(c.getInt(c.getColumnIndex(TTimetable.END_TIME)));
            final String shortName = c.getString(c.getColumnIndex(TTimetable.SUB_short));
            final String fullName = c.getString(c.getColumnIndex(TTimetable.SUB_full));
            final String teacher = c.getString(c.getColumnIndex(TTimetable.TEACHER));

            // calculate duration
            int durH = endTime.hour - startTime.hour, durM;
            if (endTime.min < startTime.min) {
                durM = (endTime.min + 60) - startTime.min;
                durH--;
            } else {
                durM = endTime.min - startTime.min;
            }
            final Time duration = new Time(durH, durM);


            LayoutInflater linf = getActivity().getLayoutInflater();
            CardView lf;
            lf = (CardView) linf.inflate(R.layout.x_timetable_lec, null);


            // Lecture start time
            TextView stimeTV = (TextView) lf.findViewById(R.id.tt_start_time);
            stimeTV.setText(startTime.timeIn24String());

            // Lecture end time
            TextView etimeTV = (TextView) lf.findViewById(R.id.tt_end_time);
            etimeTV.setText(endTime.timeIn24String());

            // lecture name
            TextView nameTV = (TextView) lf.findViewById(R.id.tt_name);
            nameTV.setText(shortName);

            // teacher name
            TextView teachernameTV = (TextView) lf.findViewById(R.id.tt_teacher);
            teachernameTV.setText(teacher);

            // duation time string
            String d;
            if (duration.min > 0) {
                if (duration.hour > 0) {
                    d = duration.hour + "h " + duration.min + "m";
                } else {
                    d = duration.min + "m";
                }
            } else {
                d = duration.hour + "h";
            }
            ((TextView) lf.findViewById(R.id.tt_dur)).setText("(" + d + ")");

            lf.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    String dur;
                    if (duration.min > 0) {
                        if (duration.hour > 0) {
                            dur = duration.hour + "h " + duration.min + "m";
                        } else {
                            dur = duration.min + "m";
                        }
                    } else {
                        dur = duration.hour + "h";
                    }

                    Toast.makeText(getActivity(),
                            fullName + "\n"
                                    + teacher + "\n"
                                    + "from " + startTime.timeIn12String() + " to " + endTime.timeIn12String() + "\n"
                                    + "duration: " + dur,
                            Toast.LENGTH_LONG)
                            .show();

                }
            });

            return lf;
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
