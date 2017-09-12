package com.danielj.springadsandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.dto.CategoryDto;
import com.danielj.springadsandroid.helper.HelperUtils;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Search activity. Let´s the user search for ads using search string, category, adtype and state
 *
 * @author Daniel Johansson
 */
public class SearchActivity extends AppCompatActivity {
    /**
     * Spinner for categories
     */
    private Spinner spinnerCategory;

    /**
     * Spinner for states
     */
    private Spinner spinnerState;

    /**
     * Spinner for adtype (Sell/Buy)
     */
    private Spinner spinnerAdType;

    /**
     * EditText for search string
     */
    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Executes GetCategoriesTask for getting categories to the spinnerCategory
        new GetCategoriesTask().execute();

        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        spinnerAdType = (Spinner) findViewById(R.id.spinnerAdType);
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        //For data to the spinnerState
        String[] states = new String[]{"All", "Blekinge", "Dalarna", "Gotland", "Gävleborg", "Halland",
                "Jämtland", "Jönköping", "Kalmar", "Kronoberg", "Norrbotten", "Skåne", "Stockholm",
                "Södermanland", "Uppsala", "Värmland", "Västerbotten", "Västernorrland",
                "Västmanland", "Västra Götaland", "Örebro", "Östergötland"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(SearchActivity.this,
                android.R.layout.simple_spinner_item, states);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);

        //For data to the spinnerAdType
        String[] types = new String[]{"Sell", "Buy"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(SearchActivity.this,
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdType.setAdapter(typeAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Method is called when user clicks the btnSearch button. If search string isn´t empty,
     * GetAdsTask is executed which finds a list of ads matching the search criteria
     *
     * @param v
     */
    public void SearchAds(View v) {
        if (editTextSearch.getText().toString().equals("")) {
            Toast.makeText(this, "Search string can not be empty! Try again!", Toast.LENGTH_SHORT).show();
        } else {
            new GetAdsTask().execute(editTextSearch.getText().toString(),
                    spinnerCategory.getSelectedItem().toString(),
                    spinnerState.getSelectedItem().toString(),
                    spinnerAdType.getSelectedItem().toString());
        }
    }

    /**
     * Class for task that gets ads matching the search criteria using a HTTP Get request to
     * the REST web service
     */
    private class GetAdsTask extends AsyncTask<String, Void, ArrayList<AdDto>> {
        @Override
        protected ArrayList<AdDto> doInBackground(String... params) {
            ArrayList<AdDto> newAdList=new ArrayList<>();
            params[0] = params[0].toUpperCase();
            params[3] = params[3].toUpperCase();

            String url = HelperUtils.HOST + "search?searchString=" + params[0] + "&category=" + params[1]
                    + "&state=" + params[2] + "&type=" + params[3];
            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                AdDto[] ads = restTemplate.getForObject(url, AdDto[].class);
                List<AdDto> adList = Arrays.asList(ads);
                newAdList = new ArrayList<>(adList);

            } catch (RestClientException e) {
                Log.e("GetAdsTask", e.getMessage(), e);
            }
            return newAdList;
        }

        @Override
        protected void onPostExecute(ArrayList<AdDto> adList) {
            //If the resulting List of ads isn´t empty, start the MainActivity using the List of
            //ads as extras. Otherwise show error Toast
            if (adList.size() == 0) {
                Toast.makeText(SearchActivity.this, "No search results found! Try again!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putParcelableArrayListExtra("adList", adList);
                intent.putExtra("activity", "SearchActivity");
                startActivity(intent);
            }
        }
    }

    /**
     * Class for task that gets all categories using a HTTP Get request to the REST web service
     */
    private class GetCategoriesTask extends AsyncTask<Void, Void, List<CategoryDto>> {
        @Override
        protected List<CategoryDto> doInBackground(Void... params) {
            List<CategoryDto> categoriesList=new ArrayList<>();
            final String url = HelperUtils.HOST + "categories/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                CategoryDto[] categories = restTemplate.getForObject(url, CategoryDto[].class);
                categoriesList= Arrays.asList(categories);
            } catch (RestClientException e) {
                Log.e("GetCategoriesTask", e.getMessage(), e);
            }
            return categoriesList;
        }

        //Creates a ArrayAdapter for the categoryAdapter with the data from the task
        @Override
        protected void onPostExecute(List<CategoryDto> categoriesList) {
            if (categoriesList.size()!=0) {
                String[] stringArray = new String[categoriesList.size() + 1];
                stringArray[0] = "All";                       //First value is "All"

                int i = 1;
                for (CategoryDto categoryDto : categoriesList) {
                    stringArray[i] = categoryDto.getName();
                    i++;
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(SearchActivity.this,
                        android.R.layout.simple_spinner_item, stringArray);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            }
        }
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
}
