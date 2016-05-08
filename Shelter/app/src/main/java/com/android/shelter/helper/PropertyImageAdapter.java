package com.android.shelter.helper;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.R;

import java.util.List;

/**
 * Adapter for property images
 */
public class PropertyImageAdapter extends RecyclerView.Adapter<PropertyImageHolder> {

    private List<PropertyImage> mPropertyImages;

    private Activity mActivity;

    //private IconDownloader<PropertyImageHolder> mIconDownloader;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for adapter
     * @param propertyImages
     * @param activity
     * @param fragmentManager
     */
    public PropertyImageAdapter(List<PropertyImage> propertyImages, Activity activity, FragmentManager fragmentManager) {
        mPropertyImages = propertyImages;
        mActivity = activity;
        //mIconDownloader = iconDownloader;
        mFragmentManager = fragmentManager;
    }

    @Override
    public PropertyImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.property_images_layout, parent, false);

        return new PropertyImageHolder(view, mActivity, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(PropertyImageHolder holder, int position) {
        PropertyImage propertyImage = mPropertyImages.get(position);
        holder.bindImage(propertyImage);
    }

    @Override
    public int getItemCount() {
        return mPropertyImages.size();
    }
}