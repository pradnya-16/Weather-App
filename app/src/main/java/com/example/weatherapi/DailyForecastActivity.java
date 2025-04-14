package com.example.weatherapi;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DailyForecastActivity extends AppCompatActivity {
    private RecyclerView dailyRecyclerView;
    private DailyAdapter dailyAdapter;
    private List<DayRecord> dailyData = new ArrayList<>();
    private RequestQueue requestQueue;

    private static final String WEATHER_API_KEY = "FB4TJGLH2QNP36LS9RDVCXNN8"; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyforecast);

        dailyRecyclerView = findViewById(R.id.daily_forecast_recycler_view);
        dailyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyAdapter = new DailyAdapter(dailyData);
        dailyRecyclerView.setAdapter(dailyAdapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchWeatherData();
    }

    private void fetchWeatherData() {
        String location = "Chicago, IL";
        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                + Uri.encode(location) + "?key=" + WEATHER_API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    parseWeatherData(response);
                    dailyAdapter.notifyDataSetChanged();
                },
                error -> {
                    Log.e("DailyForecastActivity", "Failed to fetch weather data: " + error.getMessage());
                    Toast.makeText(this, "Unable to fetch weather data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void parseWeatherData(JSONObject response) {
        try {
            JSONArray daysArray = response.getJSONArray("days");

            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject day = daysArray.getJSONObject(i);

                String date = day.getString("datetime");
                double tempMax = day.getDouble("tempmax");
                double tempMin = day.getDouble("tempmin");
                String description = day.getString("description");
                int precipProb = day.optInt("precipprob", 0);
                int uvIndex = day.optInt("uvindex", 0);
                String icon = day.getString("icon").replace("-", "_");

                // Get morning, afternoon, evening, and night temps from hours array
                JSONArray hours = day.getJSONArray("hours");
                double morningTemp = hours.getJSONObject(8).getDouble("temp");
                double afternoonTemp = hours.getJSONObject(13).getDouble("temp");
                double eveningTemp = hours.getJSONObject(17).getDouble("temp");
                double nightTemp = hours.getJSONObject(23).getDouble("temp");

                // Create a DayRecord object and add it to the list
                DayRecord dayRecord = new DayRecord(date, tempMax, tempMin, description, precipProb, uvIndex,
                        morningTemp, afternoonTemp, eveningTemp, nightTemp, icon);
                dailyData.add(dayRecord);
            }
        } catch (Exception e) {
            Log.e("DailyForecastActivity", "Error parsing weather data", e);
        }
    }


}
