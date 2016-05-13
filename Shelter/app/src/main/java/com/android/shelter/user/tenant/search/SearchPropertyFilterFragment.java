package com.android.shelter.user.tenant.search;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.shelter.user.Location;
import com.android.shelter.R;

import java.io.IOException;
import java.util.List;


public class SearchPropertyFilterFragment extends DialogFragment {

    public static final String EXTRA_OPTION =
            "com.android.shelter.filter_option";

    private static final String ARG_OPTION = "option";

    private RadioGroup mRadioGroup;
    private EditText mKeyword;
    private EditText mCity;
    private EditText mZipCode;
    private EditText mMinRent;
    private EditText mMaxRent;
    private RadioButton mPropertyType;
    private View mView;
    private static SearchPropertyFilterCriteria mCriteria;


    public static SearchPropertyFilterFragment newInstance(SearchPropertyFilterCriteria criteria) {
        Bundle args = new Bundle();
        SearchPropertyFilterFragment fragment = new SearchPropertyFilterFragment();
        fragment.setArguments(args);
        mCriteria=criteria;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Location appLocation = Location.getInstance();
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_search_property_filter, null);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.filtered_property_type);
        ((RadioButton) mRadioGroup.findViewById(getPropertyTypeId(mCriteria.getApartmentType()))).setChecked(true);

        mCity =(EditText) v.findViewById(R.id.filtered_city);
        mCity.setText(mCriteria.getCity());

        mKeyword=(EditText) v.findViewById(R.id.filtered_keyword);
        mKeyword.setText(mCriteria.getKeyword());

        mZipCode =(EditText) v.findViewById(R.id.filtered_zip);
        mZipCode.setText(mCriteria.getZipcode());


        mZipCode.setText(appLocation.getPostalCode());
        mCity.setText(appLocation.getCityName());

        mMaxRent=(EditText) v.findViewById(R.id.filtered_max_rent);
        mMaxRent.setText(mCriteria.getMaxRent());

        mMinRent =(EditText) v.findViewById(R.id.filtered_min_rent);
        mMinRent.setText(mCriteria.getMinRent());



        ((Button)v.findViewById(R.id.filtered_filter_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPropertyFilterCriteria criteria = new SearchPropertyFilterCriteria();
                criteria.setKeyword(mKeyword.getText().toString());
                criteria.setCity(mCity.getText().toString());
                criteria.setMaxRent(mMaxRent.getText().toString());
                criteria.setMinRent(mMinRent.getText().toString());
                criteria.setZipcode(mZipCode.getText().toString());

                int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
                criteria.setApartmentType(getPropertyType(checkedRadioButtonId));

                appLocation.setCityName(criteria.getCity());
                appLocation.setPostalCode(criteria.getZipcode());
                try {
                    final Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                    String location = criteria.getCity();

                    List<Address> addresses = geocoder.getFromLocationName(location, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String url = "http://maps.google.com/maps/api/staticmap?center=" + address.getLatitude()
                                + "," + address.getLongitude() + "&zoom=15&size=400x200&sensor=false&markers=color:red|"
                                +address.getLatitude()+","+address.getLongitude();
                        Log.d("static map url:",url);
                        appLocation.setStaticMapUrl(url);
                        criteria.setMapUrl(url);

                        //Toast.makeText(getActivity().getApplicationContext(), "Modified" + url, Toast.LENGTH_SHORT).show();
                    } else {


                    }
                } catch (IOException e) {
                    e.toString();
                }

                getDialog().dismiss();

                sendResult(Activity.RESULT_OK, criteria);
            }
        });

//        ((Button)v.findViewById(R.id.diaglog_cancel)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDialog().dismiss();
//                sendResult(Activity.RESULT_CANCELED, sortByOption);
//            }
//        });
        mView = v;
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Filter Options")
                .create();
    }

    private int getPropertyTypeId(String type){
        if(type.equals("Apartment")){
            return R.id.apartment_type;
        }else if(type.equals("House")){
            return R.id.house_type;
        }else if(type.equals("Townhouse")){
            return R.id.townhouse_type;
        }else {
            return R.id.all_type;
        }
    }

    private String getPropertyType(int checkedRadioButtonId) {
        if(checkedRadioButtonId==R.id.apartment_type){
            return ((RadioButton)mView.findViewById(R.id.apartment_type)).getText().toString();
        }else if(checkedRadioButtonId==R.id.townhouse_type){
            return ((RadioButton)mView.findViewById(R.id.townhouse_type)).getText().toString();
        }else if(checkedRadioButtonId==R.id.house_type){
            return ((RadioButton)mView.findViewById(R.id.house_type)).getText().toString();
        }else {
            return ((RadioButton)mView.findViewById(R.id.all_type)).getText().toString();
        }
    }

    /**
     * Send result to the required activity
     * @param resultCode
     * @param criteria
     */
    private void sendResult(int resultCode, SearchPropertyFilterCriteria criteria) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OPTION, criteria);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    


}
