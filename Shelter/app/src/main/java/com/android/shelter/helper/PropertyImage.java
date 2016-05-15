package com.android.shelter.helper;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Model for proeprty image.
 * Created by rishi on 5/6/16.
 */
public class PropertyImage {
    private UUID mId;
    private Bitmap mImageBitMap;
    private String mImagePath;
    private String ownerId;
    private String propertyId;
    private String imageString64;
    private String imageName;

    public PropertyImage(){
        mId = UUID.randomUUID();
    }

    public UUID getId(){
        return mId;
    }
    public Bitmap getImageBitMap() {
        return mImageBitMap;
    }

    public void setImageBitMap(Bitmap mImageBitMap) {
        this.mImageBitMap = mImageBitMap;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getImageString64() {
        return imageString64;
    }

    public void setImageString64(String imageString64) {
        this.imageString64 = imageString64;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
