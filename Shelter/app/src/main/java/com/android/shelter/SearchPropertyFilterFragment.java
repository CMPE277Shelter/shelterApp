package com.android.shelter;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


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


    public static SearchPropertyFilterFragment newInstance() {
        Bundle args = new Bundle();
        SearchPropertyFilterFragment fragment = new SearchPropertyFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_search_property_filter, null);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.filtered_property_type);
        ((RadioButton) mRadioGroup.findViewById(R.id.all_type)).setChecked(true);

        mCity =(EditText) v.findViewById(R.id.filtered_city);
        mKeyword=(EditText) v.findViewById(R.id.filtered_keyword);
        mZipCode =(EditText) v.findViewById(R.id.filtered_zip);
        mMaxRent=(EditText) v.findViewById(R.id.filtered_max_rent);
        mMinRent =(EditText) v.findViewById(R.id.filtered_min_rent);




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