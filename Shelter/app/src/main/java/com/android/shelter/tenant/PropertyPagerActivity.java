package com.android.shelter.tenant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.shelter.Property;
import com.android.shelter.PropertyLab;
import com.android.shelter.R;

import java.util.List;
import java.util.UUID;

public class PropertyPagerActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_ID =
            "com.android.shelter.property_id";

    private ViewPager mViewPager;
    private List<Property> mPropertyList;
    public static Intent newIntent(Context packageContext, UUID imageId) {
        Intent intent = new Intent(packageContext, PropertyPagerActivity.class);
        intent.putExtra(EXTRA_IMAGE_ID, imageId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_pager);
        UUID imageId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_IMAGE_ID);

        mViewPager = (ViewPager) findViewById(R.id.posted_property_pager);

        mPropertyList = PropertyLab.get(getApplicationContext()).getProperties();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Property property = mPropertyList.get(position);
                return PropertyFragment.newInstance(property.getId());
            }

            @Override
            public int getCount() {
                return mPropertyList.size();
            }
        });

        for (int i = 0; i < mPropertyList.size(); i++) {
            if (mPropertyList.get(i).getId().equals(imageId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
