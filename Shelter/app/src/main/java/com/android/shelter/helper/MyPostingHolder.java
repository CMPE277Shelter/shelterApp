package com.android.shelter.helper;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.shelter.landlord.PostedPropertyPagerActivity;
import com.android.shelter.Property;
import com.android.shelter.R;


public class MyPostingHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private ImageView mPropertyImageView;
    private TextView mPropertyName;
    private TextView mAddress;
    private TextView mPropertyType;
    private ImageView mFavImageView;
    private TextView mRent;
    private ToggleButton mFavToggleButton;

    private Property mProperty;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for holder
     * @param itemView
     * @param context
     * @param fragmentManager
     */
    public MyPostingHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;

        mPropertyImageView = (ImageView) itemView.findViewById(R.id.property_imageview);
        mPropertyName = (TextView) itemView.findViewById(R.id.property_name);
        mPropertyType = (TextView) itemView.findViewById(R.id.property_type);
        mAddress = (TextView) itemView.findViewById(R.id.property_address);
        mRent = (TextView) itemView.findViewById(R.id.property_rent);

        mFavToggleButton = (ToggleButton) itemView.findViewById(R.id.fav_toggle_button);
        mFavToggleButton.setChecked(true);
        mFavToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFavToggleButton.isChecked()){
                    mFavToggleButton.setChecked(false);
                    Log.d("MyPostingHolder", "Toggle clicked white");
                }else {
                    mFavToggleButton.setChecked(true);
                    Log.d("MyPostingHolder", "Toggle clicked red");
                }
            }
        });

    }

    /**
     * Binds the image for the property
     * @param property
     */
    public void bindView(Property property) {
        mProperty = property;
        mPropertyName.setText(property.getName());
        mPropertyType.setText(property.getType());
        mPropertyImageView.setImageResource(property.getPhotoId());
        mAddress.setText(property.getAddress());
        mRent.setText(property.getRent());
    }

    @Override
    public void onClick(View v) {
        Log.d("MyPostingHolder", "Pager activity starting");
        Intent intent = PostedPropertyPagerActivity.newIntent(mActivity, mProperty.getId());
        mActivity.startActivity(intent);
    }


}