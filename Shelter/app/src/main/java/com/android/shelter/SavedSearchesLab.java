package com.android.shelter;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Prasanna on 5/10/16.
 */
public class SavedSearchesLab {
    private static SavedSearchesLab sPropertyLab;
    private ArrayList<SavedSearch> mSavedSearches;
    private Context mAppContext;


    private SavedSearchesLab(Context appContext) {
        mAppContext = appContext;
        mSavedSearches = new ArrayList<>();
    }

    public void addSavedSearch(SavedSearch c){
        mSavedSearches.add(c);
    }

    public void clearSavedSearchesList(){
        mSavedSearches =new ArrayList<SavedSearch>();
    }

    public static SavedSearchesLab get(Context c) {
        if (sPropertyLab == null) {
            sPropertyLab = new SavedSearchesLab(c.getApplicationContext());
        }
        return sPropertyLab;
    }

    public ArrayList<SavedSearch> getSavedSearches() {
        return mSavedSearches;
    }

    public SavedSearch getSavedSearch(UUID id) {
        for(SavedSearch savedSearch : mSavedSearches){
            if(savedSearch.getId().equals(id)){
                return savedSearch;
            }
        }
        return null;
    }
}
