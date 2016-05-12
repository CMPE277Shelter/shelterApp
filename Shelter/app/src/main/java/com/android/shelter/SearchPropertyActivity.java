package com.android.shelter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchPropertyActivity extends AbstractFragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "SearchPropertyActivity";
    private DrawerLayout mDrawer;
    int PLACE_PICKER_REQUEST = 1;
    public static com.android.shelter.Location appLocation ;

    @Override
    protected Fragment createFragment() {
        return new SearchPropertyFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);
        com.android.shelter.Location.initInstance();
        appLocation = com.android.shelter.Location.getInstance();
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
        LatLng loc=getCurrentLocation(getApplicationContext());
        String url = "http://maps.google.com/maps/api/staticmap?center=" + loc.latitude
                + "," + loc.longitude + "&zoom=15&size=400x200&sensor=false&markers=color:red|"
                +loc.latitude+","+loc.longitude;
        appLocation.setStaticMapUrl(url);
        //Toast.makeText(SearchPropertyActivity.this, appLocation.getStaticMapUrl(), Toast.LENGTH_SHORT).show();
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
            if (addresses.size() > 0) {
                appLocation.setCityName(addresses.get(0).getLocality());
                appLocation.setPostalCode(addresses.get(0).getPostalCode());
            }
            //Toast.makeText(SearchPropertyActivity.this, addresses.get(0).getPostalCode(), Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "Menu inflated");
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_search_property, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_loc_picker:
                try {
                    PlacePicker.IntentBuilder intentBuilder =new PlacePicker.IntentBuilder();
                    LatLng loc=getCurrentLocation(getBaseContext());
                    Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
                        if (addresses.size() > 0) {
                            appLocation.setCityName(addresses.get(0).getLocality());
                            appLocation.setPostalCode(addresses.get(0).getPostalCode());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(SearchPropertyActivity.this, location.toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(SearchPropertyActivity.this, appLocation.getPostalCode(), Toast.LENGTH_SHORT).show();
                    LatLngBounds BOUNDS_CURRENT_LOCATION= new LatLngBounds(
                            new LatLng(loc.latitude,loc.longitude),
                            new LatLng(loc.latitude,loc.longitude)
                    );
                    intentBuilder.setLatLngBounds(BOUNDS_CURRENT_LOCATION);
                    Intent intent = intentBuilder.build(SearchPropertyActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }


    }
    public static LatLng getCurrentLocation(Context c) {
        LocationManager service = (LocationManager) c.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = service.getBestProvider(criteria, true);
        if (ContextCompat.checkSelfPermission(c.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            return new LatLng(37.322993, -121.883200);
        }


        Location location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        if(location==null){
            return new LatLng(37.322993, -121.883200);
        }
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        return userLocation;
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
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, this);
            LatLng selectedLoc = place.getLatLng();
            String url = "http://maps.google.com/maps/api/staticmap?center=" + selectedLoc.latitude
                    + "," + selectedLoc.longitude + "&zoom=15&size=400x200&sensor=false&markers=color:red|"
                    +selectedLoc.latitude+","+selectedLoc.longitude;
            appLocation.setStaticMapUrl(url);

            //Toast.makeText(SearchPropertyActivity.this, url, Toast.LENGTH_SHORT).show();

            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(selectedLoc.latitude, selectedLoc.longitude, 1);
                if (addresses.size() > 0) {
                    appLocation.setCityName(addresses.get(0).getLocality());
                    appLocation.setPostalCode(addresses.get(0).getPostalCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
