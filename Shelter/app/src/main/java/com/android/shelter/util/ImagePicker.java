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

    public List<PropertyImage> mPropertyImages = new ArrayList<>();
    private static ImagePicker sImagePicker;


    public static ImagePicker get(Context context) {
        if (sImagePicker == null) {
            sImagePicker = new ImagePicker(context);
        }
        return sImagePicker;
    }

    private ImagePicker(Context context){}
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
        cursor.close();

        PropertyImage image = new PropertyImage();
        image.setImagePath(picturePath);
        if(capturedImage != null){
            image.setImageBitMap(capturedImage);
        }else {
            image.setImageBitMap(BitmapFactory.decodeFile(picturePath));
        }
        //image.setImageString64(getImageString64(image.getImageBitMap()));
        return image;
    }

    /**
     * Keeps the updated property image list
     * @param imageList
     */
    public void updateImageList(List<PropertyImage> imageList){
        mPropertyImages = new ArrayList<>();
        mPropertyImages = imageList;
    }

    /**
     * Returns entire property image list
     * @return
     */
    public List<PropertyImage> getPropertyImages(){
        return mPropertyImages;
    }

    public PropertyImage getPropertyImage(UUID id){
        for(PropertyImage image : mPropertyImages){
            if(image.getId().equals(id)){
                return image;
            }
        }
        return null;
    }

    public byte[] getImageString64(Bitmap image){
        // convert bitmap to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
//        try{
//            System.gc();
//            return Base64.encodeToString(imageInByte, Base64.NO_WRAP);
//        }catch (OutOfMemoryError ex){
//            stream = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.PNG, 50, stream);
//            imageInByte = stream.toByteArray();
//
//            return Base64.encodeToString(imageInByte, Base64.NO_WRAP);
//        } catch (Exception ex){
//            Log.d(TAG, ex.toString());
//        }
        return imageInByte;
    }

    public List<String> getPropertyImageString64(){
        List<String> imageStrings = new ArrayList<>();
        for (PropertyImage image : mPropertyImages){
            // convert bitmap to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.getImageBitMap().compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte imageInByte[] = stream.toByteArray();
            imageStrings.add(Base64.encodeToString(imageInByte, Base64.NO_WRAP));
        }
        return imageStrings;
    }

}
