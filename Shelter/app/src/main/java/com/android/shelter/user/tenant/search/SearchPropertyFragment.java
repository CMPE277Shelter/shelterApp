package com.android.shelter.user.tenant.search;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.shelter.FragmentCallback;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.user.Location;
import com.android.shelter.R;
import com.android.shelter.user.UserSessionManager;
import com.android.shelter.user.tenant.savedsearch.SavedSearch;
import com.android.shelter.util.ShelterConstants;
import com.android.shelter.util.ShelterPropertyTask;
import com.android.shelter.util.ShelterSavedSearchTask;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPropertyFragment extends Fragment {
    private static final String TAG = "MySearchFragment";
    private static final String DIALOG_FILTER = "DialogFilter";
    private static final String DIALOG_SAVE_SEARCH = "DialogSaveSearch";
    private static final int REQUEST_FILTER_OPTION = 0;
    private static final int REQUEST_SAVE_SEARCH_OPTION = 1;
    private static final int PLACE_PICKER_REQUEST = 2;


    private SearchPropertyFilterCriteria criteria;
    private SavedSearch searchToBeSaved;
    private RecyclerView mPostingRecyclerView;
    private SearchPropertyAdapter mPostingAdapter;
    public static Location appLocation ;

    Button btnFilter;
    Button btnSaveSearch;

    private ProgressDialog mProgressDialog;

    public SearchPropertyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_property, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Search");

        Location.initInstance();
        appLocation = Location.getInstance();

        btnFilter = (Button)rootView.findViewById(R.id.filter_btn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchPropertyFilterFragment dialog = SearchPropertyFilterFragment
                        .newInstance(criteria);
                dialog.setTargetFragment(SearchPropertyFragment.this, REQUEST_FILTER_OPTION);
                dialog.show(fragmentManager, DIALOG_FILTER);

            }
        });

        btnSaveSearch = (Button)rootView.findViewById(R.id.save_search_btn);
        btnSaveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserSessionManager.get(getContext()).isUserSignedIn()){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    SearchPropertySaveSearchFragment dialog = SearchPropertySaveSearchFragment.newInstance(searchToBeSaved,criteria);
                    dialog.setTargetFragment(SearchPropertyFragment.this, REQUEST_SAVE_SEARCH_OPTION);
                    dialog.show(fragmentManager, DIALOG_SAVE_SEARCH);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getContext());
                    builder.setCancelable(true);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.drawable.icon);
                    builder.setMessage("Please sign in to Save Search");
                    builder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });

        LatLng loc=getCurrentLocation(getActivity().getApplicationContext());
        String url = "http://maps.google.com/maps/api/staticmap?center=" + loc.latitude
                + "," + loc.longitude + "&zoom=15&size=400x200&sensor=false&markers=color:red|"
                +loc.latitude+","+loc.longitude;
        Log.d("static url:",url);
        appLocation.setStaticMapUrl(url);

        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
            if (addresses.size() > 0) {
                appLocation.setCityName(addresses.get(0).getLocality());
                appLocation.setPostalCode(addresses.get(0).getPostalCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        criteria =new SearchPropertyFilterCriteria();
        criteria.setCity(appLocation.getCityName());
        criteria.setZipcode(appLocation.getPostalCode());
        criteria.setMapUrl(appLocation.getStaticMapUrl());

        searchToBeSaved = new SavedSearch();

        mPostingRecyclerView = (RecyclerView) rootView.findViewById(R.id.property_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mPostingRecyclerView.setLayoutManager(layoutManager);

        showProgressDialog("Searching properties...");
        String ownerId = UserSessionManager.get(getContext()).getOwnerId();
        new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                ownerId, null, criteria.getKeyword(), criteria.getCity(), null,
                criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
                new FragmentCallback() {
                    @Override
                    public void onTaskDone() {
                        mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                                getActivity(), getActivity().getSupportFragmentManager());
                        mPostingRecyclerView.setAdapter(mPostingAdapter);
                        hideProgressDialog();
                    }
                }).execute();

        mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                getActivity(), getActivity().getSupportFragmentManager());
        mPostingRecyclerView.setAdapter(mPostingAdapter);
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "Menu inflated");
        inflater.inflate(R.menu.menu_search_property, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.post_new_property).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_loc_picker:
                try {
                    PlacePicker.IntentBuilder intentBuilder =new PlacePicker.IntentBuilder();
                    LatLng loc=getCurrentLocation(getActivity().getBaseContext());
                    Geocoder gcd = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = gcd.getFromLocation(loc.latitude, loc.longitude, 1);
                        if (addresses.size() > 0) {
                            appLocation.setCityName(addresses.get(0).getLocality());
                            appLocation.setPostalCode(addresses.get(0).getPostalCode());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    LatLngBounds BOUNDS_CURRENT_LOCATION= new LatLngBounds(
                            new LatLng(loc.latitude,loc.longitude),
                            new LatLng(loc.latitude,loc.longitude)
                    );
                    intentBuilder.setLatLngBounds(BOUNDS_CURRENT_LOCATION);
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILTER_OPTION){
            if(resultCode == Activity.RESULT_OK){
                criteria = (SearchPropertyFilterCriteria)
                        data.getSerializableExtra(SearchPropertyFilterFragment.EXTRA_OPTION);

                showProgressDialog("Filtering properties...");
                String ownerId = UserSessionManager.get(getContext()).getOwnerId();
                new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                        ownerId, null, criteria.getKeyword(), criteria.getCity(), criteria.getZipcode(),
                        criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
                        new FragmentCallback() {
                    @Override
                    public void onTaskDone() {
                        mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                        getActivity(), getActivity().getSupportFragmentManager());
                        mPostingRecyclerView.setAdapter(mPostingAdapter);
                        hideProgressDialog();
                    }
                }).execute();
            }
        }else if (requestCode == REQUEST_SAVE_SEARCH_OPTION){
            if(resultCode == Activity.RESULT_OK){
                searchToBeSaved = (SavedSearch) data.getSerializableExtra(SavedSearch.EXTRA_OPTION);
                try{
                    postSearchData(searchToBeSaved);
                }catch (Exception ex){
                    Log.e(TAG, ex.getStackTrace().toString());
                }
            }
        } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, getActivity());
            LatLng selectedLoc = place.getLatLng();
            String url = "http://maps.google.com/maps/api/staticmap?center=" + selectedLoc.latitude
                    + "," + selectedLoc.longitude + "&zoom=15&size=400x200&sensor=false&markers=color:red|"
                    +selectedLoc.latitude+","+selectedLoc.longitude;
            appLocation.setStaticMapUrl(url);

            Geocoder gcd = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            try {
                List<Address> addresses = gcd.getFromLocation(selectedLoc.latitude, selectedLoc.longitude, 1);
                if (addresses.size() > 0) {
                    appLocation.setCityName(addresses.get(0).getLocality());
                    appLocation.setPostalCode(addresses.get(0).getPostalCode());

                    criteria.setCity(appLocation.getCityName());
                    criteria.setZipcode(appLocation.getPostalCode());
                    criteria.setMapUrl(appLocation.getStaticMapUrl());

                    String ownerId = UserSessionManager.get(getContext()).getOwnerId();
                    new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                            ownerId, null, criteria.getKeyword(), criteria.getCity(), null,
                            criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
                            new FragmentCallback() {
                                @Override
                                public void onTaskDone() {
                                    mPostingAdapter = new SearchPropertyAdapter(PropertyLab.get(getContext()).getProperties(),
                                            getActivity(), getActivity().getSupportFragmentManager());
                                    mPostingRecyclerView.setAdapter(mPostingAdapter);
                                }
                            }).execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postSearchData(SavedSearch searchToBeSaved) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String ownerId = preferences.getString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID,
                ShelterConstants.DEFAULT_INT_STRING);

        jsonObject.put("id", searchToBeSaved.getId().toString());
        jsonObject.put("user",ownerId);
        jsonObject.put("name",searchToBeSaved.getSavedSearchName());
        jsonObject.put("frequency",searchToBeSaved.getFrequency());

        jsonObject.put("keyword",searchToBeSaved.getKeyword());
        jsonObject.put("city",searchToBeSaved.getCity());
        jsonObject.put("zipcode",searchToBeSaved.getZipcode());
        jsonObject.put("minrent",searchToBeSaved.getMinRent());
        jsonObject.put("maxrent",searchToBeSaved.getMaxRent());
        jsonObject.put("propertyType",searchToBeSaved.getPostingType());

        jsonObject.put("haskeyword",searchToBeSaved.hasKeyword());
        jsonObject.put("hascity",searchToBeSaved.hasCity());
        jsonObject.put("haszipcode",searchToBeSaved.hasZipcode());
        jsonObject.put("hasminrent",searchToBeSaved.hasMinRent());
        jsonObject.put("hasmaxrent",searchToBeSaved.hasMaxRent());
        jsonObject.put("haspropertyType", searchToBeSaved.hasPostingType());
        jsonObject.put("staticmapurl", searchToBeSaved.getMapURL());


//        Log.d(TAG, jsonObject.toString());
        showProgressDialog("Saving your search...");
        new ShelterSavedSearchTask(getContext(), "savesearch/", "POST", true, jsonObject,
                searchToBeSaved, new FragmentCallback() {
            @Override
            public void onTaskDone() {
                hideProgressDialog();
                Toast.makeText(getActivity() , "Search saved successfully!", Toast.LENGTH_LONG).show();
            }
        }).execute();
    }

    public LatLng getCurrentLocation(Context c) {
        LocationManager service =(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = service.getBestProvider(criteria, true);
        if (ContextCompat.checkSelfPermission(c.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            return new LatLng(37.322993, -121.883200);
        }

        android.location.Location location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(location==null){
            return new LatLng(37.322993, -121.883200);
        }
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        return userLocation;
    }

    private void showProgressDialog(String message){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.hide();
        }
    }
}
