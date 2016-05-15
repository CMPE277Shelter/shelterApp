package com.android.shelter.user.landlord;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.shelter.R;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.util.RentedOrCancelTask;
import com.android.shelter.util.ShelterConstants;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Shows detail view of posted property which are posted by landlord
 */
public class PostedPropertyFragment extends Fragment {
    private static final String TAG = "PostedPropertyFragment";
    private static final String ARG_PROPERTY_ID = "property_id";
    public static final int REQUEST_EDIT = 1;

    private Property mProperty;
    private ImageView mImage;
    private TextView mPropertyName;
    private TextView mAddress;
    private TextView mPropertyRooms;
    private TextView mPropertyType;
    private TextView mRent;
    private TextView mBath;
    private TextView mFloorArea;
    private TextView mContactName;
    private TextView mContactPhone;
    private TextView mContactEmail;
    private TextView mDesc;
    private TextView mPageViews;

    private CheckBox mRentedOrCancel;

    private Toolbar mToolbar;
    private int mLastDampedScroll;


    public static PostedPropertyFragment newInstance(UUID propertyId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);

        PostedPropertyFragment fragment = new PostedPropertyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        UUID id = (UUID) getArguments().getSerializable(ARG_PROPERTY_ID);
        mProperty = PropertyLab.get(getContext()).getProperty(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posted_property_detail, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mProperty.getName());

        mImage = (ImageView) v.findViewById(R.id.posted_property_image);
        mImage.setImageResource(R.drawable.header);

        mPropertyName = (TextView) v.findViewById(R.id.posted_property_name);
        mPropertyName.setText(mProperty.getName());

        mAddress = (TextView) v.findViewById(R.id.posted_property_address);
        mAddress.setText(mProperty.getAddress());

        mPropertyRooms = (TextView) v.findViewById(R.id.posted_property_rooms);
        mPropertyRooms.append(mProperty.getDisplayRoom());
        mPropertyType = (TextView) v.findViewById(R.id.posted_property_type);
        mPropertyType.append(mProperty.getType());
        mBath = (TextView) v.findViewById(R.id.posted_property_bath);
        mBath.append(mProperty.getDisplayBath());
        mFloorArea = (TextView) v.findViewById(R.id.posted_property_floor_area);
        mFloorArea.append(mProperty.getDisplayFloorArea());
        mPageViews = (TextView) v.findViewById(R.id.page_reviews);
        mPageViews.setText(mProperty.getDisplayPageViews());

        mContactName = (TextView) v.findViewById(R.id.posted_property_contact_name);
        mContactName.setText(mProperty.getContactName());
        mContactPhone = (TextView) v.findViewById(R.id.posted_property_contact_phone);
        mContactPhone.setText(mProperty.getPhoneNumber());
        mContactEmail = (TextView) v.findViewById(R.id.posted_property_contact_email);
        mContactEmail.setText(mProperty.getEmail());

        mRent = (TextView) v.findViewById(R.id.posted_property_rent);
        mRent.setText(mProperty.getDisplayRent());

        mDesc = (TextView) v.findViewById(R.id.posted_property_desc);
        mDesc.setText(mProperty.getDescription());

        mRentedOrCancel = (CheckBox) v.findViewById(R.id.rented_or_cancel);
        mRentedOrCancel.setChecked(mProperty.isRentedOrCancel());

        mRentedOrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

                    JSONObject putData = new JSONObject();
                    putData.put("isRented", mRentedOrCancel.isChecked());
                    putData.put(ShelterConstants.OWNER_ID, preferences.getString(ShelterConstants.SHARED_PREFERENCE_OWNER_ID, ShelterConstants.DEFAULT_INT_STRING));
                    putData.put(ShelterConstants.PROPERTY_ID, mProperty.getId());

                    new RentedOrCancelTask(getContext(), "/isrented/", putData).execute();
                }catch (JSONException ex){

                }

            }
        });
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posted_property, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                if (NavUtils.getParentActivityIntent(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.edit_property:
                Log.d(TAG, "Edit clicked restart the PostPropertyActivity");
                Intent postPropertyActivity = PostPropertyActivity.newIntent(getContext(), mProperty.getId());
                startActivity(postPropertyActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}


