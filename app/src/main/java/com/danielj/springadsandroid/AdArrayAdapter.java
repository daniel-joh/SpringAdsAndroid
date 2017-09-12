package com.danielj.springadsandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danielj.springadsandroid.dto.AdDto;

import java.util.List;

/**
 * ArrayAdapter class for the listview in MainActivity
 *
 * @author Daniel Johansson
 */
public class AdArrayAdapter extends ArrayAdapter<AdDto> {

    public AdArrayAdapter(Context context, List<AdDto> ads) {
        super(context, 0, ads);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Gets the ad for this position
        AdDto ad = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ad_item, parent, false);
        }

        TextView textviewAdHeadline = (TextView) convertView.findViewById(R.id.textviewAdHeadline);
        TextView textviewAdPrice = (TextView) convertView.findViewById(R.id.textviewAdPrice);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        TextView textviewAdDateCreated = (TextView) convertView.findViewById(R.id.textviewAdDateCreated);
        TextView textviewAdCity = (TextView) convertView.findViewById(R.id.textviewAdCity);

        //Sets the text in the textviews
        textviewAdHeadline.setText(ad.getHeadline());
        textviewAdPrice.setText(Long.toString(ad.getPrice()) + " kr");
        textviewAdCity.setText(ad.getCity());
        String dateCreated = ad.getDateCreated().substring(0, 10);
        textviewAdDateCreated.setText(dateCreated);

        //Decodes the byte array of the image and sets the bitmap to the ImageView
        if (ad.getImage() != null) {
            byte[] byteArray = ad.getImage();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

            //Sets the Bitmap data to the ImageView
            imageView.setImageBitmap(bmp);
        }

        return convertView;
    }
}

