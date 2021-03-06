package com.kc.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kc.C;
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.other.Subject;
import com.kc.utilites.MyJson;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class FAttendance extends MyFragment {

    public FAttendance() {

        titleBar_text = "Attendance";

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AHome.CURRENT_FRAGMENT = AHome.F_ATTENDANCE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.f_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        new AttendanceFetcher(view).execute(null, null, null);

    }

    class AttendanceFetcher extends AsyncTask<Void, Void, Subject[]> {

        float Overall;
        View view;

        public AttendanceFetcher(View view) {
            this.view = view;
        }

        @Override
        protected Subject[] doInBackground(Void... params) {

            Subject[] subjects = new Subject[5];


            try {

                String json = MyJson.getData(getContext(), C.ATTENDANCE_FILE);

                JSONObject jo2 = new JSONObject(json);

                for (int i = 0; i < jo2.length(); i++) {

                    JSONObject tmpJo = jo2.getJSONObject("" + (i + 1));

                    subjects[i] = new Subject();

                    subjects[i].subject_id = tmpJo.getInt("sub_id");
                    subjects[i].held = tmpJo.getInt("held");
                    subjects[i].present = tmpJo.getInt("present");
                    subjects[i].percentage = (float) tmpJo.getDouble("per");
                    subjects[i].full_name = tmpJo.getString("sub_full_name");
                    subjects[i].short_name = tmpJo.getString("sub_short_name");

                    Overall += subjects[i].percentage;


                }

                Overall /= 5;

                Log.i("info", "Overall average: " + (Overall));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return subjects;
        }

        @Override
        protected void onPostExecute(final Subject[] subjects) {
            for (int i = 0; i < subjects.length; i++) {

                LinearLayout layout = (LinearLayout) view.findViewById(R.id.fattendance_subjects);
                layout.addView(inflateAttendanceCard(subjects[i]));

            }

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.fattendance_overall);
            layout.addView(inflateOverallCell());

        }

        CardView inflateAttendanceCard(final Subject s) {
            LayoutInflater linf = getActivity().getLayoutInflater();
            CardView card = (CardView) linf.inflate(R.layout.x_attendance_card, null);

            TextView nameTV = (TextView) card.findViewById(R.id.sub_name);
            nameTV.setText(s.short_name);

            TextView marksTV = (TextView) card.findViewById(R.id.sub_per);
            marksTV.setText(s.percentage + "%");

            if (s.held > 0) {
                card.setCardBackgroundColor(getResources().getColor(getColor((int) s.percentage)));
            } else {
                card.setCardBackgroundColor(getResources().getColor(R.color.not_held));
            }

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String info = s.full_name + "\n" +
                            "Total lectures conducted: " + s.held + "\n" +
                            "Total lectures present: " + s.present + "\n" +
                            "Attendance percentage: " + s.percentage + "%";
                    Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
                }
            });

            return card;
        }

        CardView inflateOverallCell() {
            LayoutInflater linf = getActivity().getLayoutInflater();
            CardView card = (CardView) linf.inflate(R.layout.x_attendance_card, null);

            TextView nameTV = (TextView) card.findViewById(R.id.sub_name);
            nameTV.setText("Overall");

            TextView marksTV = (TextView) card.findViewById(R.id.sub_per);
            marksTV.setText(Overall + "%");

            card.setCardBackgroundColor(getResources().getColor(getColor((int) Overall)));

            return card;
        }

        int getColor(int per) {
            if (per <= 25) {
                return R.color.a_25;
            } else if (per > 25 && per < 50) {
                return R.color.a_25_50;
            } else if (per >= 50 && per < 75) {
                return R.color.a_50_75;
            } else if (per >= 75 && per < 90) {
                return R.color.a_75_90;
            } else {
                return R.color.a_90;
            }
        }

    }

}
