package com.android.shelter;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;

import com.android.shelter.util.ShelterPropertyTask;

import java.util.List;

/**
 * Independent activity added which will be used for showing when user
 * clicks on search on {@link HomeActivity}
 * Created by rishi on 5/1/16.
 */
public class SearchActivity extends AppCompatActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {

    private static final String TAG = "SearchActivity";
    private RecyclerView mRecyclerView;
    private PropertyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Property> properties;
    private String searchLocation;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.property_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        properties = PropertyLab.get(getBaseContext()).getProperties();
        mAdapter = new PropertyAdapter(properties);
        mRecyclerView.setAdapter(mAdapter);

        ((AppCompatActivity)this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)this).getSupportActionBar().setDisplayShowHomeEnabled(true);

//        ((AppCompatActivity)this).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_search_menu, menu);
        MenuItem menuItem =menu.findItem(R.id.search_bar);
        searchView =(SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        new ShelterPropertyTask(getApplicationContext(),"all",
                false,null,null,null,null,null,null,null,null).execute();

        properties = PropertyLab.get(getBaseContext()).getProperties();
        mAdapter = new PropertyAdapter(properties);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.invalidate();

        searchView.setIconified(true);
        searchView.setIconified(true);
        searchView.clearFocus();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
