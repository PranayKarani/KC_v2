package com.kc.database; // 21 Feb, 04:16 AM

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String NAME    = "local_db";
    static final int    VERSION = 2;

    TTimetable   timetable;
    TNoticeboard noticeboard;

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
        timetable = new TTimetable();
        noticeboard = new TNoticeboard();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(timetable.createTable());
        db.execSQL(noticeboard.createTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == VERSION) {
            db.execSQL(timetable.createTable());
        }

    }
}
