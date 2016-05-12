package com.android.shelter.tenant;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.shelter.Property;
import com.android.shelter.PropertyLab;
import com.android.shelter.R;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;

import java.util.UUID;


public class PropertyFragment extends Fragment implements OnScrollChangedCallback {
    private static final String TAG = "PropertyFragment";
    private static final String ARG_PROPERTY_ID = "property_id";

    private Property mProperty;
    private ImageView mImage;
    private TextView mPropertyName;
    private TextView mAddress;
    private TextView mPropertyRooms;
    private TextView mPropertyType;
    private TextView mRent;
    private TextView mContactName;
    private TextView mContactPhone;
    private TextView mContactEmail;
    private TextView mDesc;

    private Toolbar mToolbar;
    private int mLastDampedScroll;



    public static PropertyFragment newInstance(UUID imageId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROPERTY_ID, imageId);

       PropertyFragment fragment = new PropertyFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_posted_property_detail, container, false);

        mToolbar = (Toolbar) v.findViewById(R.id.posted_property_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        mImage = (ImageView) v.findViewById(R.id.posted_property_image);
        mImage.setImageResource(R.drawable.header);

        mPropertyName = (TextView) v.findViewById(R.id.posted_property_name);
        mPropertyName.setText(mProperty.getName());

        mAddress = (TextView) v.findViewById(R.id.posted_property_address);
        mAddress.setText("4th Street, San Jose, 95112");

        mPropertyRooms = (TextView) v.findViewById(R.id.posted_property_rooms);
        mPropertyRooms.append("4 rooms");
        mPropertyType = (TextView) v.findViewById(R.id.posted_property_type);
        mPropertyType.append("Townhouse");

        mContactName = (TextView) v.findViewById(R.id.posted_property_contact_name);
        mContactName.setText("Rishiraj Randive");
        mContactPhone = (TextView) v.findViewById(R.id.posted_property_contact_phone);
        mContactPhone.setText("6692389963");
        mContactEmail = (TextView) v.findViewById(R.id.posted_property_contact_email);
        mContactEmail.setText("randive.rishiraj@gmail.com");

        mRent = (TextView) v.findViewById(R.id.posted_property_rent);
        mRent.setText("$3400");

        mDesc = (TextView) v.findViewById(R.id.posted_property_desc);
        mDesc.setText(getString(R.string.loren_ipsum));


        ObservableScrollable scrollView = (ObservableScrollable) v.findViewById(R.id.posted_property_scrollview);
        scrollView.setOnScrollChangedCallback(this);

        onScroll(-1, 0);
        return v;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_posted_property, menu);
    }
    // TODO: Rename method, update argument and hook method into UI event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.edit:
                Log.d(TAG, "Edit clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onScroll(int l, int scrollPosition) {
        int headerHeight = mImage.getHeight() - mToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0) {
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        }
        updateActionBarTransparency(ratio);
        updateParallaxEffect(scrollPosition);
    }
    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mToolbar.getBackground().setAlpha(newAlpha);
    }
    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mImage.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }
}
