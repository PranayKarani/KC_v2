package com.kc.other; // 21 Feb, 06:15 PM

import com.kc.utilites.Time;

public class Lecture {

    public int    sub_id;
    public String full_name, short_name;
    public int    dow;
    public String teacher;
    public Time start_time, end_time;

    public Lecture() {
    }
//    public String getFormatedStartTime() {
//
//        int h = start_time / 10000;
//        int m = (start_time % 10000) / 100;
//
//        if (m < 10) {
//            return h + ":0" + m;
//        } else {
//            return h + ":" + m;
//        }
//
//    }
//
//    public String getFormatedEndTime() {
//
//        int h = end_time / 10000;
//        int m = (end_time % 10000) / 100;
//
//        if (m < 10) {
//            return h + ":0" + m;
//        } else {
//            return h + ":" + m;
//        }
//
//    }
//
//    public String getFormatedTime(int time) {
//
//        int h = time / 10000;
//        int m = (time % 10000) / 100;
//
//        if (m < 10) {
//            return h + ":0" + m;
//        } else {
//            return h + ":" + m;
//        }
//
//    }

}
