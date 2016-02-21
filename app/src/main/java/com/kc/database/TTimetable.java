package com.kc.database; // 21 Feb, 02:58 AM

public class TTimetable {

    public static final String NAME = "Timetable";

    public static final String SUB_ID     = "sub_ID";
    public static final String SUB_full   = "sub_full_name";
    public static final String SUB_short  = "sub_short_name";
    public static final String DOW        = "day_of_week";
    public static final String START_TIME = "start_time";
    public static final String END_TIME   = "end_time";
    public static final String TEACHER    = "teacher";

    public String createTable() {

        return "CREATE TABLE " + NAME + "(" +
                SUB_ID + " TINYINT NOT NULL," +
                SUB_full + " TEXT NOT NULL," +
                SUB_short + " TEXT NOT NULL," +
                DOW + " TINYINT NOT NULL," +
                TEACHER + " TEXT NOT NULL," +
                START_TIME + " INT NOT NULL," +
                END_TIME + " INT NOT NULL" +
                ")";

    }

}
