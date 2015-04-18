package com.buzzbd.mirtefa.tolpar.Fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzbd.mirtefa.tolpar.EventValues;
import com.buzzbd.mirtefa.tolpar.R;
import com.buzzbd.mirtefa.tolpar.StoryActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewFragment extends Fragment {

    @InjectView(R.id.img) SimpleDraweeView mImageView;

    private static Uri mImgUri;

    public ImageViewFragment() {

    }

    public static ImageViewFragment newInstance(Uri imgUri) {
        ImageViewFragment fragment = new ImageViewFragment();
        mImgUri = imgUri;
        Bundle args = new Bundle();
        return  fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_image_view, container, false);
        ButterKnife.inject(this, v);

        mImageView.setImageURI(mImgUri);
        mImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // go back
                getFragmentManager().popBackStackImmediate();
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        StoryActivity.mPage = EventValues.img;
        StoryActivity.mActionBar.hide();
    }

    @Override
    public void onPause() {
        super.onPause();
        StoryActivity.mPage = EventValues.story;
        StoryActivity.mActionBar.show();
    }
}
