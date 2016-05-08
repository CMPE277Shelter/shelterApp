package com.android.shelter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.android.shelter.R.layout;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.helper.PropertyImageAdapter;
import com.android.shelter.util.ImagePicker;
import com.android.shelter.util.SendEmail;

import java.util.ArrayList;
import java.util.List;

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
        EditText propertyName = (EditText) view.findViewById(R.id.property_name);

        EditText street = (EditText) view.findViewById(R.id.street_name);
        EditText city = (EditText) view.findViewById(R.id.city_name);
        EditText state = (EditText) view.findViewById(R.id.state_name);
        EditText zipcode = (EditText) view.findViewById(R.id.zip);

        EditText totalRooms = (EditText) view.findViewById(R.id.number_of_rooms);
        EditText monthlyRent = (EditText) view.findViewById(R.id.monthly_rent);

        EditText contactName = (EditText) view.findViewById(R.id.person_name);
        EditText contactEmail = (EditText) view.findViewById(R.id.email);
        EditText contactPhone = (EditText) view.findViewById(R.id.phone_number);

        EditText propertyDescription = (EditText) view.findViewById(R.id.description);

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
                String [] address = {"randive.rishiraj@gmail.com"};
                String subject = "Hello from shelter";
                composeEmail();
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

                    mImageList.add(ImagePicker.getPropertyImage(getContext(), data));
                    setupAdapter();

//                    // convert bitmap to byte
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    yourImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte imageInByte[] = stream.toByteArray();
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

    /**
     * onClick added for radio buttons in {@link layout#post_property_fragment}
     * @param view
     */
    public void onPropertyTypeClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.house_type:
                if (checked)
                    Log.d(TAG, "Its house type");
                    break;
            case R.id.townhouse_type:
                if (checked)
                    Log.d(TAG, "Its townhouse type");
                    break;
            case R.id.apartment_type:
                if (checked)
                    Log.d(TAG, "Its apartment type");
                    break;
        }

    }
}