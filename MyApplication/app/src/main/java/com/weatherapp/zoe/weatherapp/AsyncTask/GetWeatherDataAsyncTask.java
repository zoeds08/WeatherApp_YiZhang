package com.weatherapp.zoe.weatherapp.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.weatherapp.zoe.weatherapp.Util.HttpConnection;

/**
 * Created by Zoe on 16/10/25.
 */

public class GetWeatherDataAsyncTask extends AsyncTask<GetWeatherDataParams,Void,String>{

    private Context mContext;
    private GetWeatherDataCompletionListener mCompletionListener;

    public interface GetWeatherDataCompletionListener{
        public void weatherDataGot(String data);
        public void weatherDataFailedToGet();
    }

    public GetWeatherDataAsyncTask(Context context){
        mContext = context;
    }

    public void setCompletionListener(GetWeatherDataCompletionListener completionListener){
        mCompletionListener = completionListener;
    }


    @Override
    protected String doInBackground(GetWeatherDataParams... params) {
        double lat = params[0].lat;
        double lon = params[0].lon;
        return HttpConnection.getWeatherData(lat,lon);
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);

        if(mCompletionListener!=null) {
            if (data != null) {
                mCompletionListener.weatherDataGot(data);
            } else {
                mCompletionListener.weatherDataFailedToGet();
            }
        }
    }
}
