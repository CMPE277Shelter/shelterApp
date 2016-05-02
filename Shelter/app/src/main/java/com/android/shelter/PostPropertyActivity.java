package com.android.shelter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

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
}
