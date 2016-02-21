package com.kc.other; // 21 Feb, 06:15 PM

public class Lecture {

    public int    sub_id;
    public String full_name, short_name;
    public int    dow;
    public String teacher;
    public int    start_time, end_time;

    public Lecture() {
    }

    public Lecture(int sub_id, String full_name, String short_name, int dow, String teacher, int start_tme, int end_time) {
        this.sub_id = sub_id;
        this.full_name = full_name;
        this.short_name = short_name;
        this.dow = dow;
        this.teacher = teacher;
        this.start_time = start_tme;
        this.end_time = end_time;
    }

    public String getFormatedStartTime() {

        int h = start_time / 10000;
        int m = (start_time % 10000) / 100;

        if (m < 10) {
            return h + ":0" + m;
        } else {
            return h + ":" + m;
        }

    }

    public String getFormatedEndTime() {

        int h = end_time / 10000;
        int m = (end_time % 10000) / 100;

        if (m < 10) {
            return h + ":0" + m;
        } else {
            return h + ":" + m;
        }

    }

    public String getFormatedTime(int time) {

        int h = time / 10000;
        int m = (time % 10000) / 100;

        if (m < 10) {
            return h + ":0" + m;
        } else {
            return h + ":" + m;
        }

    }

}
