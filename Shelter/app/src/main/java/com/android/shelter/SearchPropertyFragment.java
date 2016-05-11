package com.android.shelter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.shelter.helper.MyPostingAdapter;
import com.android.shelter.util.ShelterPropertyTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPropertyFragment extends Fragment {
    private static final String TAG = "SearchPropertyFragment";
    private static final String DIALOG_FILTER = "DialogFilter";
    private static final int REQUEST_FILTER_OPTION = 0;
    private SearchPropertyFilterCriteria criteria;
    private RecyclerView mPostingRecyclerView;


    Button btnFilter;

    public SearchPropertyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_property, container, false);

        btnFilter = (Button)rootView.findViewById(R.id.filter_btn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchPropertyFilterFragment dialog = SearchPropertyFilterFragment
                        .newInstance();
                dialog.setTargetFragment(SearchPropertyFragment.this, REQUEST_FILTER_OPTION);

                dialog.show(fragmentManager, DIALOG_FILTER);

            }
        });

        mPostingRecyclerView = (RecyclerView) rootView.findViewById(R.id.property_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mPostingRecyclerView.setLayoutManager(layoutManager);
        setupAdapter();
        return rootView;

    }
    @Override
    public void onResume() {
        super.onResume();
        setupAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILTER_OPTION){
            if(resultCode == Activity.RESULT_OK){
                criteria = (SearchPropertyFilterCriteria) data.getSerializableExtra(SearchPropertyFilterFragment.EXTRA_OPTION);
                new ShelterPropertyTask(getActivity().getApplicationContext(),"postings",true,
                        null,null,criteria.getKeyword(),criteria.getCity(),criteria.getZipcode(),
                        criteria.getMinRent(),criteria.getMaxRent(),criteria.getApartmentType()).execute();

            }else if(resultCode == Activity.RESULT_CANCELED){
//                mSortByOption = (int) data.getSerializableExtra(FilterResultFragment.EXTRA_OPTION);
            }
        }
    }

    private void setupAdapter() {
        if (isAdded()) {
            Log.d(TAG, "Setting adapter for view");

            mPostingRecyclerView.setAdapter(new MyPostingAdapter(PropertyLab.get(getContext()).getProperties(), getActivity(), getActivity().getSupportFragmentManager()));
        }
    }
}
