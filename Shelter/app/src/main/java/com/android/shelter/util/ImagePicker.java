package com.android.shelter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.android.shelter.R;
import com.android.shelter.helper.PropertyImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility to show user the photo selection options,
 * handles camera images and finally gives the bitmap which can used to show on view.
 * <p><b>NOTE: Google blocks login attempt for sender, so in this case the account used has
 * 'Access for less secure apps' ON/enabled.</b></p>
 * Created by rishi on 5/1/16.
 */
public class ImagePicker {
    private static final String TAG = "ImagePicker";

    private static ImagePicker sImagePicker;
    private Bitmap imageBitmap;


    public static ImagePicker get(Context context) {
        if (sImagePicker == null) {
            sImagePicker = new ImagePicker(context);
        }
        return sImagePicker;
    }

    private ImagePicker(Context context){}

    public static Intent getGalleryImagePickIntent(Context context){
        Intent selectPictureIntent = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectPictureIntent.setType("image/*");
        return  selectPictureIntent;
    }
    /**
     * Shows choices for taking photos, all available options are shown.
     * @param context
     * @return
     */
    public static Intent getPickImageIntent(Context context) {
        Intent intentChoices = null;

        List<Intent> intentList = new ArrayList<>();

        // TODO Only selects one image at time from gallery, try multiple
        Intent selectPictureIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectPictureIntent.setType("image/*");
        selectPictureIntent.putExtra("isCamera", false);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra("isCamera", true);
        intentList = addIntentsToList(context, intentList, selectPictureIntent);
        intentList = addIntentsToList(context, intentList, takePictureIntent);

        if (intentList.size() > 0) {
            intentChoices = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            intentChoices.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return intentChoices;
    }

    /**
     * Adds intents to list
     * @param context
     * @param list
     * @param intent
     * @return
     */
    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }


    /**
     * Returns bitmap to display on view
     * @return
     */
    public PropertyImage getPropertyImage(Context context, Intent data) {
        Log.d(TAG, "Creating and returing new PropertyImage");
        Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver()
                .query(selectedImage, filePathColumn, null, null,
                        null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        Log.d(TAG, "Image name "+ picturePath);
        cursor.close();

        String[] imageNameString = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor imageNameCursor = context.getContentResolver()
                .query(selectedImage, imageNameString, null, null,
                        null);
        imageNameCursor.moveToFirst();
        String imageName = imageNameCursor.getString(imageNameCursor.getColumnIndex(imageNameString[0]));
        Log.d(TAG, "Image name "+ imageName);
        imageNameCursor.close();

        PropertyImage image = new PropertyImage();
        image.setImageName(imageName);
        image.setImagePath(picturePath);
        if(capturedImage != null){
            image.setImageBitMap(capturedImage);
        }else {
            image.setImageBitMap(BitmapFactory.decodeFile(picturePath));
        }
        //image.setImageString64(getImageBytes(image.getImageBitMap()));
        return image;
    }

//    public byte[] getImageBytes(PropertyImage propertyImage){
//        // convert bitmap to byte
//        imageBitmap = null;
//        if(propertyImage.getImageBitMap() == null){
//            new AsyncTask<String, Void, Bitmap>(){
//                @Override
//                protected void onPostExecute(Bitmap bitmap) {
//                    Log.d(TAG, "Image downloaded");
//                    imageBitmap = bitmap;
//                }
//
//                @Override
//                protected Bitmap doInBackground(String... params) {
//                    String urlOfImage = params[0];
//                    Bitmap logo = null;
//                    try {
//                        InputStream is = new URL(urlOfImage).openStream();
//                        logo = BitmapFactory.decodeStream(is);
//                    } catch (Exception e) { // Catch the download exception
//                        e.printStackTrace();
//                    }
//                    return logo;
//                }
//            }.execute(propertyImage.getImagePath());
//
//        }else {
//            imageBitmap = propertyImage.getImageBitMap();
//        }
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] imageInByte = stream.toByteArray();
//        return imageInByte;
//    }

    public byte[] getImageBytes(PropertyImage propertyImage){
        // convert bitmap to byte
        imageBitmap = propertyImage.getImageBitMap();
        if(imageBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            return imageInByte;
        }
        return null;
    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        if(width != 0){
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }else {
            return b;
        }
    }

    public static Bitmap scaleToFitHeight(Bitmap b, int height)
    {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }
}
