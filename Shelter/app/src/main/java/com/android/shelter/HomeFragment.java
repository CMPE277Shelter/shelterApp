package com.android.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Fragment for {@link HomeActivity} TODO Make toolbar transparent on landing and scorlling effect.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "On create called");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View v = inflater.inflate(R.layout.content_home, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        LinearLayout startSearch = (LinearLayout) v.findViewById(R.id.start_search);
        startSearch.setClickable(true);

        startSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchActivity);
            }
        });

        return v;
    }
}


