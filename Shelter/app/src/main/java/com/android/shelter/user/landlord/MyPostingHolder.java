package com.android.shelter.user.landlord;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.shelter.property.Property;
import com.android.shelter.user.landlord.PostedPropertyPagerActivity;
import com.android.shelter.R;
import com.android.shelter.util.DownloadImageTask;


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

        mPropertyImageView = (ImageView) itemView.findViewById(R.id.my_property_photo);
        mPropertyName = (TextView) itemView.findViewById(R.id.my_property_name);
        mPropertyType = (TextView) itemView.findViewById(R.id.my_property_type);
        mAddress = (TextView) itemView.findViewById(R.id.my_property_address);
        mRent = (TextView) itemView.findViewById(R.id.my_property_rent);
    }

    /**
     * Binds the image for the property
     * @param property
     */
    public void bindView(Property property) {
        mProperty = property;
        mPropertyName.setText(property.getName());
        mPropertyType.setText(property.getType());
//        mPropertyImageView.setImageResource(property.getPhotoId());
        new DownloadImageTask(mPropertyImageView).
                execute("http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=p2.jpg");
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