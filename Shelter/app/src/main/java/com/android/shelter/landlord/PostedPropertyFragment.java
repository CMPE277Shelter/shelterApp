package com.android.shelter.landlord;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.android.shelter.Property;
import com.android.shelter.PropertyLab;
import com.android.shelter.R;
import com.android.shelter.util.IncrementViewCount;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollable;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;

import java.util.UUID;

/**
 * Shows detail view of posted property which are posted by landlord
 */
public class PostedPropertyFragment extends Fragment implements OnScrollChangedCallback{
    private static final String TAG = "PostedPropertyFragment";
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

        // Increments page views
        // TODO How it undestands which property?
        //new IncrementViewCount().execute("http://ec2-52-33-84-233.us-west-2.compute.amazonaws.com:5000/incrementViewCount/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mAddress.setText(mProperty.getAddress());

        mPropertyRooms = (TextView) v.findViewById(R.id.posted_property_rooms);
        mPropertyRooms.append(mProperty.getDisplayRoom());
        mPropertyType = (TextView) v.findViewById(R.id.posted_property_type);
        mPropertyType.append(mProperty.getType());
        mBath = (TextView) v.findViewById(R.id.posted_property_bath);
        mBath.append(mProperty.getBath());
        mFloorArea = (TextView) v.findViewById(R.id.posted_property_floor_area);
        mFloorArea.append(mProperty.getFloorArea());

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
        mRentedOrCancel.setOnClickListener(new RentedOrCancelClickListener());


        ObservableScrollable scrollView = (ObservableScrollable) v.findViewById(R.id.posted_property_scrollview);
        scrollView.setOnScrollChangedCallback(this);

        onScroll(-1, 0);
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
            case android.R.id.edit:
                Log.d(TAG, "Edit clicked restart the PostPropertyActivity");
                Intent postPropertyActivity = PostPropertyActivity.newIntent(getContext(), mProperty.getId());
                startActivity(postPropertyActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private class RentedOrCancelClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AsyncTask<Void, Void, String> updateRentedOrCancelTask = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    // TODO Add code to check rented or cancel
                    return null;
                }
                @Override
                protected void onPostExecute(String aString) {
                    super.onPostExecute(aString);
                    Log.d(TAG, "Favorite updated in database");
                }
            }.execute();
        }
    }

    /**
     * Updates alpha for toolbar.
     * @param scrollRatio
     */
    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mToolbar.getBackground().setAlpha(newAlpha);
    }

    /**
     * Scrolling effect
     * @param scrollPosition
     */
    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mImage.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

}


