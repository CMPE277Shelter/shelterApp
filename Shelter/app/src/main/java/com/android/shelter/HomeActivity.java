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
import android.widget.Toast;

/**
 * Landing screen or home activity for the application.
 */
public class HomeActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_FRAGMENT = 1;
    public static final String EXTRA_FRAGMENT_ID = "com.android.shelter.fragment_id";
    public static final int HOME_FRAGMENT_ID = 2;
    public static final int MY_POSTING_FRAGMENT_ID = 3;

    public static final String HOME_FRAGMENT_TAG = "HomeFragment";
    public static final String MY_POSTING_FRAGMENT_TAG = "MyPostingFragment";

    private static final String TAG = "HomeActivity";
    private DrawerLayout mDrawer;


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
        if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Menu inflated");
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.activity_home_menu, menu);
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
            }else {
                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
            }
        }
    }

    @Override
    public void updateFragment(Fragment fragment, String fragmentTag) {
        super.updateFragment(fragment, fragmentTag);
    }
}
