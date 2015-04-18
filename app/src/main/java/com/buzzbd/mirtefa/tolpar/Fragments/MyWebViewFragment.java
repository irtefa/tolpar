package com.buzzbd.mirtefa.tolpar.Fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.buzzbd.mirtefa.tolpar.EventValues;
import com.buzzbd.mirtefa.tolpar.R;
import com.buzzbd.mirtefa.tolpar.StoryActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyWebViewFragment extends android.support.v4.app.Fragment {

    private static WebView mWebView;
    private static String mUrl = null;
    private static MyWebViewFragment sWV;
    public static ProgressBar mProgressBar;

    public MyWebViewFragment() {
        // Required empty public constructor
    }

    public static MyWebViewFragment newInstance(String url) {
        mUrl = url;
        sWV = new MyWebViewFragment();
        Bundle args = new Bundle();
        return  sWV;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        View v =  inflater.inflate(R.layout.fragment_web_view, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        mWebView = (WebView) v.findViewById(R.id.webView1);
        final WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                if(progress < 100 && mProgressBar.getVisibility() == ProgressBar.GONE){
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
                mProgressBar.setProgress(progress);
                if(progress > 90) {
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        mWebView.loadUrl(mUrl);



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        StoryActivity.mPage = EventValues.story;
    }
}
