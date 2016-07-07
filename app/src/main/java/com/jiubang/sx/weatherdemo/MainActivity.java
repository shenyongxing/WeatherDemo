package com.jiubang.sx.weatherdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiubang.sx.weatherdemo.views.WeatherSunnyView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeatherSunnyView view = new WeatherSunnyView(this) ;
        setContentView(view);
    }
}
