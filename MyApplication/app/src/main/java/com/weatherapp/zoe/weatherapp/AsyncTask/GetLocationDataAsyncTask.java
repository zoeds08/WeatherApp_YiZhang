package com.weatherapp.zoe.weatherapp.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.weatherapp.zoe.weatherapp.Util.HttpConnection;

/**
 * Created by Zoe on 16/10/25.
 */

public class GetLocationDataAsyncTask extends AsyncTask<String,Void,String>{

    private Context mContext;
    private GetLocationDataCompletionListener mCompletionListener;

    public interface GetLocationDataCompletionListener{
        public void locationDataGot(String data);
        public void locationDataFailedToGet();
    }

    public GetLocationDataAsyncTask(Context context){
        mContext = context;
    }

    public void setCompletionListener(GetLocationDataCompletionListener completionListener){
        mCompletionListener = completionListener;
    }

    @Override
    protected String doInBackground(String... zipcode) {
        return HttpConnection.getLocationData(zipcode[0]);
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);

        if(mCompletionListener!=null) {
            if (data != null) {
                mCompletionListener.locationDataGot(data);
            } else {
                mCompletionListener.locationDataFailedToGet();
            }
        }

    }
}
