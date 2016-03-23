package com.kc.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.activities.ANoticeViewer;
import com.kc.database.DBHelper;
import com.kc.database.DBHelper.dbType;
import com.kc.database.TNoticeboard;
import com.kc.utilites.ShrPref;

public class FNoticeboard extends MyFragment {

    private Context stupidContext;
    
    public FNoticeboard() {
        // Required empty public constructor
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AHome.CURRENT_FRAGMENT = AHome.F_NOTICEBOARD;
        stupidContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.f_notice, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ShrPref.writeData(stupidContext, "noof_notice", 0);
        new NoticesLoader().execute(null, null, null);
    }

    @Override
    public void onStop() {
        super.onStop();

        LinearLayout urb = (LinearLayout) getActivity().findViewById(R.id.unread_bucket);
        for (int i = 0; i < urb.getChildCount(); i++) {
            if (urb.getChildAt(i) instanceof CardView) {
                urb.removeViewAt(i);
            }
        }

        LinearLayout fb = (LinearLayout) getActivity().findViewById(R.id.fav_bucket);
        for (int i = 0; i < fb.getChildCount(); i++) {
            if (fb.getChildAt(i) instanceof CardView) {
                fb.removeViewAt(i);
            }
        }

        LinearLayout rb = (LinearLayout) getActivity().findViewById(R.id.read_bucket);
        for (int i = 0; i < rb.getChildCount(); i++) {
            if (rb.getChildAt(i) instanceof CardView) {
                rb.removeViewAt(i);
            }
        }
    }

    private class NoticesLoader extends AsyncTask<Void, Void, Cursor> {

        DBHelper dbh;

        @Override
        public Cursor doInBackground(Void[] params) {

            dbh = new DBHelper(stupidContext, dbType.READ);

            return dbh.rawQuery(
                    "SELECT * FROM " + TNoticeboard.TABLE_NAME + " ORDER BY "
                            + TNoticeboard.READ + " ASC,"
                            + TNoticeboard.FAV + " DESC,"
                            + TNoticeboard.DATE + " DESC,"
                            + TNoticeboard.TIME + " DESC");

        }

        @Override
        public void onPostExecute(Cursor c) {

            if (c.getCount() > 0) {

                c.moveToFirst();

                int unread = 0, fav = 0, read = 0;

                // declare buckets
                LinearLayout urb = (LinearLayout) getActivity().findViewById(R.id.unread_bucket);
                LinearLayout fb = (LinearLayout) getActivity().findViewById(R.id.fav_bucket);
                LinearLayout rb = (LinearLayout) getActivity().findViewById(R.id.read_bucket);

                do {

                    int isThisRowUnread = c.getInt(c.getColumnIndex(TNoticeboard.READ));
                    if (isThisRowUnread == 0) { // 0 = Yes, 1 = no, it's read
                        unread++;
                        urb.addView(inflate_unread(c));
                    }

                    int isThisRowFav = c.getInt(c.getColumnIndex(TNoticeboard.FAV));
                    if (isThisRowFav != 0) {
                        fav++;
                        fb.addView(inflate_notice(c, true));
                    }

                    if (isThisRowUnread != 0 && isThisRowFav == 0) {
                        read++;
                        rb.addView(inflate_notice(c, false));
                    }


                } while (c.moveToNext());

                urb.setVisibility(unread == 0 ? View.GONE : View.VISIBLE);
                fb.setVisibility(fav == 0 ? View.GONE : View.VISIBLE);
                rb.setVisibility(read == 0 ? View.GONE : View.VISIBLE);

            }
            c.close();
            dbh.close();

        }

        View inflate_unread(Cursor c) {

            // extract data
            final int ID = c.getInt(c.getColumnIndex(TNoticeboard.ID));
            final String header = c.getString(c.getColumnIndex(TNoticeboard.HEADER));
            final String body = c.getString(c.getColumnIndex(TNoticeboard.MESSAGE));
            final int date = c.getInt(c.getColumnIndex(TNoticeboard.DATE));
            final int time = c.getInt(c.getColumnIndex(TNoticeboard.TIME));


            CardView cv = (CardView) FNoticeboard.this.getActivity().getLayoutInflater().inflate(R.layout.x_notice_unread, null);

            // set header
            ((TextView) cv.findViewById(R.id.unread_notice_header)).setText(header);

            // set body
            int noofLetters = 100;
            String display_body = body.replace("\n", "");
            TextView bodyTextView = (TextView) cv.findViewById(R.id.unread_notice_desc);
            if (display_body.length() > noofLetters) {
                bodyTextView.setText(display_body.substring(0, noofLetters).concat("..."));
            } else {
                bodyTextView.setText(display_body);
            }

            cv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO activate this to read the unread notice
                    Intent i = new Intent(getActivity(), ANoticeViewer.class);

                    i.putExtra("ID", ID);
                    i.putExtra("header", header);
                    i.putExtra("body", body);
                    i.putExtra("fav", false);
                    i.putExtra("date", date);
                    i.putExtra("time", time);
                    startActivity(i);

                }
            });

            return cv;
        }

        View inflate_notice(Cursor c, boolean fav) {

            // extract data
            final int ID = c.getInt(c.getColumnIndex(TNoticeboard.ID));
            final String header = c.getString(c.getColumnIndex(TNoticeboard.HEADER));
            final String body = c.getString(c.getColumnIndex(TNoticeboard.MESSAGE));
            final int date = c.getInt(c.getColumnIndex(TNoticeboard.DATE));
            final int time = c.getInt(c.getColumnIndex(TNoticeboard.TIME));

            CardView cv = (CardView) FNoticeboard.this.getActivity().getLayoutInflater().inflate(R.layout.x_notice_header, null);
            if (fav) {
                cv.setCardBackgroundColor(getResources().getColor(R.color.fav_notice));
            } else {
                cv.setCardBackgroundColor(getResources().getColor(R.color.read_notice));
            }

            // set header
            ((TextView) cv.findViewById(R.id.notice_header)).setText(header);

            cv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO activate this to read the unread notice
                    Intent i = new Intent(getActivity(), ANoticeViewer.class);

                    i.putExtra("ID", ID);
                    i.putExtra("header", header);
                    i.putExtra("body", body);
                    i.putExtra("fav", true);
                    i.putExtra("date", date);
                    i.putExtra("time", time);
                    startActivity(i);

                }
            });

            return cv;
        }

    }


}
