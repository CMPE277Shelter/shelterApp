package com.android.shelter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.android.shelter.helper.PropertyImage;
import com.android.shelter.helper.PropertyImageAdapter;
import com.android.shelter.util.ImagePicker;
import com.android.shelter.util.SendEmail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rishi on 5/7/16.
 */
public class MyPostingFragment extends Fragment {
    private static final String TAG = "MyPostingsFragment";
    private RecyclerView mPostingRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.fragment_my_postings, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Postings");


        mPostingRecyclerView = (RecyclerView) view.findViewById(R.id.my_postings_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mPostingRecyclerView.setLayoutManager(layoutManager);
        setupAdapter();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set list in the property list adapter
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mPostingRecyclerView.setAdapter(new PropertyAdapter(PropertyLab.get(getContext()).getProperties()));
        }
    }
}
