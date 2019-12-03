package weather.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BaseWidgetConfigureActivity BaseWidgetConfigureActivity}
 */
public class BaseWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {

        CharSequence widgetText = BaseWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.base_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, App.URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {

                    try {
                        JSONObject weatherObj = response.getJSONArray("weather").getJSONObject(0);
                        JSONObject sys = response.getJSONObject("sys");
                        String desc = weatherObj.getString("description");
                        long sunrise = sys.getInt("sunrise");
                        long sunset = sys.getInt("sunset");
                        long currentTime = response.getLong("dt");
                        int id = weatherObj.getInt("id");
                        int temp = response.getJSONObject("main").getInt("temp");
                        int wind_speed = response.getJSONObject("wind").getInt("speed");
                        int wind_deg = response.getJSONObject("wind").getInt("deg");
                        int humidity = response.getJSONObject("main").getInt("humidity");


                        views.setTextViewText(R.id.txt_temp, String.valueOf(temp));
                        views.setTextViewText(R.id.txt_cent, Html.fromHtml("&#8451"));
                        views.setTextViewText(R.id.txt_desc,desc);
                        views.setTextViewText(R.id.txt_wind_speed,String.valueOf(wind_speed));
                        views.setTextViewText(R.id.txt_wind_deg, String.valueOf(wind_deg));
                        views.setTextViewText(R.id.txt_humidity,String.valueOf(humidity));

                        switch (id) {
                            case 800:
                                if (currentTime >= sunrise && currentTime < sunset) {
                                    Log.i("times", "day");
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.sun);
                                    break;
                                } else {
                                    Log.i("times", "night");
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.moon);
                                    break;
                                }

                            case 200:
                                if (currentTime >= sunrise && currentTime < sunset) {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.thunderstorm);
                                    break;
                                } else {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.thunderstorm_night);
                                    break;
                                }

                            case 300:
                                if (currentTime >= sunrise && currentTime < sunset) {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.drizzle);
                                    break;
                                } else {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.drizzle_night);
                                    break;
                                }

                            case 500:
                                views.setImageViewResource(R.id.img_weather_ic, R.drawable.rain);
                                break;
                            case 600:
                                views.setImageViewResource(R.id.img_weather_ic, R.drawable.snow);
                                break;
                            case 700:
                                views.setImageViewResource(R.id.img_weather_ic, R.drawable.atmosphere);
                                break;
                            case 801:
                                if (currentTime >= sunrise && currentTime < sunset) {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.cloudy);
                                    break;
                                } else {
                                    views.setImageViewResource(R.id.img_weather_ic, R.drawable.cloadymoon);
                                    break;
                                }

                            default:
                                return;
                        }

                        //update manually
                        /*------------------------------*/
                        Intent updateIntent = new Intent(context,BaseWidget.class);
                        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        int[] idArr = new int[]{appWidgetId};
                        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,idArr);
                        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,appWidgetId,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                        views.setOnClickPendingIntent(R.id.show_weather,pendingUpdate);
                        /*------------------------------*/

                        appWidgetManager.updateAppWidget(appWidgetId, views);

                    } catch (JSONException e) {
                        Log.e("connect", e.getLocalizedMessage());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("connect", e.getLocalizedMessage());
            }
        });
        App.queue.add(request);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "weather updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            BaseWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

