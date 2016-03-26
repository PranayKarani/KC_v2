package com.kc.utilites; // 26 Mar, 09:35 PM

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Network {

    public static boolean isConnectedToInternet(Activity activity) {
        ConnectivityManager conman = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conman.getActiveNetworkInfo();
        boolean isConnected = netInfo != null && netInfo.getState().equals(NetworkInfo.State.CONNECTED);
        return isConnected;
    }

}
