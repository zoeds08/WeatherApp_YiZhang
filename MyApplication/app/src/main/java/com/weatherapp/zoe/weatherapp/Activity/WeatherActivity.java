package com.weatherapp.zoe.weatherapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherapp.zoe.weatherapp.AsyncTask.GetLocationDataAsyncTask;
import com.weatherapp.zoe.weatherapp.AsyncTask.GetWeatherDataAsyncTask;
import com.weatherapp.zoe.weatherapp.AsyncTask.GetWeatherDataParams;
import com.weatherapp.zoe.weatherapp.R;
import com.weatherapp.zoe.weatherapp.Util.FromJSON;
import com.weatherapp.zoe.weatherapp.adapter.WeatherAdapter;
import com.weatherapp.zoe.weatherapp.model.AllWeather;
import com.weatherapp.zoe.weatherapp.model.Location;
import com.weatherapp.zoe.weatherapp.model.Weather;
import com.weatherapp.zoe.weatherapp.sensor.LocationFinder;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.weatherapp.zoe.weatherapp.R.string.lat;

public class WeatherActivity extends AppCompatActivity implements LocationFinder.LocationDetector{

    private final String TAG = "WeatherActivity";
    private TextView cityName;
    private TextView coord;
    private ListView mListView;

    private ArrayList<String> items;

    private android.location.Location mLocation = null;
    private LocationFinder locationFinder;

    String zipcode;
    int forecastDate;
    boolean tempUnit;
    boolean type;

    GetLocationDataAsyncTask locationDataAsyncTask;
    GetWeatherDataAsyncTask weatherDataAsyncTask;

    FromJSON fromJSON;
    String data = null;
    ArrayList<String> listWeather;
    ArrayList<Bitmap> listImage;

    ProgressDialog detectProgressDialog;

    AlertDialog.Builder noFAlertBuilder;
    AlertDialog noFAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //indeterminate dialog;
        detectProgressDialog = new ProgressDialog(WeatherActivity.this);
        detectProgressDialog.setTitle(getString(R.string.indetitle));
        detectProgressDialog.setMessage(getString(R.string.indemessage));
        detectProgressDialog.show();

        locationDataAsyncTask = new GetLocationDataAsyncTask(this);

        //R.string for weather description(txt); no hard code;
        items = new ArrayList<>();
        items.add(getString(R.string.temp));
        items.add(getString(R.string.des));
        items.add(getString(R.string.windspeed));
        items.add(getString(R.string.humidity));
        items.add(getString(R.string.pressure));



        cityName = (TextView) findViewById(R.id.cityText);
        coord = (TextView) findViewById(R.id.coord);

        //Get info from the SettingsActivity;
        SharedPreferences settings = getSharedPreferences("PrefFile", 0);
        zipcode = settings.getString("zipcode", "22202");
        forecastDate = settings.getInt("forecastDate", 3);
        tempUnit = settings.getBoolean("tempUnit", true);
        type = settings.getBoolean("type",false);

        //location detector;
        locationFinder = new LocationFinder(this, this);
        locationFinder.detectLocation();

        //Json parse;
        fromJSON = new FromJSON();
        //ListView
        listWeather = new ArrayList<>();
        listImage = new ArrayList<>();

        //zipCode method
        if(type==true) {
            detectProgressDialog.cancel();
            try {
                data = locationDataAsyncTask.execute(zipcode).get();
                Location location = fromJSON.getLocation(data);
                cityName.setText(location.getCity() + "," + location.getCountry());
                coord.setText(getString(R.string.coord) + "\n" + getString(lat) + location.getLatitude() + "," + getString(R.string.lon) + location.getLongtitude());
                listWeather = fromJSON.getForecastWeatherFromZipCode(items, zipcode, forecastDate, tempUnit);
                listImage = fromJSON.getImageViewFromZipCode(zipcode, forecastDate);

                mListView = (ListView) findViewById(R.id.weather_list_view);
                ArrayList<AllWeather> allWeatherList = new ArrayList<AllWeather>();
                WeatherAdapter adapter = new WeatherAdapter(getBaseContext(),listWeather,listImage);
                mListView.setAdapter(adapter);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

        //location detect;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET
                    },10);
                    return;
                }else{
                    configureButton() ;
                }

            }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton() ;
                return;
        }
    }

    private void configureButton() {

          if(type==false)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                }
                locationFinder.getLocationManager().requestLocationUpdates("gps", 10000, 0, locationFinder);
            }


    //Menu(settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settingsId) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);

            //indeterminate dialog;
            ProgressDialog progressDialog = new ProgressDialog(WeatherActivity.this);
            progressDialog.setTitle(getString(R.string.indetitle));
            progressDialog.setMessage(getString(R.string.indemessage));
            progressDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void locationFound(android.location.Location location) {
        Log.d(TAG,"location found");
        mLocation = location;

        if(noFAlertDialog!=null){
            noFAlertDialog.cancel();
        }

        //GPS method;
        weatherDataAsyncTask = new GetWeatherDataAsyncTask(this);

        if (mLocation != null && type==false) {

            coord.setText(getString(R.string.coord) + "\n" + getString(lat) + mLocation.getLatitude()
                    + ","+ getString(R.string.lon) + mLocation.getLongitude());

            //use GPS method;
            Double lat = mLocation.getLatitude();
            Double lon = mLocation.getLongitude();
            try {
                data = weatherDataAsyncTask.execute(new GetWeatherDataParams(lat,lon)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Weather weather = fromJSON.getForecastWeather(data,1);
            cityName.setText(weather.getLocation().getCity() + "," + weather.getLocation().getCountry());
            listWeather = fromJSON.getForecastWeatherFromCoord(items, lat,lon, forecastDate, tempUnit);
            listImage = fromJSON.getImageViewFromCoord(lat,lon,forecastDate);

            mListView = (ListView) findViewById(R.id.weather_list_view);
            ArrayList<AllWeather> allWeatherList = new ArrayList<AllWeather>();
            WeatherAdapter adapter = new WeatherAdapter(getBaseContext(),listWeather,listImage);
            mListView.setAdapter(adapter);

            if(detectProgressDialog!=null){
                detectProgressDialog.cancel();
            }


        }
    }

    @Override
    public void locationNotFound(LocationFinder.FailureReason failureReason) {
        Log.d(TAG,"location not found");

        if(detectProgressDialog!=null){
            detectProgressDialog.cancel();
        }

        //Alert dialog;
        if(type==false && mLocation==null){
            noFAlertBuilder = new AlertDialog.Builder(WeatherActivity.this);
            noFAlertBuilder.setTitle(getString(R.string.noftitle));
            noFAlertBuilder.setMessage(getString(R.string.nofmessage) + " " + failureReason);

            noFAlertBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Toast.makeText(WeatherActivity.this,getString(R.string.zipclick),Toast.LENGTH_LONG).show();
                }
            });

        noFAlertDialog = noFAlertBuilder.create();
        noFAlertDialog.show();

        }

    }
}
