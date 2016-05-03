package com.android.shelter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Independent activity added which will be used for showing when user
 * clicks on search on {@link HomeActivity}
 * Created by rishi on 5/1/16.
 */
public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText searchBox = (EditText) findViewById(R.id.search_bar);

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "Search clicked");
                //TODO Add code to search the query and start the other activity
                return false;
            }
        });

        ImageView searchBack = (ImageView) findViewById(R.id.search_back);
        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Back clicked");
                // FIXME Must be better way to do this
                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeActivity);
            }
        });

        ImageView searchIcon = (ImageView) findViewById(R.id.search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Search icon clicked");
                //TODO Add code to search the query and start the other activity
            }
        });


    }
}
