package com.android.shelter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearch implements Serializable {
    public static final String EXTRA_OPTION =
            "com.android.shelter.save_option";
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

    public String getMinRent() {
        return minRent;
    }

    public void setMinRent(String minRent) {
        this.minRent = minRent;
    }

    public String getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(String maxRent) {
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
        savedSearchName="";
        hasSavedSearchName=false;
        postingType="";
        hasPostingType=false;
        minRent="";
        hasMinRent=false;
        maxRent="";
        hasMaxRent=false;
        keyword="";
        hasKeyword=false;
        city="";
        hasCity=false;
        zipcode="";
        hasZipcode=false;
        frequency="Realtime";
        hasFrequency=false;
    }

    private UUID id;
    private String savedSearchName;
    private String postingType;
    private String minRent;
    private String maxRent;
    private String keyword;
    private String city;
    private String zipcode;
    private String frequency;

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean hasSavedSearchName() {
        return hasSavedSearchName;
    }

    public void setHasSavedSearchName(boolean hasSavedSearchName) {
        this.hasSavedSearchName = hasSavedSearchName;
    }

    public boolean hasPostingType() {
        return hasPostingType;
    }

    public void setHasPostingType(boolean hasPostingType) {
        this.hasPostingType = hasPostingType;
    }

    public boolean hasMinRent() {
        return hasMinRent;
    }

    public void setHasMinRent(boolean hasMinRent) {
        this.hasMinRent = hasMinRent;
    }

    public boolean hasMaxRent() {
        return hasMaxRent;
    }

    public void setHasMaxRent(boolean hasMaxRent) {
        this.hasMaxRent = hasMaxRent;
    }

    public boolean isHasKeyword() {
        return hasKeyword;
    }

    public void setHasKeyword(boolean hasKeyword) {
        this.hasKeyword = hasKeyword;
    }

    public boolean hasCity() {
        return hasCity;
    }

    public void setHasCity(boolean hasCity) {
        this.hasCity = hasCity;
    }

    public boolean hasZipcode() {
        return hasZipcode;
    }

    public void setHasZipcode(boolean hasZipcode) {
        this.hasZipcode = hasZipcode;
    }

    public boolean hasFrequency() {
        return hasFrequency;
    }

    public void setHasFrequency(boolean hasFrequency) {
        this.hasFrequency = hasFrequency;
    }

    private boolean hasSavedSearchName;
    private boolean hasPostingType;
    private boolean hasMinRent;
    private boolean hasMaxRent;
    private boolean hasKeyword;
    private boolean hasCity;
    private boolean hasZipcode;
    private boolean hasFrequency;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    private int photoId;
}
