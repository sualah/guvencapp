package com.tr.guvencmakina.guvencapp.Utils;



import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    private static App sApp;
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sApp = this;
    }

    public static App getApp() {
        return sApp;
    }



}