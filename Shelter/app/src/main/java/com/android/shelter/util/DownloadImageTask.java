package com.android.shelter.util;

/**
 * Created by Prasanna on 5/13/16.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try {
            InputStream is = new URL(urlOfImage).openStream();
            logo = BitmapFactory.decodeStream(is);
        } catch (Exception e) { // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    protected void onPostExecute(Bitmap result){
        Log.d("setting image"," now");
        imageView.setImageBitmap(ImagePicker.scaleToFitWidth(result, imageView.getWidth()));
    }

}
