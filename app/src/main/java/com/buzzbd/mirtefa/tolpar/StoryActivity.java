package com.buzzbd.mirtefa.tolpar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.buzzbd.mirtefa.tolpar.Fragments.ImageViewFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.MyWebViewFragment;
import com.buzzbd.mirtefa.tolpar.Fragments.StoryFragment;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.greenrobot.event.EventBus;


public class StoryActivity extends ActionBarActivity {
    public static String mPage = EventValues.story;
    public static int sdk = android.os.Build.VERSION.SDK_INT;

    private static String storyTitle = null;
    private static String storyContent = null;
    private static String storyImgUri = null;
    private static String storySourceUrl = null;
    public static String storyObjectId = null;

    public static String mAccessToken = null;

    private static Uri imgUri = null;

    public static ActionBar mActionBar;
    //Webview vars
    public static String targetUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_story);
        mActionBar = getSupportActionBar();
        //Actionbar Stuff
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.brand_red));
        getSupportActionBar().setTitle(getResources().getString(R.string.tolpaar));

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data == null) {
            storyTitle = getIntent().getStringExtra("storyTitle");
            storyContent = getIntent().getStringExtra("storyContent");
            storyImgUri = getIntent().getStringExtra("storyImgUri");
            storySourceUrl = getIntent().getStringExtra("storySourceUrl");
            storyObjectId = getIntent().getStringExtra("storyObjectId");
            launchFragment();
        } else {
            Fresco.initialize(this);
            List<String> params = data.getPathSegments();
            storyObjectId = params.get(1);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
            query.whereEqualTo("objectId", storyObjectId);

            if (isNetworkAvailable()) {
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                        if (e == null) {
                            ParseObject.unpinAllInBackground();
                            ParseObject.pinAllInBackground(parseObjects);
                            ParseObject storyObj = parseObjects.get(0);

                            storyTitle = storyObj.get("Title").toString();
                            storyContent = storyObj.get("Content").toString();
                            storyImgUri = storyObj.get("ImgUri").toString();
                            storyObjectId = storyObj.getObjectId();

                            String sourceUrl = null;
                            if (storyObj.get("SourceUrl") != null)
                                sourceUrl = storyObj.get("SourceUrl").toString();

                            storySourceUrl = sourceUrl;

                            launchFragment();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
            } else {
                query.fromLocalDatastore().findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                        // Update UI
                        if (e == null) {
                            ParseObject.pinAllInBackground(parseObjects);
                            ParseObject storyObj = parseObjects.get(0);

                            storyTitle = storyObj.get("Title").toString();
                            storyContent = storyObj.get("Content").toString();
                            storyImgUri = storyObj.get("ImgUri").toString();
                            storyObjectId = storyObj.getObjectId();

                            String sourceUrl = null;
                            if (storyObj.get("SourceUrl") != null)
                                sourceUrl = storyObj.get("SourceUrl").toString();

                            storySourceUrl = sourceUrl;

                            launchFragment();
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings_story) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "http://tolpar.parseapp.com/story/"+ storyObjectId);
            startActivity(Intent.createChooser(intent, "Share with"));

//            //setup request params
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchFragment() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = null;

        switch (StoryActivity.mPage) {
            case EventValues.story:
                fragment = StoryFragment.newInstance(storyTitle, storyContent, storyImgUri, storySourceUrl);
                break;
            case EventValues.img:
                fragment = ImageViewFragment.newInstance(imgUri);
                break;
            default:
                fragment = MyWebViewFragment.newInstance(targetUrl);
        }

        if (fragment == null) {
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            if (StoryActivity.mPage.equals(EventValues.story)) {
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer,
                                fragment)
                        .commit();
            } else {
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer,
                                fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    public void onEventMainThread(StoryFragment.ClickedShareButton event) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "http://tolpar.parseapp.com/story/"+ storyObjectId);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    public void onEventMainThread(StoryFragment.ClickedImage event) {
        imgUri = event.imgUri;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = ImageViewFragment.newInstance(event.imgUri);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.fragmentContainer,
                            ImageViewFragment.newInstance(event.imgUri))
                    .addToBackStack("tag")
                    .commit();
        }
    }

    public void onEventMainThread(StoryFragment.ClickedNavigateToSource event) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = MyWebViewFragment.newInstance(targetUrl);
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            fm.beginTransaction()
                    .replace(R.id.fragmentContainer,
                            MyWebViewFragment.newInstance(targetUrl))
                    .addToBackStack("tag")
                    .commit();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
