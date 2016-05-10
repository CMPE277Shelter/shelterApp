package com.android.shelter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Prasanna on 5/5/16.
 */
public class Property {

    private UUID id;
    private String name;
    private String type;
    private int photoId;
    private boolean isFavorite;
    private String address;
    private String rent;

    public Property() {
        id = UUID.randomUUID();
    }

    Property(String name, String type, int photoId) {
        this.name = name;
        this.type = type;
        this.photoId = photoId;
    }

    public UUID getId(){
        return id;
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

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }
}
