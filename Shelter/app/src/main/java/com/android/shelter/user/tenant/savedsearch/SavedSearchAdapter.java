package com.android.shelter.user.tenant.savedsearch;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.R;

import java.util.List;

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearchAdapter extends RecyclerView.Adapter<SavedSearchHolder> {
    private List<SavedSearch> mSavedSearchList;

    private Activity mActivity;

    //private IconDownloader<PropertyImageHolder> mIconDownloader;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for adapter
     * @param savedSearchList
     * @param activity
     * @param fragmentManager
     */
    public SavedSearchAdapter(List<SavedSearch> savedSearchList, Activity activity, FragmentManager fragmentManager) {
        mSavedSearchList = savedSearchList;
        mActivity = activity;
        //mIconDownloader = iconDownloader;
        mFragmentManager = fragmentManager;
    }

    @Override
    public SavedSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.my_saved_searches_list, parent, false);

        return new SavedSearchHolder(view, mActivity, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(SavedSearchHolder holder, int position) {
        SavedSearch savedSearch = mSavedSearchList.get(position);
        holder.bindView(savedSearch);
    }

    @Override
    public int getItemCount() {
        return mSavedSearchList.size();
    }
}
