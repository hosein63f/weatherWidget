package weather.widget.database;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class WModel {
    long id;
    double lat,lon;
    String city,countryCode;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @NonNull
    @Override
    public String toString() {
        return getCountryCode()+", "+getCity();
    }

    public static ContentValues convertInsertToCV(Long id, String city, Double lat, Double lon, String code, boolean selected){
        ContentValues cv = new ContentValues();
        cv.put("id",id);
        cv.put("city",city);
        cv.put("lat",lat);
        cv.put("lon",lon);
        cv.put("countryCode",code);
        cv.put("selected",selected?1:0);
        return cv;
    }

    public static WModel convertCursorToCityModel(Cursor cursor){
        WModel cityModel = new WModel();
        cityModel.setId(cursor.getLong(cursor.getColumnIndex("id")));
        cityModel.setCity(cursor.getString(cursor.getColumnIndex("city")));
        cityModel.setLat(cursor.getDouble(cursor.getColumnIndex("lat")));
        cityModel.setLon(cursor.getDouble(cursor.getColumnIndex("lon")));
        cityModel.setCountryCode(cursor.getString(cursor.getColumnIndex("countryCode")));
        return cityModel;
    }
}
