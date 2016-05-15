package com.android.shelter;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.shelter.user.IUserSessionUpdate;
import com.android.shelter.user.LoginActivity;
import com.android.shelter.user.UserSessionManager;
import com.android.shelter.user.landlord.MyPostingFragment;
import com.android.shelter.user.landlord.PostPropertyActivity;
import com.android.shelter.user.tenant.favorite.FavoriteFragment;
import com.android.shelter.user.tenant.savedsearch.SavedSearchFragment;
import com.android.shelter.user.tenant.search.SearchPropertyFragment;
import com.android.shelter.user.UserProfileActivity;
import com.android.shelter.util.DownloadImageTask;

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
    public static final String EXTRA_SHOW_POST_PROPERTY = "com.android.shelter.show_post_property";
    public static final int HOME_FRAGMENT_ID = 2;
    public static final int MY_POSTING_FRAGMENT_ID = 3;
    public static final int MY_SAVED_SEARCH_FRAGMENT_ID = 4;
    public static final int MY_SEARCH_FRAGMENT_ID = 5;
    public static final int MY_FAVORITES_FRAGMENT_ID = 6;

    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String MY_POSTING_FRAGMENT_TAG = "MyPostingFragment";
    public static final String MY_SEARCH_FRAGMENT_TAG = "MySearchFragment";
    public static final String MY_SAVED_SEARCH_FRAGMENT_TAG = "MySavedSearchFragment";
    public static final String MY_FAVORITES_FRAGMENT_TAG = "MyFavoritesFragment";

    private static final String TAG = "HomeActivity";
    private Menu mNavigationMenu;
    private DrawerLayout mDrawer;
    private RelativeLayout mBeforeSignInLayout;
    private RelativeLayout mAfterSignInLayout;
    private UserSessionManager mUserSessionManager;

    @Override
    public Fragment createFragment(){
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Now creating the fragment");
        mUserSessionManager = UserSessionManager.get(getApplicationContext());
        mUserSessionManager.registerUserUpdateCallbacks(this, new IUserSessionUpdate() {
            @Override
            public void signInSuccessfull() {
                Log.d(TAG, "Sign in success");
            }

            @Override
            public void signOutSuccessfull() {
                Log.d(TAG, "User successfully logged out");
                toggleUserProfileLayout();
            }
        });

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                toggleUserProfileLayout();
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mNavigationMenu = navigationView.getMenu();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        View navHeaderView = navigationView.getHeaderView(0);
        mAfterSignInLayout = (RelativeLayout) navHeaderView.findViewById(R.id.after_signin);
        mAfterSignInLayout.setOnClickListener(new ProfileClickListener());
        mBeforeSignInLayout = (RelativeLayout) navHeaderView.findViewById(R.id.before_signin);

        toggleUserProfileLayout();

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Inside on resume");
        toggleUserProfileLayout();
        mUserSessionManager.getGoogleApiClient().connect();
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
            updateFragment(new FavoriteFragment(), MY_FAVORITES_FRAGMENT_TAG);
        } else if (id == R.id.nav_saved_searches) {
            updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
        } else if(id == R.id.nav_login) {
            Log.d(TAG, "Login button clicked");
            new StartLoginProcess().execute(false);
        } else if(id == R.id.nav_logout) {
            Log.d(TAG, "Login button clicked");
            startLogoutProcess();
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
                if(mUserSessionManager.isUserSignedIn()){
                    Intent postProperty = PostPropertyActivity.newIntent(getApplicationContext(), null);
                    startActivityForResult(postProperty, REQUEST_FRAGMENT);
                }else{
                    new StartLoginProcess().execute(true);
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
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, 1) == MY_FAVORITES_FRAGMENT_ID){
                updateFragment(new SavedSearchFragment(), MY_FAVORITES_FRAGMENT_TAG);
            }else{
                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
            }
        } else if(requestCode == REQUEST_LOGIN){
            toggleUserProfileLayout();
        }
    }

    private void toggleUserProfileLayout(){
        if(mUserSessionManager.isUserSignedIn()){
            mNavigationMenu.findItem(R.id.nav_logout).setVisible(true);
            mNavigationMenu.findItem(R.id.nav_login).setVisible(false);

            mBeforeSignInLayout.setVisibility(View.GONE);
            mAfterSignInLayout.setVisibility(View.VISIBLE);

            TextView userName = (TextView) mAfterSignInLayout.findViewById(R.id.user_name);
            userName.setText(mUserSessionManager.getUserName());
            ImageView profilePicture = (ImageView) mAfterSignInLayout.findViewById(R.id.user_profile_picture);
            new DownloadImageTask(profilePicture).execute(mUserSessionManager.getPictureURL());
        }else {
            mNavigationMenu.findItem(R.id.nav_logout).setVisible(false);
            mNavigationMenu.findItem(R.id.nav_login).setVisible(true);
            mBeforeSignInLayout.setVisibility(View.VISIBLE);
            mAfterSignInLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateFragment(Fragment fragment, String fragmentTag) {
        super.updateFragment(fragment, fragmentTag);
    }

    private class StartLoginProcess extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Login activity started");
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.putExtra(EXTRA_SHOW_POST_PROPERTY, params[0]);
            startActivityForResult(intent, REQUEST_LOGIN);
            return null;
        }
    }

    public void startLogoutProcess() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d(TAG, "Successfully logged out");
            }

            @Override
            protected Void doInBackground(Void... params) {
                mUserSessionManager.signOutUser();
                return null;
            }
        }.execute();
    }

    private class ProfileClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent userProfile = new Intent(HomeActivity.this, UserProfileActivity.class);
            startActivityForResult(userProfile, REQUEST_USER_PROFILE);
        }
    }
}
