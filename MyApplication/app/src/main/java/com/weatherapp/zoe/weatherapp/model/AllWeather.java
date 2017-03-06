package com.weatherapp.zoe.weatherapp.model;

import android.graphics.Bitmap;

/**
 * Created by Zoe on 16/10/18.
 */

public class AllWeather {
    String dayWeather;
    Bitmap dayImage;

    public String getDayWeather() {
        return dayWeather;
    }

    public Bitmap getDayImage() {
        return dayImage;
    }

    public AllWeather(String dayWeather, Bitmap dayImage){
        this.dayWeather = dayWeather;
        this.dayImage = dayImage;
    }

}
