package com.buzzbd.mirtefa.tolpar;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.astuetz.PagerSlidingTabStrip;
import com.buzzbd.mirtefa.tolpar.Fragments.AntorjatikFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.BanijjoFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.BinodonFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.FeedFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.KheladhulaFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.ShironamFragment;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";

    public static String mPage = EventValues.feed;
    public static int sdk = android.os.Build.VERSION.SDK_INT;

    //ViewPager
    private ViewPager mViewPager;
    private TabsPagerAdapter mAdapter;
    private PagerSlidingTabStrip mTabs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fresco.initialize(this);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.pager_title_strip);
        mTabs.setViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.brand_red));
        getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.facebook.appevents.AppEventsLogger.activateApp(this, getResources().getString(R.string.fb_app_id));
    }

    // Events
    public void onEventMainThread(Events.ClickedStory event) {
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("storyTitle", event.title);
        intent.putExtra("storyContent", event.content);
        intent.putExtra("storyImgUri", event.imgUri);
        intent.putExtra("storySourceUrl", event.sourceUrl);
        intent.putExtra("storyObjectId", event.objectId);

        startActivity(intent);
    }

    //ViewPager Adapter
    public class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_TABS = 6;

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            Fragment fragment;
            // Get tracker.
            Tracker t = ((App) getApplication()).getTracker(
                    App.TrackerName.APP_TRACKER);

            // Set screen name.
            String mTitle = null;
            // Send a screen view.
            switch (index) {
                case 0: {
                    fragment = ShironamFragment.newInstance("Shironam");
                    mTitle = "Shironam";
                    break;
                }
                case 1: {
                    fragment = FeedFragment.newInstance("Rajniti");
                    mTitle = "Rajniti";
                    break;
                }
                case 2: {
                    fragment = KheladhulaFragment.newInstance("Kheladhula");
                    mTitle = "Kheladhula";
                    break;
                }
                case 3: {
                    fragment = BinodonFragment.newInstance("Binodon");
                    mTitle = "Binodon";
                    break;
                }
                case 4: {
                    fragment = AntorjatikFragment.newInstance("Antorjatik");
                    mTitle = "Antorjatik";
                    break;
                }
                case 5: {
                    fragment = BanijjoFragment.newInstance("Banijjo");
                    mTitle = "Banijjo";
                    break;
                }
                default:
                    fragment = FeedFragment.newInstance("Rajniti");
                    mTitle = "Rajniti";
            }

            t.setScreenName(mTitle);
            t.send(new HitBuilders.ScreenViewBuilder().build());
            return fragment;
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.shironam);
                case 1:
                    return getResources().getString(R.string.rajniti);
                case 2:
                    return getResources().getString(R.string.kheladhula);
                case 3:
                    return getResources().getString(R.string.binodon);
                case 4:
                    return getResources().getString(R.string.antorjatik);
                case 5:
                    return getResources().getString(R.string.banijjo);
            }
            return null;
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return NUM_TABS;
        }
    }
}
