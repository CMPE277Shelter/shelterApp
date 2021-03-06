package com.android.shelter.user.landlord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.util.ImagePicker;

import java.util.List;
import java.util.UUID;

/**
 * Pager activity to display images when user clicks on the thumbnail
 */
public class ImagePagerActivity extends AppCompatActivity {

    private static final String TAG = "ImagePagerActivity";
    private static final String EXTRA_IMAGE_ID =
            "com.android.shelter.image_id";

    private ViewPager mViewPager;
    private List<PropertyImage> mPropertyImagesList;

    public static Intent newIntent(Context packageContext, UUID imageId) {
        Intent intent = new Intent(packageContext, ImagePagerActivity.class);
        intent.putExtra(EXTRA_IMAGE_ID, imageId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);

        UUID imageId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_IMAGE_ID);

        mViewPager = (ViewPager) findViewById(R.id.property_image_pager);

        mPropertyImagesList = PropertyLab.get(getApplicationContext()).getPropertyImages();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                PropertyImage propertyImage = mPropertyImagesList.get(position);
                return ImageDisplayFragment.newInstance(propertyImage.getId());
            }

            @Override
            public int getCount() {
                return mPropertyImagesList.size();
            }
        });

        for (int i = 0; i < mPropertyImagesList.size(); i++) {
            if (mPropertyImagesList.get(i).getId().equals(imageId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        if(getFragmentManager().getBackStackEntryCount() > 0){
//            getFragmentManager().popBackStack();
//        }else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                Log.d(TAG, "Home clicked");
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}