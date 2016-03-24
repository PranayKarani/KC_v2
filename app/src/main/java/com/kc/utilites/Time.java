package com.kc.utilites; // 25 Mar, 12:40 AM

public class Time {

    public int completeTime;
    public int hour, min;

    public Time(int hour, int min) {
        this.hour = hour;
        this.min = min;
        completeTime = (hour * 100) + min;
    }

    public Time(int time) {
        time /= 100;
        completeTime = time;
        this.hour = time / 100;
        this.min = time % 100;
    }

    public static String timeIn24String(int time) {
        String t;

        time /= 100;

        int h = time / 100;
        int m = time % 100;

        // is a single digit? yes, concate 0
        if (m < 10) {

            t = h + ":0" + m;

        } else {

            t = h + ":" + m;

        }
        return t;

    }

    public static String timeIn12String(int time) {
        String t;

        time /= 100;

        int h = time / 100;
        int m = time % 100;

        // is a single digit? yes, concate 0
        if (m < 10) {

            // is hour > 12? yes, subtract 12 from it
            if (h > 12) {
                t = (h - 12) + ":0" + m + " pm";
            } else {
                t = h + ":0" + m + " am";
            }

        } else {

            if (h > 12) {
                t = (h - 12) + ":" + m + " pm";
            } else {
                t = h + ":" + m + " am";
            }

        }
        return t;
    }

    /**
     * For Time objects
     *
     * @return
     */
    public String timeIn24String() {
        String t;

        int h = completeTime / 100;
        int m = completeTime % 100;

        // is a single digit? yes, concate 0
        if (m < 10) {

            t = h + ":0" + m;

        } else {

            t = h + ":" + m;

        }
        return t;

    }

    /**
     * For Time objects
     *
     * @return
     */
    public String timeIn12String() {
        String t;

        int h = completeTime / 100;
        int m = completeTime % 100;

        // is a single digit? yes, concate 0
        if (m < 10) {

            // is hour > 12? yes, subtract 12 from it
            if (h > 12) {
                t = (h - 12) + ":0" + m + " pm";
            } else {
                t = h + ":0" + m + " am";
            }

        } else {

            if (h > 12) {
                t = (h - 12) + ":" + m + " pm";
            } else {
                t = h + ":" + m + " am";
            }

        }
        return t;
    }

}
