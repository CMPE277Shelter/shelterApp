package com.android.shelter;

import java.util.UUID;

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearch {
    public String getSavedSearchName() {
        return savedSearchName;
    }

    public void setSavedSearchName(String savedSearchName) {
        this.savedSearchName = savedSearchName;
    }

    public String getPostingType() {
        return postingType;
    }

    public void setPostingType(String postingType) {
        this.postingType = postingType;
    }

    public double getMinRent() {
        return minRent;
    }

    public void setMinRent(double minRent) {
        this.minRent = minRent;
    }

    public double getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(double maxRent) {
        this.maxRent = maxRent;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public UUID getId(){
        return id;
    }

    public SavedSearch() {
        id = UUID.randomUUID();
    }
    private UUID id;
    private String savedSearchName;
    private String postingType;
    private double minRent;
    private double maxRent;
    private String keyword;
    private String city;
    private String zipcode;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    private int photoId;
}
