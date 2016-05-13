package com.android.shelter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.shelter.user.LoginActivity;
import com.android.shelter.user.landlord.MyPostingFragment;
import com.android.shelter.user.landlord.PostPropertyActivity;
import com.android.shelter.user.tenant.savedsearch.SavedSearchFragment;
import com.android.shelter.user.tenant.search.SearchPropertyFragment;
import com.android.shelter.user.UserProfileActivity;
import com.android.shelter.util.ShelterConstants;

/**
 * Landing screen or home activity for the application.
 */
public class HomeActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_FRAGMENT = 1;
    public static final int REQUEST_LOGIN = 2;
    public static final int REQUEST_USER_PROFILE = 3;
    public static final String EXTRA_FRAGMENT_ID = "com.android.shelter.fragment_id";
    public static final String EXTRA_IS_LOGGED_OUT = "com.android.shelter.is_logged_out";
    public static final int HOME_FRAGMENT_ID = 2;
    public static final int MY_POSTING_FRAGMENT_ID = 3;
    public static final int MY_SAVED_SEARCH_FRAGMENT_ID = 4;
    public static final int MY_SEARCH_FRAGMENT_ID = 5;

    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String MY_POSTING_FRAGMENT_TAG = "MyPostingFragment";
    public static final String MY_SEARCH_FRAGMENT_TAG = "MySearchFragment";
    public static final String MY_SAVED_SEARCH_FRAGMENT_TAG = "MySavedSearchFragment";

    private static final String TAG = "HomeActivity";
    private DrawerLayout mDrawer;
    private RelativeLayout mBeforeSigninLayout;
    private LinearLayout mAfterSigninLayout;


    @Override
    public Fragment createFragment(){
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Now creating the fragment");
        setContentView(R.layout.activity_home);

        int id = getResources().getIdentifier("com.android.shelter:drawable/" , null, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View navHeaderView = navigationView.getHeaderView(0);
        mAfterSigninLayout = (LinearLayout) navHeaderView.findViewById(R.id.after_signin);
        mAfterSigninLayout.setOnClickListener(new ProfileClickListener());
        mBeforeSigninLayout = (RelativeLayout) navHeaderView.findViewById(R.id.before_signin);

        toggleUserProfileLayout();

        Button loginButton = (Button) navHeaderView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                new StartLoginProcess().execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_home){
            updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
        } else if (id == R.id.nav_search) {
            updateFragment(new SearchPropertyFragment(), MY_SEARCH_FRAGMENT_TAG);
        } else if (id == R.id.nav_properties) {
            updateFragment(new MyPostingFragment(), MY_POSTING_FRAGMENT_TAG);
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_saved_searches) {
            updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Menu inflated");
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post_new_property:
                if(isUserLoggedIn()){
                    Intent postProperty = PostPropertyActivity.newIntent(getApplicationContext(), null);
                    startActivityForResult(postProperty, REQUEST_FRAGMENT);
                }else{
                    new StartLoginProcess().execute();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FRAGMENT && data != null && data.hasExtra(EXTRA_FRAGMENT_ID)){
            Log.d(TAG, "Back in Home activity " + data.getIntExtra(EXTRA_FRAGMENT_ID, 1));
            if(data.getIntExtra(EXTRA_FRAGMENT_ID, 1) == MY_POSTING_FRAGMENT_ID){
                updateFragment(new MyPostingFragment(), MY_POSTING_FRAGMENT_TAG);
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, 1) == MY_SEARCH_FRAGMENT_ID){
                updateFragment(new SearchPropertyFragment(), MY_SEARCH_FRAGMENT_TAG);
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, 1) == MY_SAVED_SEARCH_FRAGMENT_ID){
                updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
            }else{
                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
            }
        }else if(requestCode == REQUEST_LOGIN){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if(preferences.getBoolean(ShelterConstants.SHARED_PREFERENCE_SIGNED_IN, false)){
                Log.d(TAG, preferences.getString("email", "none"));
                toggleUserProfileLayout();
            }
        }else if(requestCode == REQUEST_USER_PROFILE && data != null && data.hasExtra(EXTRA_IS_LOGGED_OUT)){
            toggleUserProfileLayout();
        }
    }

    private boolean isUserLoggedIn(){
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(ShelterConstants.SHARED_PREFERENCE_SIGNED_IN, false);
    }

    private void toggleUserProfileLayout(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(isUserLoggedIn()){
            mBeforeSigninLayout.setVisibility(View.GONE);
            mAfterSigninLayout.setVisibility(View.VISIBLE);
            TextView userName = (TextView) mAfterSigninLayout.findViewById(R.id.user_name);
            userName.setText(preferences.getString(ShelterConstants.SHARED_PREFERENCE_USER_NAME, ShelterConstants.DEFAULT_STRING));
        }else {
            mBeforeSigninLayout.setVisibility(View.VISIBLE);
            mAfterSigninLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateFragment(Fragment fragment, String fragmentTag) {
        super.updateFragment(fragment, fragmentTag);
    }

    private class StartLoginProcess extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Login activity started");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
            return null;
        }
    }

    private class ProfileClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent userProfile = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivityForResult(userProfile, REQUEST_USER_PROFILE);
        }
    }
}
