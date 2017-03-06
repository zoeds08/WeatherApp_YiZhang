package com.weatherapp.zoe.weatherapp.AsyncTask;

/**
 * Created by Zoe on 16/10/25.
 */
//because the weather data{lat&lon} not a single param;
public class GetWeatherDataParams {
    double lat;
    double lon;

    public GetWeatherDataParams(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }
}
