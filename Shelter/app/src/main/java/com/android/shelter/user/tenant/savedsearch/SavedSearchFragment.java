package com.android.shelter.user.tenant.savedsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.util.ShelterSavedSearchTask;

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearchFragment extends Fragment {
    private static final String TAG = "MySavedSearchFragment";

    private RecyclerView mSearchesRecyclerView;
    private SavedSearch mSavedSearch;

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
        View view = inflater.inflate(R.layout.fragment_my_searches, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Saved Searches");

        mSearchesRecyclerView = (RecyclerView) view.findViewById(R.id.my_searches_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mSearchesRecyclerView.setLayoutManager(layoutManager);

        mSavedSearch = new SavedSearch();
        mSavedSearch.setId("");
        new ShelterSavedSearchTask(getContext(), "getsearch", "GET", true, null,
                mSavedSearch, new FragmentCallback() {
            @Override
            public void onTaskDone() {
                setupAdapter();
            }
        }).execute();
        setupAdapter();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.post_new_property).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void setupAdapter() {
        if (isAdded()) {
            mSearchesRecyclerView.setAdapter(
                    new SavedSearchAdapter(
                            SavedSearchesLab.get(getContext()).getSavedSearches(), getActivity(),
                            getActivity().getSupportFragmentManager()
                    )
            );
        }
    }
}
