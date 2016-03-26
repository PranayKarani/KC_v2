package com.kc.fragments;


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
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.other.Subject;
import com.kc.utilites.MyJson;
import com.kc.utilites.Network;
import com.kc.utilites.RemoteDatabaseConnecter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.kc.C.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class FAttendance extends Fragment {

    public FAttendance() {
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

            String url;
            url = GET_MY_ATTENDANCE + "current_sem=" + MY_SEM + "&student_id=" + MY_ID;
            RemoteDatabaseConnecter rdc2;
            try {

                if (Network.isConnectedToInternet(getActivity())) {
                    rdc2 = new RemoteDatabaseConnecter("GET", url).connect(null);
                    MyJson.saveData(getContext(), rdc2.rawData);
                }

                String json = MyJson.getData(getContext());

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

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return subjects;
        }

        @Override
        protected void onPostExecute(final Subject[] subjects) {
            for (int i = 0; i < subjects.length; i++) {

                LinearLayout layout = (LinearLayout) view.findViewById(R.id.fattendance_layout);
                layout.addView(inflateAttendanceCard(subjects[i]));

            }

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.fattendance_layout);
            layout.addView(inflateOverallCell());

        }

        CardView inflateAttendanceCard(final Subject s) {
            LayoutInflater linf = getActivity().getLayoutInflater();
            CardView card = (CardView) linf.inflate(R.layout.x_attendance_card, null);

            TextView nameTV = (TextView) card.findViewById(R.id.sub_name);
            nameTV.setText(s.short_name);

            TextView marksTV = (TextView) card.findViewById(R.id.sub_per);
            marksTV.setText(s.percentage + "%");

            card.setCardBackgroundColor(getResources().getColor(getColor((int) s.percentage)));

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
