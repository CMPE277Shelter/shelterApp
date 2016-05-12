package com.android.shelter.landlord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.shelter.AbstractFragmentActivity;
import com.android.shelter.R;

import java.util.UUID;

/**
 * Activity to add new property.
 */
public class PostPropertyActivity extends AbstractFragmentActivity {

    public static final String EXTRA_PROPERTY_ID =
            "com.android.shelter.post_property_activity.property_id";

    public static Intent newIntent(Context packageContext, UUID propertyId) {
        Intent intent = new Intent(packageContext, PostPropertyActivity.class);
        intent.putExtra(EXTRA_PROPERTY_ID, propertyId);
        return intent;
    }
    @Override
    public Fragment createFragment(){
        return new PostPropertyFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_property);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }
}
