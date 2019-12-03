package weather.widget;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import weather.widget.database.CopyDB;
import weather.widget.database.WeatherDB;

public class App extends Application {
    public static final String URL = "http://api.openweathermap.org/data/2.5/weather?id=115019&units=metric&APPID=64caa635b6ffe0cbbc6a48eedef151d2";
    public static RequestQueue queue;
    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);

        new CopyDB(this).checkingExistDB();
    }



}
