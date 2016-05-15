package com.android.shelter.user.tenant.search;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.shelter.FragmentCallback;
import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.UserSessionManager;
import com.android.shelter.user.tenant.favorite.FavoriteCriteria;
import com.android.shelter.util.DownloadImageTask;
import com.android.shelter.util.ShelterFavoriteTask;

/**
 * Created by Prasanna on 5/14/16.
 */
public class SearchPropertyHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    private ImageView mPropertyImageView;
    private TextView mPropertyName;
    private TextView mAddress;
    private TextView mPropertyType;
    private TextView mBaths;
    private TextView mBeds;
    private TextView mFloorArea;
    private TextView mRent;
    private Property mProperty;
    private PropertyImage mPropertyImage;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    public SearchPropertyHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;

        mPropertyImageView = (ImageView) itemView.findViewById(R.id.thumbnail);
        mPropertyName = (TextView) itemView.findViewById(R.id.name);
        mPropertyType = (TextView) itemView.findViewById(R.id.type);
        mAddress = (TextView) itemView.findViewById(R.id.address);
        mRent = (TextView) itemView.findViewById(R.id.rent);
        mBaths = (TextView) itemView.findViewById(R.id.baths);
        mBeds =(TextView)itemView.findViewById(R.id.beds);
        mFloorArea = (TextView)itemView.findViewById(R.id.floorArea);
    }

    /**
     * Binds the image for the property
     * @param property
     */
    public void bindView(final Property property) {
        mProperty = property;
        mPropertyName.setText(property.getName());
        mPropertyType.setText(property.getType());
//        mPropertyImageView.setImageResource(property.getPhotoId());
        mPropertyImage = new PropertyImage();
        if(mPropertyImage != null){
            mPropertyImage = mProperty.getPropertyImages().get(0);
            if(mPropertyImage.getImageResourceId() == 0){
                new DownloadImageTask(mPropertyImageView).
                        execute(mPropertyImage.getImagePath());
            }else {
                mPropertyImageView.setBackgroundResource(mPropertyImage.getImageResourceId());
            }
        }
        mAddress.setText(property.getAddress());
        mRent.setText(property.getDisplayRent());
        mBaths.setText(property.getDisplayBath());
        mBeds.setText(property.getDisplayRoom());
        mFloorArea.setText(property.getDisplayFloorArea());
    }

    @Override
    public void onClick(View v) {
        Log.d("SearchPropertyHolder", "Pager activity starting");
        PropertyLab.get(mActivity).updatePropertyFavorite(mProperty.getId(), mProperty.isFavorite());
        Intent intent = SearchPropertyPagerActivity.newIntent(mActivity, mProperty.getId());
        mActivity.startActivity(intent);
    }
}
