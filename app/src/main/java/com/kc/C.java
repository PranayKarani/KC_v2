package com.kc; // 17 Feb, 11:13 AM

public class C {

    /* Log levels
    more here: http://stackoverflow.com/questions/7959263/android-log-v-log-d-log-i-log-w-log-e-when-to-use-each-one/7959379#7959379 */

    public static final String FILE_NAME = "studentData";
    public static final String ATTENDANCE_FILE = "attendance.json";
    public static final String GOOGLE_PROJECT_NO = "380016519033";

    public static final String root = "http://192.168.1.101:80/web/kc/";

    public static final String STUDENT_VALIDTY  = root + "validity_check.php?";
    public static final String PASSWORD_CHECKER = root + "password_check.php?";
    public static final String UPDATE_GCM_ID    = root + "update_gcm_id.php";
    public static final String GET_STUDENT_INFO = root + "get_student_info.php?";
    public static final String GET_TIMETABLE    = root + "timetable/get_time_table.php?";
    public static final String GET_MY_ATTENDANCE = root + "attendance/get_my_attendance.php?";
    public static final String GCM_ID_RECHECK   = root + "gcm_id_recheck.php?";

    public static String MY_ID;
    public static String MY_GCM_ID;
    public static String MY_NAME;
    public static int    MY_SEM;
    public static int    MY_ROLL;
    public static String MY_EMAIL;


}
