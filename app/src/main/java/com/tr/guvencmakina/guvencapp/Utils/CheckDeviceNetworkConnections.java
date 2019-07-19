package com.tr.guvencmakina.guvencapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckDeviceNetworkConnections {


public static boolean isConnectedToInternet(Context context){

  ConnectivityManager cm =
          ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE));

    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


    //
//    ConnectionQuality connectionQuality = AndroidNetworking.getCurrentConnectionQuality();
//    if(connectionQuality == ConnectionQuality.EXCELLENT) {
//        // do something
//    } else if (connectionQuality == ConnectionQuality.POOR) {
//        // do something
//    } else if (connectionQuality == ConnectionQuality.UNKNOWN) {
//        // do something
//    }

    return activeNetwork != null &&
            activeNetwork.isConnectedOrConnecting();
}

}
