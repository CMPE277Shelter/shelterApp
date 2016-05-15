package com.android.shelter.user.landlord;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;
import com.android.shelter.property.PropertyLab;
import com.android.shelter.util.DownloadImageTask;
import com.android.shelter.util.ImagePicker;
import com.android.shelter.util.ImageResizer;

import java.util.UUID;

/**
 * Display fragment to show the complete/full image when user clicks on the thumbnails while posting a property.
 * Created by rishi on 5/6/16.
 */
public class ImageDisplayFragment extends Fragment {
    private static final String TAG = "ImageDisplayFragment";
    private static final String ARG_IMAGE_ID = "image_id";

    private PropertyImage mPropertyImage;
    private ImageView mImage;

    public static ImageDisplayFragment newInstance(UUID imageId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE_ID, imageId);

        ImageDisplayFragment fragment = new ImageDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        UUID imageId = (UUID) getArguments().getSerializable(ARG_IMAGE_ID);
        mPropertyImage = PropertyLab.get(getContext()).getPropertyImage(imageId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_display, container, false);

        mImage = (ImageView) v.findViewById(R.id.property_full_image);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){   //API LEVEL 13
            display.getSize(size);
        }else{
            size.x = display.getWidth();
            size.y = display.getHeight();
        }
        int width = size.x;
        int height = size.y;

        if(mPropertyImage.getImageBitMap() == null){
            if(mPropertyImage.getImageResourceId() == 0){
                new DownloadImageTask(mImage).execute(mPropertyImage.getImagePath());
            }else{
                mImage.setBackgroundResource(mPropertyImage.getImageResourceId());
            }
        }else {
            mImage.setImageBitmap(ImageResizer
                    .decodeSampledBitmapFromFile(mPropertyImage.getImagePath(), width, height));
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Home clicked");
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
