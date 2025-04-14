package com.example.weatherapi;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VisualCrossingDownloader {
    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String API_KEY = "FB4TJGLH2QNP36LS9RDVCXNN8"; // Replace with your actual API key
    private final RequestQueue requestQueue;

    public VisualCrossingDownloader(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public interface Listener<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    public void fetchWeatherData(String location, Listener<Weather> listener) {
        String url = new Uri.Builder()
                .scheme("https")
                .authority("weather.visualcrossing.com")
                .appendPath("VisualCrossingWebServices")
                .appendPath("rest")
                .appendPath("services")
                .appendPath("timeline")
                .appendPath(location) // Automatically encodes special characters in location
                .appendQueryParameter("key", API_KEY)
                .build()
                .toString();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Weather weather = Weather.parseFromJson(response);
                        listener.onSuccess(weather);
                    } catch (JSONException e) {
                        listener.onFailure(e);
                    }
                },
                error -> listener.onFailure(new Exception(error.getMessage()))
        );


        requestQueue.add(jsonObjectRequest);
    }

}
