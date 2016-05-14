package com.android.shelter.user.tenant.search;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.shelter.R;
import com.android.shelter.property.Property;
import com.android.shelter.user.landlord.MyPostingHolder;

import java.util.List;

/**
 * Created by Prasanna on 5/14/16.
 */
public class SearchPropertyAdapter extends RecyclerView.Adapter<SearchPropertyHolder> {
    private List<Property> mPropertyList;
    private Activity mActivity;
    private FragmentManager mFragmentManager;

    public SearchPropertyAdapter(List<Property> propertyList, Activity activity, FragmentManager fragmentManager) {
        mPropertyList = propertyList;
        mActivity = activity;
        mFragmentManager = fragmentManager;
    }

    @Override
    public SearchPropertyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        View view = layoutInflater.inflate(R.layout.tenant_cardview_property_item, parent, false);
        return new SearchPropertyHolder(view, mActivity, mFragmentManager);
    }

    @Override
    public void onBindViewHolder(SearchPropertyHolder holder, int position) {
        Property property = mPropertyList.get(position);
        holder.bindView(property);
    }

    @Override
    public int getItemCount() {
        return mPropertyList.size();
    }
}
