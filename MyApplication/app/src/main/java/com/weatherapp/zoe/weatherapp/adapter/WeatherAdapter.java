package com.weatherapp.zoe.weatherapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weatherapp.zoe.weatherapp.R;
import com.weatherapp.zoe.weatherapp.model.AllWeather;

import java.util.ArrayList;

/**
 * Created by Zoe on 16/10/18.
 */

public class WeatherAdapter extends BaseAdapter {

    private ImageView iconView;
    private TextView dayWeather;

    Context mContext;
    LayoutInflater mInflater;

    ArrayList<AllWeather> allWeatherList = new ArrayList<>();

    //all weather is composed of weather description(txt) & image;
    public WeatherAdapter(Context context,ArrayList<String> listWeather, ArrayList<Bitmap> listImage){
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int length = listWeather.size();//days
        for(int i =0;i<length;i++){
            allWeatherList.add(i,new AllWeather(listWeather.get(i),listImage.get(i)));
        }
    }

    @Override
    public int getCount() {
        return allWeatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return allWeatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AllWeather allWeather = allWeatherList.get(position);

        if(convertView==null){
            convertView=mInflater.inflate(R.layout.list_item_weather, parent, false);
        }


        iconView = (ImageView) convertView.findViewById(R.id.thumbnailIcon);
        dayWeather = (TextView) convertView.findViewById(R.id.eachText);

        iconView.setImageBitmap(allWeather.getDayImage());
        dayWeather.setText(allWeather.getDayWeather());

        return convertView;
    }
}
