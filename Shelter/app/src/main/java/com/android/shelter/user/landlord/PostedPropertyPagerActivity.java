package com.android.shelter.user.landlord;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.R;

import java.util.List;
import java.util.UUID;

/**
 * Pager activity for posted properties
 */
public class PostedPropertyPagerActivity extends AppCompatActivity {

    private static final String TAG = "PostedPropertyPager";
    private static final String EXTRA_PROPERTY_ID =
            "com.android.shelter.posted_property_pager_activity.property_id";

    private ViewPager mViewPager;
    private List<Property> mPropertyList;

    /**
     * Returns new intent pager property
     * @param packageContext
     * @param propertyId
     * @return
     */
    public static Intent newIntent(Context packageContext, UUID propertyId) {
        Intent intent = new Intent(packageContext, PostedPropertyPagerActivity.class);
        intent.putExtra(EXTRA_PROPERTY_ID, propertyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On created for pager activity");
        setContentView(R.layout.activity_posted_property_pager);

        UUID imageId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_PROPERTY_ID);

        mViewPager = (ViewPager) findViewById(R.id.posted_property_pager);

        mPropertyList = PropertyLab.get(getApplicationContext()).getProperties();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Property property = mPropertyList.get(position);
                return PostedPropertyFragment.newInstance(property.getId());
            }

            @Override
            public int getCount() {
                return mPropertyList.size();
            }
        });

        for (int i = 0; i < mPropertyList.size(); i++) {
            if (mPropertyList.get(i).getId().equals(imageId)) {
                mViewPager.setCurrentItem(i);
                getSupportActionBar().setTitle(PropertyLab.get(getApplicationContext()).getProperty(imageId).getName());
                break;
            }
        }
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(PropertyLab.get(getApplicationContext()).getProperty(position).getName());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
