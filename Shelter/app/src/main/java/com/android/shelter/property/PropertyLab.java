package com.android.shelter.property;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.android.shelter.helper.PropertyImage;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Prasanna on 5/5/16.
 */
public class PropertyLab {
    private static PropertyLab sPropertyLab;
    private ArrayList<Property> mProperties;
    private List<PropertyImage> mPropertyImages;
    private Context mAppContext;


    private PropertyLab(Context appContext) {
        mAppContext = appContext;
        mProperties = new ArrayList<>();
        mPropertyImages = new ArrayList<>();
    }

    public void addProperty(Property c){
        mProperties.add(c);
    }

    public void clearPropertyList(){
        mProperties=new ArrayList<Property>();
    }

    public static PropertyLab get(Context c) {
        if (sPropertyLab == null) {
            sPropertyLab = new PropertyLab(c.getApplicationContext());
        }
        return sPropertyLab;
    }

    public ArrayList<Property> getProperties() {
        return mProperties;
    }

    public Property getProperty(UUID id) {
        for(Property property : mProperties){
            if(property.getId().equals(id)){
                return property;
            }
        }
        return null;
    }

    public void updatePropertyFavorite(UUID id, boolean isFavorite){
        for(Property property : mProperties){
            if(property.getId().equals(id)){
                property.setFavorite(isFavorite);
            }
        }
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

    public void clearPropertyImageList(){
        mPropertyImages = new ArrayList<PropertyImage>();
    }

}
