package com.weatherapp.zoe.weatherapp.Util;

import android.graphics.Bitmap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weatherapp.zoe.weatherapp.Activity.WeatherActivity;
import com.weatherapp.zoe.weatherapp.AsyncTask.DownloadImageAsyncTask;
import com.weatherapp.zoe.weatherapp.AsyncTask.GetLocationDataAsyncTask;
import com.weatherapp.zoe.weatherapp.AsyncTask.GetWeatherDataAsyncTask;
import com.weatherapp.zoe.weatherapp.AsyncTask.GetWeatherDataParams;
import com.weatherapp.zoe.weatherapp.model.Location;
import com.weatherapp.zoe.weatherapp.model.Weather;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Zoe on 16/10/17.
 */

public class FromJSON {

    //if user tap the zipCode, it need to be converted to the lat&lon and forecast
    public Location getLocation(String data){

        Location location = new Location();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();

        //get the coord info
        JsonObject coordObj = jsonObject.get("coord").getAsJsonObject();
        location.setLatitude(coordObj.get("lat").getAsDouble());
        location.setLongtitude(coordObj.get("lon").getAsDouble());

        //get the city info
        location.setCity(jsonObject.get("name").getAsString());

        //get the country info
        JsonObject sysObj = jsonObject.get("sys").getAsJsonObject();
        location.setCountry(sysObj.get("country").getAsString());
        return location;
    }

    //forecast weather info
    public Weather getForecastWeather(String data, int forecastDate) {

        Weather weatherForecast = new Weather();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();


        //get the city info
        JsonObject cityObj = jsonObject.get("city").getAsJsonObject();
        weatherForecast.getLocation().setCity(" " + cityObj.get("name").getAsString());
        weatherForecast.getLocation().setCountry(" " + cityObj.get("country").getAsString());

        //get the coord info
        JsonObject coordObj = cityObj.get("coord").getAsJsonObject();
        weatherForecast.getLocation().setLatitude(coordObj.get("lat").getAsFloat());
        weatherForecast.getLocation().setLongtitude(coordObj.get("lon").getAsFloat());


        //Get the list info
        JsonArray jsonArray = jsonObject.get("list").getAsJsonArray();

        JsonObject jsonList = jsonArray.get((forecastDate-1)*8).getAsJsonObject();
        //main info
        JsonObject mainObj = jsonList.get("main").getAsJsonObject();
        weatherForecast.setTemp(mainObj.get("temp").getAsDouble());
        weatherForecast.setHumidity(mainObj.get("humidity").getAsFloat());
        weatherForecast.setPressure(mainObj.get("pressure").getAsFloat());

        //get the weatherId, main, description, icon
        JsonArray jsonWeather = jsonList.get("weather").getAsJsonArray();
        weatherForecast.setIcon(jsonWeather.get(0).getAsJsonObject().get("icon").getAsString());
        weatherForecast.setDescription(jsonWeather.get(0).getAsJsonObject().get("description").getAsString());

        //Get wind info
        JsonObject windObj = jsonList.get("wind").getAsJsonObject();
        weatherForecast.setWindSpeed(windObj.get("speed").getAsFloat());

        //Get dt_text
        weatherForecast.setDate(jsonList.get("dt_txt").getAsString());

        return weatherForecast;
    }


    //specific day's descriptions of weather;
    public ArrayList<String> getItemsText(ArrayList<String> arrayList, Weather weather, boolean tempUnit){

        ArrayList<String> itemsEach = new ArrayList<>();
        itemsEach.add(weather.getDate() + "\n");

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        double tempResult;
        if (tempUnit) {
            tempResult = weather.getTemp();
            String tempFormat = decimalFormat.format(tempResult);
            itemsEach.add(arrayList.get(0) + tempFormat+ "°C" + "\n");
        } else {
            tempResult = (weather.getTemp() * 9 / 5) + 32;
            String tempFormat = decimalFormat.format(tempResult);
            itemsEach.add(arrayList.get(0) + tempFormat+ "°F" + "\n");
        }

        itemsEach.add(arrayList.get(1) + weather.getDescription() + "\n");
        itemsEach.add(arrayList.get(2) + weather.getWindSpeed() + "m/s" + "\n");
        itemsEach.add(arrayList.get(3) + weather.getHumidity()+ "%" + "\n");
        itemsEach.add(arrayList.get(4) + weather.getPressure()+ "hpa" + "\n");

        return itemsEach;
    }



    //forecast Days' descriptions of weather;
    //zipcode method;
    public ArrayList<String> getForecastWeatherFromZipCode(ArrayList<String> arrayList, String zipCode, int forecastDate,boolean tempUnit) {

        ArrayList<String> daysWeather = new ArrayList<>();

        Location location = new Location();
        Weather weather = new Weather();
        WeatherActivity weatherActivity = new WeatherActivity();
        GetLocationDataAsyncTask locationDataAsyncTask = new GetLocationDataAsyncTask(weatherActivity.getBaseContext());
        String dataLoc = null;
        String dataWea = null;


        try {
            dataLoc = locationDataAsyncTask.execute(zipCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        location = this.getLocation(dataLoc);

        GetWeatherDataAsyncTask weatherDataAsyncTask = new GetWeatherDataAsyncTask(weatherActivity.getBaseContext());

        try {
            dataWea = weatherDataAsyncTask.execute(new GetWeatherDataParams(location.getLatitude(),location.getLongtitude())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(int i=1;i<forecastDate+1;i++){
            weather = this.getForecastWeather(dataWea, i);
            String each;
            each = this.getItemsText(arrayList,weather,tempUnit).get(0)+ this.getItemsText(arrayList,weather,tempUnit).get(1)
                    + this.getItemsText(arrayList,weather,tempUnit).get(2) +this.getItemsText(arrayList,weather,tempUnit).get(3)
                    +this.getItemsText(arrayList,weather,tempUnit).get(4) + this.getItemsText(arrayList,weather,tempUnit).get(5);

            daysWeather.add(i-1,each);
        }
        return daysWeather;
    }

    //forecast Days' descriptions of weather;
    //GPS method;
    public ArrayList<String> getForecastWeatherFromCoord(ArrayList<String> arrayList, double lat, double lon, int forecastDate,boolean tempUnit) {

        ArrayList<String> daysWeather = new ArrayList<>();

        Weather weather = new Weather();
        WeatherActivity weatherActivity = new WeatherActivity();
        String dataWea = null;

        GetWeatherDataAsyncTask weatherDataAsyncTask = new GetWeatherDataAsyncTask(weatherActivity.getBaseContext());

        try {
            dataWea = weatherDataAsyncTask.execute(new GetWeatherDataParams(lat,lon)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        for(int i=1;i<forecastDate+1;i++){
            weather = this.getForecastWeather(dataWea, i);
            String each;
            each = this.getItemsText(arrayList,weather,tempUnit).get(0)+ this.getItemsText(arrayList,weather,tempUnit).get(1)
                    + this.getItemsText(arrayList,weather,tempUnit).get(2) +this.getItemsText(arrayList,weather,tempUnit).get(3)
                    +this.getItemsText(arrayList,weather,tempUnit).get(4) + this.getItemsText(arrayList,weather,tempUnit).get(5);

            daysWeather.add(i-1,each);
        }
        return daysWeather;
    }


    //forecast Days' imageViews
    //zipcode method;
    public ArrayList<Bitmap> getImageViewFromZipCode(String zipCode, int forecastDate) {

        Bitmap bitmap = null;
        ArrayList<Bitmap> daysIcon = new ArrayList<>();

        Location location = new Location();
        Weather weather = new Weather();
        WeatherActivity weatherActivity = new WeatherActivity();
        GetLocationDataAsyncTask locationDataAsyncTask = new GetLocationDataAsyncTask(weatherActivity.getBaseContext());
        String dataLoc = null;
        String dataWea = null;


        try {
            dataLoc = locationDataAsyncTask.execute(zipCode).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        location = this.getLocation(dataLoc);

        GetWeatherDataAsyncTask weatherDataAsyncTask = new GetWeatherDataAsyncTask(weatherActivity.getBaseContext());

        try {
            dataWea = weatherDataAsyncTask.execute(new GetWeatherDataParams(location.getLatitude(),location.getLongtitude())).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        for(int i=1;i<forecastDate+1;i++){
            weather = this.getForecastWeather(dataWea, i);

            try {
                DownloadImageAsyncTask imageAsyncTask = new DownloadImageAsyncTask(weatherActivity.getBaseContext());
                bitmap=imageAsyncTask.execute(weather.getIcon()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            daysIcon.add(i-1,bitmap);
        }

        return daysIcon;
    }


    //forecast Days imageView
    //GPS method;
    public ArrayList<Bitmap> getImageViewFromCoord(double lat, double lon, int forecastDate) {

        Bitmap bitmap = null;
        ArrayList<Bitmap> daysIcon = new ArrayList<>();

        Weather weather = new Weather();
        WeatherActivity weatherActivity = new WeatherActivity();
        String dataWea = null;

        GetWeatherDataAsyncTask weatherDataAsyncTask = new GetWeatherDataAsyncTask(weatherActivity.getBaseContext());

        try {
            dataWea = weatherDataAsyncTask.execute(new GetWeatherDataParams(lat,lon)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        for(int i=1;i<forecastDate+1;i++){
            weather = this.getForecastWeather(dataWea, i);

            try {
                DownloadImageAsyncTask imageAsyncTask = new DownloadImageAsyncTask(weatherActivity.getBaseContext());
                bitmap=imageAsyncTask.execute(weather.getIcon()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            daysIcon.add(i-1,bitmap);
        }

        return daysIcon;
    }





}
