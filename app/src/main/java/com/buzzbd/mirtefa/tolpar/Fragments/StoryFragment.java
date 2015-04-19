package com.buzzbd.mirtefa.tolpar.Fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.buzzbd.mirtefa.tolpar.App;
import com.buzzbd.mirtefa.tolpar.EventValues;
import com.buzzbd.mirtefa.tolpar.MainActivity;
import com.buzzbd.mirtefa.tolpar.R;
import com.buzzbd.mirtefa.tolpar.StoryActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.shamanland.fab.FloatingActionButton;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class StoryFragment extends Fragment implements View.OnClickListener {
    private static String mTitle;
    private static String mContent;
    private static String mImgUri;
    private static String mSrcUrl;

    @InjectView(R.id.storyImg) SimpleDraweeView storyImgView;
    @InjectView(R.id.storyTitle) TextView storyTitleView;
    @InjectView(R.id.storyContent) TextView storyContentView;
    @InjectView(R.id.sourceNavButton) Button sourceNavButton;
    @InjectView(R.id.share_button) FloatingActionButton shareButton;

    public static StoryFragment newInstance(String title, String content, String imgUri, String srcUrl) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();

        mTitle = title;
        mContent = content;
        mImgUri = imgUri;
        mSrcUrl = srcUrl;
        StoryActivity.targetUrl = mSrcUrl;

        fragment.setArguments(args);
        return fragment;
    }

    public StoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mPage = EventValues.story;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.inject(this, v);

        storyTitleView.setText(mTitle);
//        storyContentView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/SL.ttf"));
        storyContentView.setText(mContent);

        if (mImgUri != null) {
            try {
                URL url = new URL(mImgUri);
                final Uri uri = Uri.parse(mImgUri);
                storyImgView.setImageURI(uri);
            } catch (MalformedURLException e) {
                storyImgView.setImageURI(Uri.parse("http://imgur.com/R5nLbfu"));
                storyImgView.setOnClickListener(this);
            }
        }
        sourceNavButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        storyImgView.setOnClickListener(this);

        if (mSrcUrl != null)
            sourceNavButton.setVisibility(View.VISIBLE);
        else
            sourceNavButton.setVisibility(View.GONE);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mPage = EventValues.story;

        // Get tracker.
        Tracker t = ((App) getActivity().getApplication()).getTracker(
                App.TrackerName.APP_TRACKER);

        // Set screen name.
        t.setScreenName(mTitle);
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Story")
                .setAction("View")
                .setLabel("Title: " + mTitle + ", ObjectId: " + StoryActivity.storyObjectId)
                .build());
    }

    @Override
    public void onClick(View v) {
        Tracker t = ((App) getActivity().getApplication()).getTracker(
                App.TrackerName.APP_TRACKER);

        switch (v.getId()) {
            case R.id.sourceNavButton: {
                StoryActivity.mPage = EventValues.web;
                EventBus.getDefault().post(new ClickedNavigateToSource());
                break;
            }
            case R.id.storyImg: {
                StoryActivity.mPage = EventValues.img;
                final Uri uri = Uri.parse(mImgUri);
                EventBus.getDefault().post(new ClickedImage(uri));
                break;
            }
            case R.id.share_button: {
                EventBus.getDefault().post(new ClickedShareButton());
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Story")
                        .setAction("Share")
                        .setLabel("Title: " + mTitle + ", ObjectId: " + StoryActivity.storyObjectId)
                        .build());
                break;
            }
        }
    }

    public class ClickedImage {
        public Uri imgUri;
        public ClickedImage(Uri imgUri) {
            this.imgUri = imgUri;
        }
    }

    public class ClickedNavigateToSource {}

    public class ClickedShareButton {}
}
