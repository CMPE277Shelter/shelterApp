package com.android.shelter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPropertySaveSearchFragment extends DialogFragment {
    private static final String TAG = "SearchPropertySaveSearchFragment";
    public static final String EXTRA_OPTION =
            "com.android.shelter.filter_option";

    private static final String ARG_OPTION = "option";

    private RadioGroup mRadioGroup;
    private EditText mSearchName;
    private RadioButton mPropertyType;
    private View mView;
    private static SavedSearch mSearchToBeSaved;

    public static SearchPropertySaveSearchFragment newInstance(SavedSearch searchToBeSaved) {
        Bundle args = new Bundle();
        SearchPropertySaveSearchFragment fragment = new SearchPropertySaveSearchFragment();
        fragment.setArguments(args);
        mSearchToBeSaved=searchToBeSaved;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_search_property_save_search, null);

        mRadioGroup = (RadioGroup) v.findViewById(R.id.save_search_frequency);
        ((RadioButton) mRadioGroup.findViewById(getFrequencyTypeId(mSearchToBeSaved.getFrequency()))).setChecked(true);

        mSearchName =(EditText) v.findViewById(R.id.save_search_name);
        mSearchName.setText(mSearchToBeSaved.getSavedSearchName());

        ((Button)v.findViewById(R.id.save_search_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedSearch savedSearch = new SavedSearch();
                savedSearch.setSavedSearchName(mSearchName.getText().toString());

                int checkedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
                savedSearch.setFrequency(getFrequencyType(checkedRadioButtonId));
                getDialog().dismiss();
                sendResult(Activity.RESULT_OK, savedSearch);
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
                .setTitle("Search Info")
                .create();
    }
    private int getFrequencyTypeId(String type){
        if(type.equals("Realtime")){
            return R.id.frequency_real_time;
        }else if(type.equals("Daily")){
            return R.id.frequency_daily;
        }else {
            return R.id.frequency_weekly;
        }
    }

    private String getFrequencyType(int checkedRadioButtonId) {
        if(checkedRadioButtonId==R.id.frequency_real_time){
            return ((RadioButton)mView.findViewById(R.id.frequency_real_time)).getText().toString();
        }else if(checkedRadioButtonId==R.id.frequency_daily){
            return ((RadioButton)mView.findViewById(R.id.frequency_daily)).getText().toString();
        }else {
            return ((RadioButton)mView.findViewById(R.id.frequency_weekly)).getText().toString();
        }
    }

    /**
     * Send result to the required activity
     * @param resultCode
     * @param savedSearch
     */
    private void sendResult(int resultCode, SavedSearch savedSearch) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OPTION, savedSearch);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
