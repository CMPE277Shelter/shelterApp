package com.android.shelter;

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

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearchFragment extends Fragment {
    private static final String TAG = "MySearchesFragment";

    private RecyclerView mSearchesRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(false);
//        for (int i = 0; i < 5; i++) {
//            SavedSearch savedSearch=new SavedSearch();
//            savedSearch.setSavedSearchName("101");
//            savedSearch.setPostingType("Townhouse");
//            savedSearch.setPhotoId(R.color.colorAccent);
//            savedSearch.setCity("San Jose");
//            savedSearch.setZipcode("95112");
//            savedSearch.setMinRent(2000);
//            savedSearch.setMaxRent(5000);
//            SavedSearchesLab.get(getContext()).addSavedSearch(savedSearch);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.fragment_my_searches, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My Searches");

        mSearchesRecyclerView = (RecyclerView) view.findViewById(R.id.my_searches_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mSearchesRecyclerView.setLayoutManager(layoutManager);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Set list in the property list adapter
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            mSearchesRecyclerView.setAdapter(new SavedSearchAdapter(SavedSearchesLab.get(getContext()).getSavedSearches(), getActivity(), getActivity().getSupportFragmentManager()));
        }
    }
}
