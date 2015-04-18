package com.buzzbd.mirtefa.tolpar;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mirtefa on 4/1/15.
 */
public class App extends Application {
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private static final String PROPERTY_ID = "UA-61964349-1";

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "XGVNcjHi1Srbti86z0gkJZJ4XMir3CWJR1nJfq3f", "RC75wW84OybVFt4lYj8nI7CEQ27Dho7v8MCUnD2b");
    }
}
