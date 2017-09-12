package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.danielj.springadsandroid.helper.HelperUtils;
import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.dto.UserDto;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ViewUserAdsActivity. Let´s the user view ads created by the user
 *
 * @author Daniel Johansson
 */
public class ViewUserAdsActivity extends AppCompatActivity implements
        DeleteAdDialog.DeleteAdDialogListener {
    /**
     * ListView showing ads
     */
    private ListView listViewUserAds;

    /**
     * AdArrayAdapter for user
     */
    private UserAdArrayAdapter adAdapter;

    /**
     * List of AdDto´s
     */
    private List<AdDto> ads = new ArrayList<>();

    /**
     * Id of ad
     */
    private Long adId;

    /**
     * AdDto
     */
    private AdDto ad;

    /**
     * Boolean for deleted ad status
     */
    private boolean deleteAdFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_ads);

        //Executes GetUserAdsTask with the signed in user. Gets the ads for the user
        new GetUserAdsTask().execute(HelperUtils.getSignedInUser());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewUserAds);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listViewUserAds = (ListView) findViewById(R.id.listViewUserAds);
        registerForContextMenu(listViewUserAds);

        //Listener for the listview´s itemclicks
        listViewUserAds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                AdDto ad = (AdDto) adapter.getItemAtPosition(position);

                //Starts the activity ViewAdDetailsActivity with the clicked ad as extra
                Intent intent = new Intent(ViewUserAdsActivity.this, ViewAdDetailsActivity.class);
                intent.putExtra("ad", ad);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    /**
     * Handles the Contextmenu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        adId = ads.get(listPosition).getId();
        ad = ads.get(listPosition);

        //If user clicked delete, show DeleteAdDialog
        if (item.getItemId() == R.id.delete) {
            DialogFragment dialog = new DeleteAdDialog();
            dialog.show(getSupportFragmentManager(), "DeleteAdDialog");
        }

        //If user clicked edit, start EditAdActivity for editing the specific ad
        if (item.getItemId() == R.id.edit) {
            Intent intent = new Intent(ViewUserAdsActivity.this, EditAdActivity.class);
            intent.putExtra("ad", ad);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewUserAds) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    /*
     * Handles a positive click on the DeleteAdDialog. Deletes the selected ad,
     * updates data in the adapter and notifies that the data has changed
     *
     * @param dialog DeleteAdDialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteAdFlag = true;

        new DeleteUserAdTask().execute(adId);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /**
     * Class for task that gets all ads for a specific user, using a HTTP Get request to
     * the REST web service
     */
    private class GetUserAdsTask extends AsyncTask<UserDto, Void, List<AdDto>> {
        @Override
        protected List<AdDto> doInBackground(UserDto... params) {
            final String url = HelperUtils.HOST + "users/" + params[0].getId() + "/ads/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                AdDto[] ads = restTemplate.getForObject(url, AdDto[].class);

                //Since Arrays.asList result is immutable, a new ArrayList has to be created
                List<AdDto> tempList = Arrays.asList(ads);
                List<AdDto> adList = new ArrayList<AdDto>(tempList);
                return adList;
            } catch (RestClientException e) {
                Log.e("GetUserAdsTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<AdDto> adList) {
            //If the delete button has been clicked in the Dialog, clear data in the adapter and notify
            if (deleteAdFlag == true) {
                ViewUserAdsActivity.this.ads = adList;

                //Updates data in the adapter
                adAdapter.clear();
                adAdapter.addAll(ads);

                //Notifies that the data set has changed to refresh the listview
                adAdapter.notifyDataSetChanged();

                deleteAdFlag = false;
            } else {
                ViewUserAdsActivity.this.ads = adList;
                adAdapter = new UserAdArrayAdapter(ViewUserAdsActivity.this, ads);

                //Attaches adapter to the listview
                listViewUserAds.setAdapter(adAdapter);
            }
        }
    }

    /**
     * Class for task that deletes the ad using a HTTP Delete request to the REST web service
     */
    private class DeleteUserAdTask extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            final String url = HelperUtils.HOST + "ads/" + params[0];

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.delete(url);
            } catch (RestClientException e) {
                Log.e("DeleteUserAdTask", e.getMessage(), e);
                deleteAdFlag = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(ViewUserAdsActivity.this, "Ad deleted!", Toast.LENGTH_SHORT).show();

            //Executes GetUserAdsTask to get all ads to refresh data after the delete
            new GetUserAdsTask().execute(HelperUtils.getSignedInUser());
        }
    }
}
