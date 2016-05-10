package com.android.shelter.landlord;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.shelter.AbstractFragmentActivity;
import com.android.shelter.R;

/**
 * Activity to add new property.
 */
public class PostPropertyActivity extends AbstractFragmentActivity {

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
