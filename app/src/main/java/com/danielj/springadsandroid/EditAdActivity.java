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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.danielj.springadsandroid.helper.HelperUtils;
import com.danielj.springadsandroid.dto.AdDto;
import com.danielj.springadsandroid.dto.CategoryDto;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity for editing an ad
 *
 * @author Daniel Johansson
 */
public class EditAdActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Bitmap
     */
    private Bitmap bitmap;

    /**
     * Button for updating ad
     */
    private Button btnUpdateAd;

    /**
     * Button for changing image
     */
    private Button btnChangeImage;

    /**
     * EditText for headline of ad
     */
    private EditText editTextHeadline;

    /**
     * EditText for description of ad
     */
    private EditText editTextDescription;

    /**
     * EditText for price of ad
     */
    private EditText editTextPrice;

    /**
     * Spinner for category
     */
    private Spinner spinnerCategory;

    /**
     * Spinner for adtype
     */
    private Spinner spinnerType;

    /**
     * ImageView for image ad
     */
    private ImageView imageViewAd;

    /**
     * AdDto
     */
    private AdDto ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ad);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditAd);
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
        btnUpdateAd = (Button) findViewById(R.id.btnUpdateAd);
        btnChangeImage = (Button) findViewById(R.id.btnChangeImage);
        btnUpdateAd.setOnClickListener(this);
        btnChangeImage.setOnClickListener(this);

        //Handles if user is not signed in
        HelperUtils.handleSignIn(this);

        ad = (AdDto) getIntent().getParcelableExtra("ad");

        //Sets up spinnerType
        String[] types = new String[]{"Sell", "Buy"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(EditAdActivity.this,
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setPrompt("Select type");
        int position = typeAdapter.getPosition(ad.getType());
        spinnerType.setSelection(position);

        //Sets text in EditText´s to values in Ad object
        editTextHeadline.setText(ad.getHeadline());
        editTextDescription.setText(ad.getDescription());
        editTextPrice.setText(Long.toString(ad.getPrice()));

        //Sets image in Ad object to ImageView
        if (ad.getImage() != null) {
            byte[] byteArray = ad.getImage();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            //Sets the Bitmap data to the ImageView
            imageViewAd.setImageBitmap(bitmap);
        }

        //Executes GetCategoriesTask for getting categories
        new GetCategoriesTask().execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateAd:
                updateAd();
                break;
            case R.id.btnChangeImage:
                changeImage();
                break;
        }
    }

    /**
     * Updates ad with values from EditText´s and other components
     */
    public void updateAd() {
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

        ad.setHeadline(editTextHeadline.getText().toString());
        ad.setDescription(editTextDescription.getText().toString());
        ad.setPrice(Long.parseLong(editTextPrice.getText().toString()));
        ad.setCategoryName(spinnerCategory.getSelectedItem().toString());
        ad.setType(spinnerType.getSelectedItem().toString());

        //Converts the image to byte[] and sets the image in Ad object if not null
        byte[] byteArray = convertImageToByteArray();
        if (byteArray != null)
            ad.setImage(byteArray);

        ad.setUserId(HelperUtils.getSignedInUser().getId());

        //Executes UpdateAdTask to update the ad
        new UpdateAdTask().execute(ad);
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
     * Starts activity for user to pick an image to change image
     */
    public void changeImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
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
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                        //Sets the picked image to the ImageView
                        imageViewAd.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("ImagePickingActivity", e.getMessage());
                    }
                    imageViewAd.setVisibility(View.VISIBLE);
                    break;
            }
    }

    /**
     * Class for task that updates the ad using a HTTP Put request to the REST web service
     */
    private class UpdateAdTask extends AsyncTask<AdDto, Void, Void> {
        @Override
        protected Void doInBackground(AdDto... params) {
            final String url = HelperUtils.HOST + "ads/" + params[0].getId();

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                restTemplate.put(url, params[0]);
            } catch (RestClientException e) {
                Log.d("UpdateAdTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(EditAdActivity.this, "Ad updated", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditAdActivity.this, UserMenuActivity.class);
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
                Log.e("GetTaskCategoriesTask", e.getMessage(), e);
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
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(EditAdActivity.this,
                        android.R.layout.simple_spinner_item, stringArray);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);

                int position = categoryAdapter.getPosition(ad.getCategoryName());
                spinnerCategory.setSelection(position);
                spinnerCategory.setPrompt("Select category");
            }
        }
    }
}

