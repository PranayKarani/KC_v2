package com.kc.database; // 26 Mar, 07:15 AM

public class TAttendance {

    public static final String NAME = "Attendance";

    public static final String SUB_1 = "sub_1";
    public static final String SUB_2 = "sub_2";
    public static final String SUB_3 = "sub_3";
    public static final String SUB_4 = "sub_4";
    public static final String SUB_5 = "sub_5";


    public String createTable() {

        return "CREATE TABLE " + NAME + "(" +
                SUB_1 + " TINYINT," +
                SUB_2 + " TINYINT," +
                SUB_3 + " TINYINT," +
                SUB_4 + " TINYINT," +
                SUB_5 + " TINYINT" +
                ")";

    }

}
