package com.kc.database; // 24 Feb, 03:39 AM

public class TNoticeboard {

    public static final String TABLE_NAME = "Noticeboard";
    public static final String ID         = "_ID";
    public static final String SENDER     = "sender";
    public static final String HEADER     = "header";
    public static final String MESSAGE    = "message";
    public static final String READ       = "read";
    public static final String FAV        = "favorite";
    public static final String DATE       = "date";
    public static final String TIME       = "time";

    public String createTable() {


        return "CREATE TABLE " + TABLE_NAME + " (" +
                ID + " INTEGER, " +
                SENDER + " TEXT NOT NULL, " +
                HEADER + " TEXT NOT NULL, " +
                MESSAGE + " TEXT NOT NULL, " +
                READ + " INTEGER DEFAULT 0," +
                FAV + " INTEGER DEFAULT 0," +
                DATE + " INTEGER, " +
                TIME + " INTEGER ) ;";

    }

}
