package com.android.shelter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.shelter.user.LoginActivity;
import com.android.shelter.user.UserSessionManager;
import com.android.shelter.user.landlord.MyPostingFragment;
import com.android.shelter.user.landlord.PostPropertyActivity;
import com.android.shelter.user.tenant.favorite.FavoriteFragment;
import com.android.shelter.user.tenant.savedsearch.SavedSearchFragment;
import com.android.shelter.user.tenant.search.SearchPropertyFragment;
import com.android.shelter.util.DownloadImageTask;
import com.android.shelter.util.GCMRegistrationIntentService;
import com.android.shelter.util.ShelterConstants;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Landing screen or home activity for the application.
 */
public class HomeActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_FRAGMENT = 1;
    public static final int REQUEST_LOGIN = 2;
    public static final int HOME_FRAGMENT_ID = 3;
    public static final int MY_POSTING_FRAGMENT_ID = 4;
    public static final int MY_SAVED_SEARCH_FRAGMENT_ID = 5;
    public static final int MY_SEARCH_FRAGMENT_ID = 6;
    public static final int MY_FAVORITES_FRAGMENT_ID = 7;
    public static final String EXTRA_FRAGMENT_ID = "com.android.shelter.fragment_id";
    public static final String EXTRA_SHOW_POST_PROPERTY = "com.android.shelter.show_post_property";

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
    private GoogleApiClient mGoogleApiClient;
    private BroadcastReceiver mBroadCastReceiver;

    @Override
    public Fragment createFragment(){
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Now creating the fragment");
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
                .build();

        setContentView(getLayoutResId());
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
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
            Log.d(TAG, "Logout button clicked");
            signOutUser();
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
                if(UserSessionManager.get(getApplicationContext()).isUserSignedIn()){
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
            if(data.getIntExtra(EXTRA_FRAGMENT_ID, HOME_FRAGMENT_ID) == MY_POSTING_FRAGMENT_ID){
                updateFragment(new MyPostingFragment(), MY_POSTING_FRAGMENT_TAG);
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, HOME_FRAGMENT_ID) == MY_SEARCH_FRAGMENT_ID){
                updateFragment(new SearchPropertyFragment(), MY_SEARCH_FRAGMENT_TAG);
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, HOME_FRAGMENT_ID) == MY_SAVED_SEARCH_FRAGMENT_ID){
                updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
            }else if(data.getIntExtra(EXTRA_FRAGMENT_ID, HOME_FRAGMENT_ID) == MY_FAVORITES_FRAGMENT_ID){
                updateFragment(new SavedSearchFragment(), MY_FAVORITES_FRAGMENT_TAG);
            }else{
                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
            }
        } else if(requestCode == REQUEST_LOGIN){
            Log.d(TAG, "On Activity result changing layout");
            toggleUserProfileLayout();
        }
    }

    private void toggleUserProfileLayout(){
        if(UserSessionManager.get(getApplicationContext()).isUserSignedIn()){

            mBroadCastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                        String token = intent.getStringExtra("token");
                        Log.e("Token : ",token);
                        // Toast.makeText(HomeActivity.this, "token" + token, Toast.LENGTH_SHORT).show();
                    }else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                        // Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }else{

                    }
                }
            };
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            if(ConnectionResult.SUCCESS!=resultCode){
                if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                    Toast.makeText(getApplicationContext(), "play services is not installed", Toast.LENGTH_SHORT).show();
                    GooglePlayServicesUtil.showErrorNotification(resultCode,getApplicationContext());
                }else{
                    Toast.makeText(getApplicationContext(), "device doest not support", Toast.LENGTH_SHORT).show();
                }
            }else{
                Intent intent = new Intent(this,GCMRegistrationIntentService.class);
                startService(intent);
            }

            mNavigationMenu.findItem(R.id.nav_logout).setVisible(true);
            mNavigationMenu.findItem(R.id.nav_login).setVisible(false);

            mBeforeSignInLayout.setVisibility(View.GONE);
            mAfterSignInLayout.setVisibility(View.VISIBLE);

            TextView userName = (TextView) mAfterSignInLayout.findViewById(R.id.user_name);
            userName.setText(UserSessionManager.get(getApplicationContext()).getUserName());
            ImageView profilePicture = (ImageView) mAfterSignInLayout.findViewById(R.id.user_profile_picture);
            new DownloadImageTask(profilePicture).execute(UserSessionManager.get(getApplicationContext()).getPictureURL());
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
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReceiver);
    }


    public void signOutUser(){
        if(ShelterConstants.FACEBOOK_TYPE.equalsIgnoreCase(UserSessionManager.get(getApplicationContext()).getLoginType())){
            LoginManager.getInstance().logOut();
            UserSessionManager.get(getApplicationContext()).clearUserData();
            Log.d(TAG, "Facebook logged out");
        }else {
            Log.d(TAG, "Google client connected "+ mGoogleApiClient.isConnected());
            if(mGoogleApiClient.isConnected()){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                UserSessionManager.get(getApplicationContext()).clearUserData();
                                Log.d(TAG, "Google logged out");
                            }
                        });
            }
        }
        toggleUserProfileLayout();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }

    /**
     * Returns the relevant resource ID for view based on tablet or phone.
     * @return
     */
    private int getLayoutResId(){
        if(isTabletAndLandscape()){
            Log.d(TAG, "Tablet layout returned");
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag(HOME_FRAGMENT_TAG) != null){
                return R.layout.activity_home;
            }
            return R.layout.fragment_tablet_layout;
        }
        Log.d(TAG, "Normal layout returned");
        return R.layout.activity_home;
    }

    /**
     * Returns if the device is tablet
     * @return
     */
    private boolean isTabletAndLandscape(){
        Configuration config = getApplicationContext().getResources().getConfiguration();
        if(config.smallestScreenWidthDp >= 400){
            return true;
        }
        return false;
    }
}
