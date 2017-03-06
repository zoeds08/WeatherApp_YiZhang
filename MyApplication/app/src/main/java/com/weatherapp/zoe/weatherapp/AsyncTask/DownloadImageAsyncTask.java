package com.weatherapp.zoe.weatherapp.AsyncTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.weatherapp.zoe.weatherapp.Util.HttpConnection;

/**
 * Created by Zoe on 16/10/25.
 */

public class DownloadImageAsyncTask extends AsyncTask<String,Void,Bitmap> {

    private Context mContext;
    private ImageDownloadedCompletionListener mCompletionListener;

    public interface ImageDownloadedCompletionListener{
        void imageDownloaded(Bitmap bitmap);
        void imageFailToDownload();
    }

    public DownloadImageAsyncTask(Context context){
        mContext = context;
    }

    public void setCompletionListener(ImageDownloadedCompletionListener completionListener){
        mCompletionListener = completionListener;
    }

    @Override
    protected Bitmap doInBackground(String... code) {
        return HttpConnection.downloadImage(code[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(mCompletionListener!=null) {
            if (bitmap != null) {
                mCompletionListener.imageDownloaded(bitmap);
            } else {
                mCompletionListener.imageFailToDownload();
            }
        }
    }
}
