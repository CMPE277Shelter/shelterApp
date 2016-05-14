package com.android.shelter.user.tenant.savedsearch;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.shelter.R;
import com.android.shelter.util.DownloadImageTask;

public class SavedSearchHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private ImageView mSavedSearchImageView;
    private TextView mSavedSearchName;
    private TextView mKeyword;
    private TextView mPropertyType;
    private TextView mRent;
    private TextView mCity;
    private TextView mZipcode;


    private SavedSearch mSavedSearch;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for holder
     * @param itemView
     * @param context
     * @param fragmentManager
     */
    public SavedSearchHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;

        mSavedSearchImageView = (ImageView) itemView.findViewById(R.id.search_imageview);
        mSavedSearchName = (TextView) itemView.findViewById(R.id.search_name);
        mPropertyType = (TextView) itemView.findViewById(R.id.search_property_type);
        mRent = (TextView) itemView.findViewById(R.id.search_property_rent);
        mKeyword = (TextView) itemView.findViewById(R.id.search_keyword);
        mCity =(TextView) itemView.findViewById(R.id.search_city);
        mZipcode=(TextView) itemView.findViewById(R.id.search_zipcode);

    }

    /**
     * Binds the image for the property
     * @param search
     */
    public void bindView(SavedSearch search) {
        mSavedSearch = search;
        mSavedSearchName.setText(search.getSavedSearchName());
        mPropertyType.setText(search.getPostingType());
        new DownloadImageTask(mSavedSearchImageView).execute("" +
                "http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=p1.jpg");
        
        mCity.setText(search.getCity());
        mRent.setText(search.getMinRent() + "-" + search.getMaxRent());
        mKeyword.setText(search.getKeyword());
        mZipcode.setText(search.getZipcode());

    }

    @Override
    public void onClick(View v) {
        Log.d("MyPostingHolder", "Pager activity starting");
//        Intent intent = PostedPropertyPagerActivity.newIntent(mActivity, mSavedSearch.getId());
//        mActivity.startActivity(intent);
    }


}

