package com.example.android.flixtrove;

import android.app.Application;

import timber.log.Timber;

public class FlixTroveApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
