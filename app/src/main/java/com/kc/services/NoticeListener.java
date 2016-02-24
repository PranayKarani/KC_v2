package com.kc.services; // 24 Feb, 02:41 AM

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.kc.R;
import com.kc.activities.AHome;
import com.kc.database.DBHelper;
import com.kc.database.DBHelper.dbType;
import com.kc.database.TNoticeboard;
import com.kc.utilites.ShrPref;

public class NoticeListener extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("log", "onMessageReceived: incoming!!");
        generateNotification(data);
    }

    void generateNotification(final Bundle data) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                DBHelper dbh = new DBHelper(NoticeListener.this, dbType.WRITE);

                ContentValues cv = new ContentValues();
                cv.put(TNoticeboard.ID, data.getString("n_id"));
                cv.put(TNoticeboard.HEADER, data.getString("header"));
                cv.put(TNoticeboard.MESSAGE, data.getString("message"));
                cv.put(TNoticeboard.SENDER, data.getString("sender"));
                cv.put(TNoticeboard.DATE, data.getString("date"));
                cv.put(TNoticeboard.TIME, data.getString("time"));

                dbh.insert(TNoticeboard.TABLE_NAME, cv);

                dbh.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {

                int noticeID = Integer.parseInt(data.getString("n_id"));//auto incremented stuff from our db

                Intent intent = new Intent(NoticeListener.this, AHome.class).putExtra("show_this", AHome.F_NOTICEBOARD).putExtras(data);
                PendingIntent pi = PendingIntent.getActivity(NoticeListener.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                Builder builder = new Builder(NoticeListener.this);
                builder.setSmallIcon(R.drawable.common_full_open_on_phone);
                builder.setContentTitle("KC Notice");
                builder.setContentText(data.getString("header"));
                builder.setAutoCancel(true);
                builder.setContentIntent(pi);

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(noticeID, builder.build());

                int noofNotice = ShrPref.readData(NoticeListener.this, "noof_notice", 0);
                ShrPref.writeData(NoticeListener.this, "noof_notice", ++noofNotice);

            }
        }.execute(null, null, null);

    }

}
