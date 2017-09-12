package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.helper.HelperUtils;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main activity class. Handles viewing ads
 *
 * @author Daniel Johansson
 */
public class MainActivity extends AppCompatActivity {
    /**
     * ArrayAdapter for ads
     */
    private AdArrayAdapter adAdapter;

    /**
     * List of AdDto´s
     */
    private List<AdDto> ads = new ArrayList<>();

    /**
     * ListView
     */
    private ListView listviewAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewAds);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        listviewAds = (ListView) findViewById(R.id.list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Starts CreateAdActivity if the user is signed in
                if (HelperUtils.isSignedIn()) {
                    Intent intent = new Intent(MainActivity.this, CreateAdActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, SignInInitialActivity.class);
                    startActivity(intent);
                }
            }
        });

        //Listener for the listview´s itemclicks
        listviewAds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                //Gets the ad that was clicked on
                AdDto ad = (AdDto) adapter.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, ViewAdDetailsActivity.class);
                intent.putExtra("ad", ad);
                startActivity(intent);
            }
        });

        handleIntent();
    }

    /**
     * Handles intent (from SearchActivity). If a search previously has been started from
     * SearchActivity, call method updateArrayAdapter() to update arrayadapter (and listview)
     * with new data. Otherwise execute GetAllAdsTask to load ads into listview
     */
    public void handleIntent() {
        Intent intent = getIntent();
        String activity = "";

        if (intent != null) {
            activity = intent.getStringExtra("activity");

            if (activity != null) {
                if (activity.equals("SearchActivity")) {
                    updateArrayAdapter();
                }
            } else {
                new GetAllAdsTask().execute();
            }
        } else {
            new GetAllAdsTask().execute();
        }
    }

    /**
     * Updates the arrayadapter with new data from SearchActivity
     */
    public void updateArrayAdapter() {
        ArrayList<AdDto> arrayAdList = getIntent().getParcelableArrayListExtra("adList");
        ads.addAll(arrayAdList);

        if (adAdapter == null) {
            adAdapter = new AdArrayAdapter(this, ads);
            listviewAds.setAdapter(adAdapter);
        }
    }

    /**
     * Class for task that gets all ads using a HTTP Get request to the REST web service
     */
    private class GetAllAdsTask extends AsyncTask<Void, Void, List<AdDto>> {
        @Override
        protected List<AdDto> doInBackground(Void... params) {
            List<AdDto> adList=new ArrayList<>();
            final String url = HelperUtils.HOST + "ads/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                AdDto[] ads = restTemplate.getForObject(url, AdDto[].class);
                adList = Arrays.asList(ads);
            } catch (RestClientException e) {
                Log.d("GetAllAdsTask", e.getMessage(), e);
            }
            return adList;
        }

        @Override
        protected void onPostExecute(List<AdDto> adList) {
            MainActivity.this.handleAdapter(adList);
        }
    }

    /**
     * Creates the AdArrayAdapter using the List of AdDto´s from the GetAllAdsTask
     *
     * @param ads
     */
    public void handleAdapter(List<AdDto> ads) {
        this.ads = ads;
        adAdapter = new AdArrayAdapter(this, ads);

        //Attaches adapter to the listview
        listviewAds.setAdapter(adAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Starts UserMenuActivity
        if (id == R.id.action_user_profile) {
            Intent intent = new Intent(this, UserMenuActivity.class);
            startActivity(intent);
        }

        //Starts SearchActivity
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }

        //Starts SignInInitialActivity
        if (id == R.id.action_sign_in) {
            if (!HelperUtils.isSignedIn()) {
                Intent intent = new Intent(this, SignInInitialActivity.class);
                startActivity(intent);
            }
        }

        //Signs out
        if (id == R.id.action_sign_out) {
            if (HelperUtils.isSignedIn()) {
                HelperUtils.setIsSignedIn(false);
                HelperUtils.setSignedInUser(null);
                this.recreate();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_ads, menu);

        //Change visibility of sign in/out buttons depending on user signin status
        if (HelperUtils.isSignedIn()) {
            menu.findItem(R.id.action_sign_in).setVisible(false);
            menu.findItem(R.id.action_sign_out).setVisible(true);
        } else {
            menu.findItem(R.id.action_sign_in).setVisible(true);
            menu.findItem(R.id.action_sign_out).setVisible(false);
        }

        return true;
    }
}


