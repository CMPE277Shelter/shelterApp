package com.android.shelter.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.Property;
import com.android.shelter.R;

import java.util.List;

/**
 * Adapter for property images
 */
public class MyPostingAdapter extends RecyclerView.Adapter<MyPostingHolder> {

    private List<Property> mPropertyList;

    private Activity mActivity;

    //private IconDownloader<PropertyImageHolder> mIconDownloader;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for adapter
     * @param propertyList
     * @param activity
     * @param fragmentManager
     */
    public MyPostingAdapter(List<Property> propertyList, Activity activity, FragmentManager fragmentManager) {
        mPropertyList = propertyList;
        mActivity = activity;
        //mIconDownloader = iconDownloader;
        mFragmentManager = fragmentManager;
    }

    @Override
    public MyPostingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.my_postings_list, parent, false);

        return new MyPostingHolder(view, mActivity, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(MyPostingHolder holder, int position) {
        Property property = mPropertyList.get(position);
        holder.bindView(property);
    }

    @Override
    public int getItemCount() {
        return mPropertyList.size();
    }
}