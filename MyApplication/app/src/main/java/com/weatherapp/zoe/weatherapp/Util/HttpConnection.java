package com.weatherapp.zoe.weatherapp.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zoe on 16/10/17.
 */

public class HttpConnection {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String APP_ID = "&appid=09c3019b94dad1fc67a42f8260cd6251";
    public static final String ICON_URL = "http://openweathermap.org/img/w/";
    public static final String FORECAST_URL ="http://api.openweathermap.org/data/2.5/forecast?";


    //the zipcode needs to be converted to the lat&lon
    public static String getLocationData(String zipCode) {


        HttpURLConnection connection = null;
        InputStream inputStream = null;


        try {

            connection = (HttpURLConnection) (new URL(BASE_URL + "zip=" + zipCode + ",us" + "&units=metric" + APP_ID)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //lat&lon to get the JSON object data
    public static String getWeatherData(double lat, double lon) {

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) (new URL(FORECAST_URL + "lat=" + lat + "&lon=" + lon + "&units=metric" + APP_ID)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            //read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            while((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();
            connection.disconnect();

            return stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //icon
    public static Bitmap downloadImage(String code) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) (new URL(ICON_URL + code + ".png")).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();

            inputStream = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }
        return null;
    }
}
