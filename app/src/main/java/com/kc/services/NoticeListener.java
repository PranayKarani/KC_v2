package com.kc.services; // 24 Feb, 02:41 AM

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.kc.database.DBHelper;

public class NoticeListener extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("log", "onMessageReceived: incoming!!");
    }

    void generateNotification(Bundle data) {

        DBHelper dbh = new DBHelper(this);
        SQLiteDatabase db;

    }

}
