package com.android.shelter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prasanna on 5/5/16.
 */
public class Property {
    public Property() {
    }

    Property(String name, String type, int photoId) {
        this.name = name;
        this.type = type;
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    String name;
    String type;
    int photoId;
//    private List<Property> properties;


}
