package weather.widget.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeatherDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "city_db";
    private static final String TBL_NAME = "cities";
    private static final int DB_VERSION = 1;

    private final String CMD = "CREATE TABLE IF NOT EXISTS '" + TBL_NAME + "' (" +
            "'id' LONG PRIMARY KEY NOT NULL ," +
            "'city' TEXT ," +
            "'lat' DOUBLE ," +
            "'lon' DOUBLE ," +
            "'countryCode' TEXT ," +
            "'selected' INTEGER " +
            ")";

    private String[] allColumn = {"id", "city", "lat", "lon", "countryCode", "selected"};

    public WeatherDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD);
        Log.i("db", "table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
            Log.i("db", "table drop");
            onCreate(db);
        }
    }

    public void getAll() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null && db.isOpen()) {
            //Cursor cursor = db.query(TBL_NAME, allColumn, null, null, null, null, "city");
            Cursor cursor = db.rawQuery("SELECT * FROM '"+TBL_NAME+"'",null);
            if (cursor != null)
                Log.d("all", "cursor count is: "+cursor.getCount());
            else
                Log.d("all","cursor is empty!");
        }
        db.close();
    }


    public List<WModel> searchCityByName(String name, String limit) {
        List<WModel> cityModels = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(true, TBL_NAME, allColumn, "city LIKE '" + name + "%'"
                , null, null, null, "countryCode, city", limit);
        if (cursor.moveToFirst()) {
            do {
                cityModels.add(WModel.convertCursorToCityModel(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cityModels;
    }
}
