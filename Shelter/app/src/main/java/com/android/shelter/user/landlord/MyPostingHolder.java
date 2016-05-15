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

import com.android.shelter.helper.PropertyImage;
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
    private TextView mRent;
    private TextView mBath;
    private TextView mRooms;
    private TextView mFloorArea;

    private Property mProperty;
    private PropertyImage mPropertyImage;

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
        mBath = (TextView) itemView.findViewById(R.id.my_property_bath);
        mBath = (TextView) itemView.findViewById(R.id.my_property_bath);
        mRooms = (TextView) itemView.findViewById(R.id.my_property_room);
        mFloorArea = (TextView) itemView.findViewById(R.id.my_property_floor);
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
        mPropertyImage = new PropertyImage();
        if(mProperty.getPropertyImages().size() > 0){
            mPropertyImage = mProperty.getPropertyImages().get(0);
            new DownloadImageTask(mPropertyImageView).
                    execute(mPropertyImage.getImagePath());
        }
        mAddress.setText(property.getAddress());
        mRent.setText(property.getRent());
        mBath.setText(property.getBath());
        mFloorArea.setText(property.getFloorArea());
        mRooms.setText(property.getRooms());
    }

    @Override
    public void onClick(View v) {
        Log.d("MyPostingHolder", "Pager activity starting");
        Intent intent = PostedPropertyPagerActivity.newIntent(mActivity, mProperty.getId());
        mActivity.startActivity(intent);
    }


}