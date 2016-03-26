package com.kc.database; // 21 Feb, 04:16 AM

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String NAME    = "local_db";
    static final int VERSION = 4;
    public SQLiteDatabase db;
    TTimetable   timetable;
    TNoticeboard noticeboard;

    public DBHelper(Context context, dbType type) {
        super(context, NAME, null, VERSION);
        timetable = new TTimetable();
        noticeboard = new TNoticeboard();

        if (type.equals(dbType.WRITE)) {
            db = getWritableDatabase();
        } else {
            db = getReadableDatabase();
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(timetable.createTable());
        db.execSQL(noticeboard.createTable());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion == 2) {
            db.execSQL(timetable.createTable());
        }

        if (newVersion == 3) {
            db.execSQL(noticeboard.createTable());
        }


    }

    @Override
    public synchronized void close() {// todo learn about synchronized method
        super.close();
        db.close();
    }

    public Cursor rawQuery(String query) {
        return db.rawQuery(query, null);
    }

    public void execSQL(String query) {
        db.execSQL(query);
    }

    public long insert(String table_name, ContentValues cv) {
        return db.insert(table_name, null, cv);
    }

    public int update(String tableName, ContentValues cv, String where, String[] args) {
        return db.update(tableName, cv, where, args);
    }

    public int delete(String tableName, String where, String[] args) {
        return db.delete(tableName, where, args);
    }

    public enum dbType {
        READ, WRITE
    }

}
