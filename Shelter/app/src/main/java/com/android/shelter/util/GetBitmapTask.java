package com.android.shelter.util;

/**
 * Created by Prasanna on 5/13/16.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.shelter.DownloadImageCallback;
import com.android.shelter.R;

import java.io.InputStream;
import java.net.URL;

public class GetBitmapTask extends AsyncTask<String,Void,Bitmap> {
    private DownloadImageCallback mDownloadImageCallback;

    public GetBitmapTask(DownloadImageCallback downloadImageCallback) {
        this.mDownloadImageCallback = downloadImageCallback;
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
        mDownloadImageCallback.onTaskDone(result);

    }

}
