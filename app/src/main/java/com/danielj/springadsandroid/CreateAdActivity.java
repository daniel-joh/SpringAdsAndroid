package com.danielj.springadsandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.dto.CategoryDto;
import com.danielj.springadsandroid.helper.HelperUtils;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity that handles creating an ad
 *
 * @author Daniel Johansson
 */
public class CreateAdActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Boolean for if image is added or not
     */
    private boolean imageAdded = false;

    /**
     * Bitmap
     */
    private Bitmap bitmap;

    /**
     * Button for save ad
     */
    private Button btnSaveAd;

    /**
     * Button for adding image
     */
    private Button btnAddImage;

    /**
     * EditText for headline of ad
     */
    private EditText editTextHeadline;

    /**
     * EditText for description of ad
     */
    private EditText editTextDescription;

    /**
     * EditText for price of ad item
     */
    private EditText editTextPrice;

    /**
     * Spinner for categories
     */
    private Spinner spinnerCategory;

    /**
     * Spinner for adtype
     */
    private Spinner spinnerType;

    /**
     * ImageView for ad image
     */
    private ImageView imageViewAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ad);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreateAd);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextHeadline = (EditText) findViewById(R.id.editTextHeadline);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        imageViewAd = (ImageView) findViewById(R.id.imageView);
        btnSaveAd = (Button) findViewById(R.id.btnSaveAd);
        btnAddImage = (Button) findViewById(R.id.btnAddImage);
        btnSaveAd.setOnClickListener(this);
        btnAddImage.setOnClickListener(this);

        //Executes GetCategoriesTask for getting categories
        new GetCategoriesTask().execute();

        spinnerCategory.setPrompt("Select category");

        //For spinnerType
        String[] types = new String[]{"Sell", "Buy"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(CreateAdActivity.this,
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setPrompt("Select type");

        imageViewAd.setVisibility(View.GONE);

        //Handles if user is not signed in
        HelperUtils.handleSignIn(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveAd:
                saveAd();
                break;
            case R.id.btnAddImage:
                addImage();
                break;
        }
    }

    /**
     * Saves the ad
     */
    public void saveAd() {
        if (editTextHeadline.getText().toString().equals("")) {
            Toast.makeText(this, "Headline cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextDescription.getText().toString().equals("")) {
            Toast.makeText(this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editTextPrice.getText().toString().equals("")) {
            Toast.makeText(this, "Price cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Creates the AdDto object with values from EditText´s etc
        AdDto ad = new AdDto();
        ad.setHeadline(editTextHeadline.getText().toString());
        ad.setDescription(editTextDescription.getText().toString());
        ad.setPrice(Long.parseLong(editTextPrice.getText().toString()));
        ad.setCategoryName(spinnerCategory.getSelectedItem().toString());
        ad.setType(spinnerType.getSelectedItem().toString());

        //If image has been added, convert it to byte[] and set it to ad object
        if (imageAdded) {
            byte[] byteArray = convertImageToByteArray();
            if (byteArray != null)
                ad.setImage(byteArray);
        }
        //If image hasnt been added, add a image for non-image ads instead
        else {
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.notavailable);
            byte[] byteArray = convertImageToByteArray();
            if (byteArray != null)
                ad.setImage(byteArray);
        }

        ad.setUserId(HelperUtils.getSignedInUser().getId());

        //Executes PostAdTask to create the ad
        new PostAdTask().execute(ad);
    }

    /**
     * Converts bitmap to byte array
     *
     * @return byte array
     */
    public byte[] convertImageToByteArray() {
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayBitmapStream);
        return byteArrayBitmapStream.toByteArray();
    }

    /**
     * Starts activity for user to pick an image to add, if user hasn´t already chosen an image
     */
    public void addImage() {
        if (!imageAdded) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        } else {
            imageViewAd.setImageBitmap(null);
            btnAddImage.setText("Add Image");
            imageAdded = false;
            imageViewAd.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                //For image picking result
                case 1:
                    Uri selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                selectedImage);
                        //Sets the picked image to the ImageView
                        imageViewAd.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("ImagePickingActivity", e.getMessage());
                    }
                    imageViewAd.setVisibility(View.VISIBLE);
                    btnAddImage.setText("Remove Image");
                    imageAdded = true;
                    break;
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

    /**
     * Class for task that creates a new ad using a HTTP Post request to the REST web service
     */
    private class PostAdTask extends AsyncTask<AdDto, Void, AdDto> {
        @Override
        protected AdDto doInBackground(AdDto... params) {
            AdDto response = new AdDto();
            final String url = HelperUtils.HOST + "users/" + params[0].getUserId() + "/ads/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                response = restTemplate.postForObject(url, params[0], AdDto.class);
            } catch (RestClientException e) {
                Log.d("PostAdTask", e.getMessage(), e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(AdDto result) {
            Toast.makeText(CreateAdActivity.this, "Ad created!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateAdActivity.this, UserMenuActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Class for task that gets all categories using a HTTP Get request to the REST web service
     */
    private class GetCategoriesTask extends AsyncTask<Void, Void, List<CategoryDto>> {
        @Override
        protected List<CategoryDto> doInBackground(Void... params) {
            List<CategoryDto> categoriesList = new ArrayList<>();
            final String url = HelperUtils.HOST + "categories/";

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                CategoryDto[] categories = restTemplate.getForObject(url, CategoryDto[].class);
                categoriesList = Arrays.asList(categories);
            } catch (RestClientException e) {
                Log.e("GetCategoriesTask", e.getMessage(), e);
            }
            return categoriesList;
        }

        //Creates a ArrayAdapter for the categoryAdapter with the data from the task
        @Override
        protected void onPostExecute(List<CategoryDto> categoriesList) {
            if (categoriesList.size() != 0) {
                String[] stringArray = new String[categoriesList.size()];

                int i = 0;
                for (CategoryDto categoryDto : categoriesList) {
                    stringArray[i] = categoryDto.getName();
                    i++;
                }
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(CreateAdActivity.this,
                        android.R.layout.simple_spinner_item, stringArray);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            }
        }
    }

}
