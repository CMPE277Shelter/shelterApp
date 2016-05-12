package com.android.shelter;

import android.content.Intent;
import android.support.design.widget.NavigationView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.shelter.landlord.MyPostingFragment;
import com.android.shelter.util.ShelterPropertyTask;

public class SearchPropertyActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SearchPropertyActivity";
    private DrawerLayout mDrawer;

    @Override
    protected Fragment createFragment() {
        return new SearchPropertyFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);
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
                Toast.makeText(SearchPropertyActivity.this, "Login code", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchPropertyActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Menu inflated");
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_search_property, menu);
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQUEST_FRAGMENT && data != null && data.hasExtra(EXTRA_FRAGMENT_ID)){
//            Log.d(TAG, "Back in Home activity " + data.getIntExtra(EXTRA_FRAGMENT_ID, 1));
//            if(data.getIntExtra(EXTRA_FRAGMENT_ID, 1) == MY_POSTING_FRAGMENT_ID){
//                updateFragment(new MyPostingFragment(), MY_POSTING_FRAGMENT_TAG);
//            } else if(data.getIntExtra(EXTRA_FRAGMENT_ID,1)==MY_SAVED_SEARCH_FRAGMENT_ID){
//                updateFragment(new SavedSearchFragment(), MY_SAVED_SEARCH_FRAGMENT_TAG);
//            } else {
//                updateFragment(new HomeFragment(), HOME_FRAGMENT_TAG);
//            }
//        }
    }
}
