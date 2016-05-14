package com.android.shelter.user.tenant.favorite;


import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.Location;
import com.android.shelter.user.landlord.MyPostingAdapter;
import com.android.shelter.user.tenant.savedsearch.SavedSearch;
import com.android.shelter.user.tenant.search.SearchPropertyAdapter;
import com.android.shelter.user.tenant.search.SearchPropertyFilterCriteria;
import com.android.shelter.user.tenant.search.SearchPropertyFilterFragment;
import com.android.shelter.user.tenant.search.SearchPropertySaveSearchFragment;
import com.android.shelter.util.ShelterConstants;
import com.android.shelter.util.ShelterFavoriteTask;
import com.android.shelter.util.ShelterPropertyTask;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    private static final String TAG = "MyFavoritesFragment";
    private RecyclerView mPostingRecyclerView;
    private SearchPropertyAdapter mPostingAdapter;
    private FavoriteCriteria mFavoriteCriteria;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.post_new_property).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Favorites");

        
        mPostingRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_favorites_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mPostingRecyclerView.setLayoutManager(layoutManager);

        mFavoriteCriteria=new FavoriteCriteria();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mFavoriteCriteria.setUser(preferences.getString(ShelterConstants.SHARED_PREFERENCE_USER_NAME
                ,ShelterConstants.DEFAULT_STRING));

        new ShelterFavoriteTask(getActivity().getApplicationContext(), "GET","favorites", true, mFavoriteCriteria,
                new FragmentCallback() {
            @Override
            public void onTaskDone() {
                mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                        getActivity(), getActivity().getSupportFragmentManager());
                mPostingRecyclerView.setAdapter(mPostingAdapter);
            }
        }).execute();

        mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                getActivity(), getActivity().getSupportFragmentManager());
        mPostingRecyclerView.setAdapter(mPostingAdapter);
        return rootView;
    }

}
