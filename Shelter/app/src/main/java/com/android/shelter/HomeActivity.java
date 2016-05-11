package com.android.shelter;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.shelter.landlord.MyPostingFragment;
import com.android.shelter.landlord.PostPropertyActivity;
import com.facebook.AccessToken;

/**
 * Landing screen or home activity for the application.
 */
public class HomeActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_FRAGMENT = 1;
    public static final String EXTRA_FRAGMENT_ID = "com.android.shelter.fragment_id";
    public static final int HOME_FRAGMENT_ID = 2;
    public static final int MY_POSTING_FRAGMENT_ID = 3;
    public static final int MY_SAVED_SEARCH_FRAGMENT_ID = 4;

    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String MY_POSTING_FRAGMENT_TAG = "MyPostingFragment";
    public static final String MY_SAVED_SEARCH_FRAGMENT_TAG = "MySavedSearchFragment";


    private static final String TAG = "HomeActivity";
    private DrawerLayout mDrawer;
    private LoginActivity login;
    private String FacebookUser;
    private String GoogleUser;


    @Override
    public Fragment createFragment(){
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login = new LoginActivity();
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
        View navHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        Button loginButton = (Button) navHeaderView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Login button clicked");
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                Toast.makeText(HomeActivity.this, "Login code", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        loginButton.setVisibility(View.VISIBLE);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        TextView Name = (TextView)findViewById(R.id.Name);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
             FacebookUser = extras.getString("FacebookUser");
             GoogleUser = extras.getString("GoogleUser");
        }
        Toast.makeText(HomeActivity.this, "USER:" +FacebookUser, Toast.LENGTH_SHORT).show();
        Toast.makeText(HomeActivity.this, "USER:"+GoogleUser, Toast.LENGTH_SHORT).show();
//        if(login.IsLoggedIn()||login.isGoogleLoggedIn()){
//
//            if(login.getUser()!="")
//                Name.setText("Welcome"+login.getUser());
//                loginButton.setVisibility(View.GONE);
//
//        }else if(login.getGoogleUser()!="" && login.isGoogleLoggedIn()){
//            //Name.setText("Welcome"+login.getGoogleUser());
//            Toast.makeText(HomeActivity.this, login.getGoogleUser(), Toast.LENGTH_SHORT).show();
//            loginButton.setVisibility(View.GONE);
//        }
//
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
        if (id == R.id.nav_search) {
            Intent searchPropertyIntent = new Intent(this,SearchPropertyActivity.class);
            startActivity(searchPropertyIntent);

        } else if (id == R.id.nav_properties) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_saved_searches) {
            Intent savedSearchIntent = new Intent(this, SavedSearchActivity.class);
            startActivityForResult(savedSearchIntent, HomeActivity.REQUEST_FRAGMENT);
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
                // TODO Check if the user is logged if not ask start LoginActivity
                Intent postProperty = new Intent(this, PostPropertyActivity.class);
                startActivityForResult(postProperty, HomeActivity.REQUEST_FRAGMENT);
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
            } else if(data.getIntExtra(EXTRA_FRAGMENT_ID,1)==MY_SAVED_SEARCH_FRAGMENT_ID){
                updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
            } else {
                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
            }
        }
    }

    @Override
    public void updateFragment(Fragment fragment, String fragmentTag) {
        super.updateFragment(fragment, fragmentTag);
    }

}
