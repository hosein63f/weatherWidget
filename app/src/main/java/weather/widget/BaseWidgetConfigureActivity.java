package weather.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import weather.widget.database.WModel;
import weather.widget.database.WeatherDB;


public class BaseWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "weather.widget.BaseWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    WeatherDB weatherDB;
    SearchView mAppWidgetText;
    List<WModel> modelList ;
    CityInsertAdapter adapter;
    RecyclerView rv;
    TextView insert_city;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = BaseWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally

            //saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            BaseWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public BaseWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.base_widget_configure);
        mAppWidgetText = findViewById(R.id.appwidget_text);
        rv = findViewById(R.id.recycler_view);
        insert_city = findViewById(R.id.txt_selected_city);
        modelList = new ArrayList<>();
        weatherDB = new WeatherDB(this);
        mAppWidgetText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                modelList =  weatherDB.searchCityByName(newText,"5");
                adapter = new CityInsertAdapter(getApplicationContext(),modelList);

                rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rv.setAdapter(adapter);
                return false;
            }
        });

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }



        //mAppWidgetText.setText(loadTitlePref(BaseWidgetConfigureActivity.this, mAppWidgetId));
    }



    // recycler view adapter
    class CityInsertAdapter extends RecyclerView.Adapter<CityInsertAdapter.MyHandler>{
        List<WModel> modelList;
        Context context;

        public CityInsertAdapter(Context context,List<WModel> list){
            if (list.isEmpty()){
                modelList = new ArrayList<>();
            }
            this.modelList=list;
            this.context = context;
        }
        @NonNull
        @Override
        public CityInsertAdapter.MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.search_insert_city,parent,false);
            return new CityInsertAdapter.MyHandler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CityInsertAdapter.MyHandler holder, final int position) {
            final WModel model = modelList.get(position);
            holder.city.setText(model.getCity()+", "+model.getCountryCode());
            holder.insert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("city",model.getCity());

                }
            });
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class MyHandler extends RecyclerView.ViewHolder {
            TextView city;
            Button insert;
            public MyHandler(@NonNull View itemView) {
                super(itemView);
                city = itemView.findViewById(R.id.txt_county_name);
                insert = itemView.findViewById(R.id.btn_insert_city);
            }
        }
    }

}

