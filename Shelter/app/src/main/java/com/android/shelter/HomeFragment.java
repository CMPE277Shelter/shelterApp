package com.android.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.util.DownloadImageTask;
import com.android.shelter.util.ShelterPropertyTask;
import com.android.shelter.util.TrendingPropertyTask;

import java.util.ArrayList;

/**
 * Fragment for {@link HomeActivity} TODO Make toolbar transparent on landing and scorlling effect.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
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
    private TextView mTrendingTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View v = inflater.inflate(R.layout.content_home, container, false);

        ImageView header = (ImageView)v.findViewById(R.id.header);
        Log.d(TAG, "HEader image width "+ header.getWidth());
        new DownloadImageTask(header).execute
                ("http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=header.jpg");


        mPropertyImageView = (ImageView) v.findViewById(R.id.trending_thumbnail);
        mPropertyName = (TextView) v.findViewById(R.id.trending_name);
        mPropertyType = (TextView) v.findViewById(R.id.trending_type);
        mAddress = (TextView) v.findViewById(R.id.trending_address);
        mRent = (TextView) v.findViewById(R.id.trending_rent);
        mBaths = (TextView) v.findViewById(R.id.trending_baths);
        mBeds =(TextView)v.findViewById(R.id.trending_beds);
        mFloorArea = (TextView)v.findViewById(R.id.trending_floorArea);
        mTrendingTextView = (TextView) v.findViewById(R.id.trending_textView);

        ArrayList<Property> propertyList= PropertyLab.get(getContext()).getProperties();
        if(propertyList.size() > 0) {
            new TrendingPropertyTask(getContext(), "trendingProperty", v, new FragmentCallback() {
                @Override
                public void onTaskDone() {
                    Property property = PropertyLab.get(getContext()).getProperties().get(0);
                    mProperty = property;
                    mPropertyName.setText(property.getName());
                    mPropertyType.setText(property.getType());
                    mPropertyImage = new PropertyImage();
                    if(mPropertyImage != null){
                        mPropertyImage = mProperty.getPropertyImages().get(0);
                        if(mPropertyImage.getImageResourceId() == 0){
                            Log.d(TAG, "Image path "+ mPropertyImage.getImagePath() + " width "+ mPropertyImageView.getWidth());

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
            }).execute();
        }else{
            mTrendingTextView.setText("No properties yet");
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        return v;
    }
}


