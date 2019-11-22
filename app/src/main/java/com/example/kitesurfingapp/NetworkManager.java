package com.example.kitesurfingapp;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkManager {

    public NetworkManager() {}

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
