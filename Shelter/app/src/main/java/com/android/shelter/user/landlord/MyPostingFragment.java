package com.android.shelter.user.landlord;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.UserSessionManager;
import com.android.shelter.util.ShelterConstants;
import com.android.shelter.util.ShelterPropertyTask;

/**
 * Shows list of posted properties
 * Created by rishi on 5/7/16.
 */
public class MyPostingFragment extends Fragment {
    private static final String TAG = "MyPostingFragment";
    private RecyclerView mPostingRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
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

        String ownerId = UserSessionManager.get(getContext()).getOwnerId();
        new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                ownerId, null, null, null, null, null, null, null,
                new FragmentCallback() {
                    @Override
                    public void onTaskDone() {
                        setupAdapter();
                    }
                }).execute();


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
            mPostingRecyclerView.setAdapter(new MyPostingAdapter(PropertyLab.get(getContext()).getProperties(), getActivity(), getActivity().getSupportFragmentManager()));
        }
    }
}
