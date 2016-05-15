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
    private ToggleButton mFavToggleButton;
    private Property mProperty;

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
        mFavToggleButton = (ToggleButton)itemView.findViewById(R.id.fav_toggle_button);
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
        new DownloadImageTask(mPropertyImageView).execute("" +
                "http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=p1.jpg");
        mAddress.setText(property.getAddress());
        mRent.setText(property.getDisplayRent());
        mBaths.setText(property.getDisplayBath());
        mBeds.setText(property.getDisplayRoom());
        mFloorArea.setText(property.getDisplayFloorArea());
        mFavToggleButton.setChecked(mProperty.isFavorite());

        mFavToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoriteCriteria criteria = new FavoriteCriteria();
                criteria.setUser(UserSessionManager.get(mActivity).getOwnerId());
                criteria.setOwner_id(mProperty.getOwnerId());
                criteria.setProperty_id(mProperty.getId().toString());
                if(mFavToggleButton.isChecked()){
                    new ShelterFavoriteTask(mActivity.getApplicationContext(), "addfavourite", "POST",
                            true, criteria, new FragmentCallback() {
                        @Override
                        public void onTaskDone() {
                            mProperty.setFavorite(true);
                        }
                    }).execute();
                }else{
                    new ShelterFavoriteTask(mActivity.getApplicationContext(), "removefavourite", "DELETE",
                            true, criteria, new FragmentCallback() {
                        @Override
                        public void onTaskDone() {
                            mProperty.setFavorite(false);
                        }
                    }).execute();
                }
//                if (property.isFavorite()) {
//                    property.setFavorite(false);
//                    mProperty = property;
//                    FavoriteCriteria criteria = new FavoriteCriteria();
//                    criteria.setUser(UserSessionManager.get(mActivity).getOwnerId());
//                    criteria.setOwner_id(mProperty.getOwnerId());
//                    criteria.setProperty_id(mProperty.getId().toString());
//                    new ShelterFavoriteTask(mActivity.getApplicationContext(), "removefavourite", "DELETE",
//                            true, criteria, new FragmentCallback() {
//                        @Override
//                        public void onTaskDone() {
//
//                        }
//                    }).execute();
//                } else {
//                    property.setFavorite(true);
//                    mProperty = property;
//                    FavoriteCriteria criteria = new FavoriteCriteria();
//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
//                    criteria.setUser(preferences.getString(
//                            ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ShelterConstants.DEFAULT_STRING));
//                    criteria.setOwner_id(mProperty.getOwnerId());
//                    criteria.setProperty_id(mProperty.getId().toString());
//                    new ShelterFavoriteTask(mActivity.getApplicationContext(), "addfavourite", "POST",
//                            true, criteria, new FragmentCallback() {
//                        @Override
//                        public void onTaskDone() {
//
//                        }
//                    }).execute();
//                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d("SearchPropertyHolder", "Pager activity starting");
        PropertyLab.get(mActivity).updatePropertyFavorite(mProperty.getId(), mProperty.isFavorite());
        Intent intent = SearchPropertyPagerActivity.newIntent(mActivity, mProperty.getId());
        mActivity.startActivity(intent);
    }
}
