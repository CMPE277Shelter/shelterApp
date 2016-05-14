package com.android.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.shelter.util.DownloadImageTask;

/**
 * Fragment for {@link HomeActivity} TODO Make toolbar transparent on landing and scorlling effect.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View v = inflater.inflate(R.layout.content_home, container, false);

        ImageView header = (ImageView)v.findViewById(R.id.header);
        new DownloadImageTask(header).execute
                ("http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=header.jpg");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        return v;
    }
}


