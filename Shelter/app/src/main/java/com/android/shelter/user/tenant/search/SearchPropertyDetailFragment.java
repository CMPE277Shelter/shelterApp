package com.android.shelter.user.tenant.search;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.shelter.R;
import com.android.shelter.property.Property;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.util.DownloadImageTask;
import com.android.shelter.util.RentedOrCancelTask;
import com.android.shelter.util.ShelterConstants;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPropertyDetailFragment extends Fragment  {
    private static final String TAG = "SearchPropertyDetailFragment";
    private static final String ARG_PROPERTY_ID = "property_id";

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
    private Toolbar mToolbar;
    public SearchPropertyDetailFragment() {
        // Required empty public constructor
    }

    public static SearchPropertyDetailFragment newInstance(UUID propertyId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, propertyId);

        SearchPropertyDetailFragment fragment = new SearchPropertyDetailFragment();
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

        // Increments page views
        // TODO How it undestands which property?
        //new IncrementViewCount().execute("http://ec2-52-33-84-233.us-west-2.compute.amazonaws.com:5000/incrementViewCount/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_property_detail, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.search_property_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mProperty.getName());


        mImage = (ImageView) v.findViewById(R.id.detail_thumbnail);
        new DownloadImageTask(mImage).execute("" +
                "http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=p1.jpg");

        mPropertyName = (TextView) v.findViewById(R.id.detail_name);
        mPropertyName.setText(mProperty.getName());

        mAddress = (TextView) v.findViewById(R.id.detail_address);
        mAddress.setText(mProperty.getAddress());

        mPropertyRooms = (TextView) v.findViewById(R.id.detail_beds);
        mPropertyRooms.append(mProperty.getDisplayRoom());
        mPropertyType = (TextView) v.findViewById(R.id.detail_type);
        mPropertyType.append(mProperty.getType());
        mBath = (TextView) v.findViewById(R.id.detail_baths);
        mBath.append(mProperty.getDisplayBath());
        mFloorArea = (TextView) v.findViewById(R.id.detail_floorArea);
        mFloorArea.append(mProperty.getDisplayFloorArea());

        mContactPhone = (TextView) v.findViewById(R.id.detail_landlord_number);
        mContactPhone.setText(mProperty.getPhoneNumber());
        mContactEmail = (TextView) v.findViewById(R.id.detail_landlord_email);
        mContactEmail.setText(mProperty.getEmail());

        mRent = (TextView) v.findViewById(R.id.detail_rent);
        mRent.setText(mProperty.getDisplayRent());

        mDesc = (TextView) v.findViewById(R.id.detail_property_desc);
        mDesc.setText(mProperty.getDescription());

        return v;
    }
}
