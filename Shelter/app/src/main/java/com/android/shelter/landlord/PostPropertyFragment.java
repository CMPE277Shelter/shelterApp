package com.android.shelter.landlord;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.android.shelter.HomeActivity;
import com.android.shelter.Property;
import com.android.shelter.PropertyLab;
import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.helper.PropertyImageAdapter;
import com.android.shelter.util.ImagePicker;
import com.android.shelter.util.PostPropertyTask;
import com.android.shelter.util.SendEmail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.protocol.BasicHttpContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Fragment for handling operations for posting new property.
 * TODO Sending data to DB
 *
 */
public class PostPropertyFragment extends Fragment {

    private static final String TAG = "PostPropertyFragment";
    private static final int REQUEST_ADD_IMAGE = 1;
    private RelativeLayout mPhotoLayout;
    private RecyclerView mImageRecyclerView;
    private List<PropertyImage> mImageList = new ArrayList<>();
    private Uri mCapturedImageURI;

    private EditText mPropertyName;
    private EditText mStreet;
    private EditText mCity;
    private EditText mState;
    private EditText mZipcode;

    private EditText mTotalRooms;
    private EditText mMonthlyRent;

    private RadioButton mHouseType;
    private RadioButton mTownhouseType;
    private RadioButton mApartmentType;
    private String mPropertyType;

    private EditText mContactName;
    private EditText mContactEmail;
    private EditText mContactPhone;

    private EditText mPropertyDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "On create called");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "On create view");
        View view = inflater.inflate(R.layout.post_property_fragment, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Post new property");


        // TODO Populate complete data in model and send it to DB
        mPropertyName = (EditText) view.findViewById(R.id.property_name);

        mStreet = (EditText) view.findViewById(R.id.street_name);
        mCity = (EditText) view.findViewById(R.id.city_name);
        mState = (EditText) view.findViewById(R.id.state_name);
        mZipcode = (EditText) view.findViewById(R.id.zip);

        mTotalRooms = (EditText) view.findViewById(R.id.number_of_rooms);
        mMonthlyRent = (EditText) view.findViewById(R.id.monthly_rent);

        mHouseType = (RadioButton) view.findViewById(R.id.house_type);
        mTownhouseType = (RadioButton) view.findViewById(R.id.townhouse_type);
        mApartmentType = (RadioButton) view.findViewById(R.id.apartment_type);
        RadioGroup propertyTypes = (RadioGroup) view.findViewById(R.id.property_type);
        propertyTypes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onPropertyTypeClicked(checkedId);
            }
        });

        mContactName = (EditText) view.findViewById(R.id.person_name);
        mContactEmail = (EditText) view.findViewById(R.id.email);
        mContactPhone = (EditText) view.findViewById(R.id.phone_number);

        mPropertyDescription = (EditText) view.findViewById(R.id.description);

        mImageRecyclerView = (RecyclerView) view.findViewById(R.id.property_images_recycler_view);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mImageRecyclerView.setLayoutManager(gridLayoutManager);
        setupAdapter();

        Button btn = (Button) view.findViewById(R.id.select_picture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectedIntent = ImagePicker.getPickImageIntent(getActivity());
                startActivityForResult(selectedIntent, REQUEST_ADD_IMAGE);

            }
        });

        mPhotoLayout = (RelativeLayout) view.findViewById(R.id.pictures_layout);

        Button finish = (Button) view.findViewById(R.id.finish_button);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add code to send data to DB and move to next activity My Postings
                //composeEmail();
                //if(isFormValid()){
                    //showMyPostings();
                try{
                    postPropertyData();
                }catch (Exception ex){
                    Log.e(TAG, ex.getStackTrace().toString());
                }

                //}
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                if (NavUtils.getParentActivityIntent(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != getActivity().RESULT_OK)
            return;
        switch(requestCode) {
            case REQUEST_ADD_IMAGE:
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    mImageList.add(ImagePicker.get(getContext()).getPropertyImage(getContext(), data));
                    setupAdapter();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    /**
     * Set list in the image adapter
     */
    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");
            ImagePicker.get(getContext()).updateImageList(mImageList);
            mImageRecyclerView.setAdapter(new PropertyImageAdapter(mImageList, getActivity(), getActivity().getSupportFragmentManager()));
        }
    }

    /**
     * Method to compose email and call {@link SendEmail} to send the email to user in Asynctask
     */
    public void composeEmail() {
        //Getting content for email
        String email = "randive.rishiraj@gmail.com";
        String subject = "Demo email";
        String message = "Hello this is a sample email from shelter";

        //Creating SendMail object
        SendEmail sm = new SendEmail(getContext(), email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

    public void onPropertyTypeClicked(int id){
        // Check which radio button was clicked
        switch(id) {
            case R.id.house_type:
                Log.d(TAG, "Its house type");
                mPropertyType = "House";
                break;
            case R.id.townhouse_type:
                Log.d(TAG, "Its townhouse type");
                mPropertyType = "Townhouse";
                break;
            case R.id.apartment_type:
                Log.d(TAG, "Its apartment type");
                mPropertyType = "Apartment";
                break;
        }

    }

    private void showMyPostings(){
        Log.d(TAG, "Finishing the activity");
        Intent intent = new Intent();
        intent.putExtra(HomeActivity.EXTRA_FRAGMENT_ID, HomeActivity.MY_POSTING_FRAGMENT_ID);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private boolean isFormValid(){
        boolean isValid = true;
        if(TextUtils.isEmpty(mPropertyName.getText())){
            mPropertyName.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mStreet.getText())){
            mStreet.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mCity.getText())){
            mCity.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mState.getText())){
            mState.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mZipcode.getText())){
            mZipcode.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mTotalRooms.getText())){
            mTotalRooms.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mMonthlyRent.getText())){
            mMonthlyRent.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(!mHouseType.isChecked() && !mTownhouseType.isChecked() && !mApartmentType.isChecked()){
            mApartmentType.setError(getString(R.string.type_not_selected_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mContactName.getText())){
            mContactName.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mContactPhone.getText())){
            mContactPhone.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        if(TextUtils.isEmpty(mContactEmail.getText())){
            mContactEmail.setError(getString(R.string.blank_error_msg));
            isValid = false;
        }
        return isValid;
    }

    private void postPropertyData() throws JSONException{

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("property_id", UUID.randomUUID().toString());
        Log.d(TAG, "");
        jsonObject.put("owner_id", "4567");

        JSONArray address = new JSONArray();
        JSONObject addressObject = new JSONObject();
        addressObject.put("street", mStreet.getText().toString());
        addressObject.put("city", mCity.getText().toString());
        addressObject.put("state", mState.getText().toString());
        addressObject.put("zipcode", mZipcode.getText().toString());
        address.put(addressObject);
        jsonObject.put("address", address);

        jsonObject.put("property_type", mPropertyType);

        JSONArray details = new JSONArray();
        JSONObject detailsObject = new JSONObject();
        detailsObject.put("rooms", mTotalRooms.getText().toString());
        detailsObject.put("bath", "2");
        detailsObject.put("floor_area", "2000");
        details.put(detailsObject);
        jsonObject.put("details", details);

        JSONArray rentDetails = new JSONArray();
        JSONObject rentObject = new JSONObject();
        rentObject.put("rent", mMonthlyRent.getText().toString());
        rentDetails.put(rentObject);
        jsonObject.put("rent_details", rentDetails);

        JSONArray ownerInfo = new JSONArray();
        JSONObject ownerObject = new JSONObject();
        ownerObject.put("phone_number", mContactPhone.getText().toString());
        ownerObject.put("display_phone", mContactPhone.getText().toString());
        ownerObject.put("email", mContactEmail.getText().toString());
        ownerInfo.put(ownerObject);
        jsonObject.put("owner_contact_info", ownerInfo);

        jsonObject.put("is_favorite", false);
        jsonObject.put("is_rented_or_cancel", false);
        jsonObject.put("description", mPropertyDescription.getText().toString());

        JSONArray moreInfo = new JSONArray();
        JSONObject moreObject = new JSONObject();
        moreObject.put("lease_type", "Percentage");
        moreObject.put("deposit", "2000");
        moreInfo.put(moreObject);
        jsonObject.put("more", moreInfo);

        Log.d(TAG, jsonObject.toString());

        new PostPropertyTask(getContext(), "postings/", true, jsonObject).execute();
    }
}

