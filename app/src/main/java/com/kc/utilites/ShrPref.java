package com.kc.utilites; // 24 Feb, 10:18 AM

import android.content.Context;

import static com.kc.C.FILE_NAME;

public class ShrPref {

    public static void writeData(Context c, String key, String data) {

        c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putString(key, data).apply();

    }

    public static void writeData(Context c, String key, int data) {
        c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putInt(key, data).apply();
    }

    public static void writeData(Context c, String key, float data) {
        c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putFloat(key, data).apply();
    }

    public static void writeData(Context c, String key, boolean data) {
        c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, data).apply();
    }

    public static String readData(Context c, String key, String def) {

        return c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(key, def);

    }

    public static int readData(Context c, String key, int def) {
        return c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getInt(key, def);
    }

    public static float readData(Context c, String key, float def) {

        return c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getFloat(key, def);
    }

    public static boolean readData(Context c, String key, boolean def) {
        return c.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getBoolean(key, def);
    }

}
