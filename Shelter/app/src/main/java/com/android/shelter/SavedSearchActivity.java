package com.android.shelter;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.Toast;

public class SavedSearchActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "SavedSearchActivity";
    private DrawerLayout mDrawer;

    @Override
    public Fragment createFragment(){
        return new SavedSearchFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_search);
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
                Toast.makeText(SavedSearchActivity.this, "Login code", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SavedSearchActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.d(TAG, "Menu inflated");
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_saved_search, menu);
        return true;
    }

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
}
