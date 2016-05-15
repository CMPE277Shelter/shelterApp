package com.android.shelter.user.tenant.savedsearch;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.shelter.R;
import com.android.shelter.util.DownloadImageTask;

public class SavedSearchHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    private ImageView mSavedSearchImageView;
    private TextView mSavedSearchName;
    private TextView mSavedSearchcriteria;

    private SavedSearch mSavedSearch;

    private Activity mActivity;

    private FragmentManager mFragmentManager;

    /**
     * Constructor for holder
     * @param itemView
     * @param context
     * @param fragmentManager
     */
    public SavedSearchHolder(View itemView, Activity context, FragmentManager fragmentManager) {
        super(itemView);
        itemView.setOnClickListener(this);

        mActivity = context;
        mFragmentManager = fragmentManager;

        mSavedSearchImageView = (ImageView) itemView.findViewById(R.id.search_imageview);
        mSavedSearchName = (TextView) itemView.findViewById(R.id.search_name);
        mSavedSearchcriteria = (TextView) itemView.findViewById(R.id.search_criteria);
    }

    /**
     * Binds the image for the property
     * @param search
     */
    public void bindView(SavedSearch search) {
        mSavedSearch = search;
        mSavedSearchName.setText(search.getSavedSearchName());
        if(search.getMapURL().equals("")){
            new DownloadImageTask(mSavedSearchImageView).execute("" +
                    "http://ec2-52-36-142-168.us-west-2.compute.amazonaws.com:5000/drawable?filename=p1.jpg");
        }else{
            new DownloadImageTask(mSavedSearchImageView).execute(search.getMapURL());
        }

        mSavedSearchcriteria.setText(formCriteriaText(search));

    }

    public String formCriteriaText(SavedSearch search) {
        StringBuilder searchCriteria=new StringBuilder();
        boolean isFirstElementSet = false;
        boolean isLastElement;

        if(search.hasKeyword()){
            searchCriteria.append(search.getKeyword());
            isFirstElementSet=true;
        }

        if(search.hasCity() && isFirstElementSet){
            searchCriteria.append(", "+search.getCity());
        }else if(search.hasCity()){
            searchCriteria.append(search.getCity());
            isFirstElementSet=true;
        }

        if(search.hasZipcode() && isFirstElementSet){
            searchCriteria.append(", Property Type : "+search.getZipcode());
        }else if(search.hasZipcode()){
            searchCriteria.append(search.getZipcode());
            isFirstElementSet=true;
        }

        if(search.hasPostingType() && isFirstElementSet){
            searchCriteria.append(", "+search.getPostingType());
        }else if(search.hasPostingType()){
            searchCriteria.append("Property Type : "+search.getPostingType());
            isFirstElementSet=true;
        }

        if(isFirstElementSet && (search.hasMinRent() || search.hasMaxRent())){
            searchCriteria.append(", ");
        }

        if(search.hasMinRent() && search.hasMaxRent()){
            searchCriteria.append("Price Range : $"+search.getMinRent()+"-"+search.getMaxRent());
        }else if(search.hasMinRent()){
            searchCriteria.append("Price Range : >= $"+search.getMinRent());
        }else if(search.hasMaxRent()){
            searchCriteria.append("Price Range : <= $"+search.getMaxRent());
        }

        return searchCriteria.toString();
    }

    @Override
    public void onClick(View v) {
//        Log.d("SavedSearchHolder", "Pager activity starting");
//        Intent intent = PostedPropertyPagerActivity.newIntent(mActivity, mSavedSearch.getId());
//        mActivity.startActivity(intent);
    }


}

