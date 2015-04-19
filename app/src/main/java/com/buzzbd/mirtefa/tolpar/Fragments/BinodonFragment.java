package com.buzzbd.mirtefa.tolpar.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buzzbd.mirtefa.tolpar.EventValues;
import com.buzzbd.mirtefa.tolpar.Events;
import com.buzzbd.mirtefa.tolpar.MainActivity;
import com.buzzbd.mirtefa.tolpar.R;
import com.etsy.android.grid.StaggeredGridView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


public class BinodonFragment extends Fragment {
    @InjectView(R.id.binodon_grid_view) StaggeredGridView mFeedGridView;
    @InjectView(R.id.binodon_swipe_container) SwipeRefreshLayout swipeLayout;

    public static FeedAdapter adapter = null;
    public static ArrayList<ParseObject> stories;
    public static String mTag = null;

    public BinodonFragment() {
        // Required empty public constructor
    }

    public static BinodonFragment newInstance(String tag) {
        BinodonFragment fragment = new BinodonFragment();
        Bundle args = new Bundle();
        mTag = tag;
        return  fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stories = new ArrayList<ParseObject>();
        MainActivity.mPage = EventValues.feed;
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_binodon, container, false);
        ButterKnife.inject(this, v);
        adapter = new FeedAdapter(getActivity(), stories);
        mFeedGridView.setAdapter(adapter);
        mFeedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (stories.size() > 0) {
                    ParseObject storyObj = stories.get(position);
                    final String title = storyObj.get("Title").toString();
                    final String content = storyObj.get("Content").toString();
                    final String imgUri = storyObj.get("ImgUri").toString();
                    final String objectId = storyObj.getObjectId();

                    String sourceUrl = null;
                    if (storyObj.get("SourceUrl") != null)
                        sourceUrl = storyObj.get("SourceUrl").toString();

                    EventBus.getDefault().post(new Events.ClickedStory(title, content, imgUri, sourceUrl, objectId));
                }
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                loadData();
            }
        });
        swipeLayout.setColorScheme(
                R.color.brand_red);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mPage = EventValues.feed;
    }

    class ViewHolder {
        @InjectView(R.id.feedItemImage) SimpleDraweeView feedItemImage;
        @InjectView(R.id.feedItemTitle) TextView feedItemTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    //Adapter
    public class FeedAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<ParseObject> mStories;

        public FeedAdapter(Context c, ArrayList<ParseObject> items) {
            inflater = ( LayoutInflater )c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mStories = items;
        }

        public int getCount() {
            return mStories.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = inflater.inflate(R.layout.feed_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            try {
                URL url = new URL(mStories.get(position).get("ImgUri").toString());
                Uri uri = Uri.parse(mStories.get(position).get("ImgUri").toString());
                holder.feedItemImage.setImageURI(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                holder.feedItemImage.setImageURI(Uri.parse("http://i.imgur.com/R5nLbfu.png"));
            }
//            holder.feedItemTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/SL.ttf"));
            holder.feedItemTitle.setText(mStories.get(position).get("Title").toString());
            return convertView;
        }
    }

    public void loadData() {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Story");
        query.whereEqualTo("Tag", mTag);
        query.orderByDescending("createdAt");
        query.setLimit(30);

        if (isNetworkAvailable()) {
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                    if (e == null) {
                        ParseObject.unpinAllInBackground();
                        stories.clear();
                        stories.addAll(parseObjects);
                        adapter.notifyDataSetChanged();
                        ParseObject.pinAllInBackground(parseObjects);
                        swipeLayout.setRefreshing(false);
                        pDialog.hide();
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
                        stories.clear();
                        stories.addAll(parseObjects);
                        adapter.notifyDataSetChanged();
                        ParseObject.pinAllInBackground(parseObjects);
                        swipeLayout.setRefreshing(false);
                        pDialog.hide();
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
