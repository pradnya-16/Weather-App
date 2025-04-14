package com.example.weatherapi;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.widget.ImageView;
import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Weather {
    private String resolvedAddress;
    private double visibility;
    private double windDirection;
    private double windSpeed;
    private double windGust;
    private double temp;
    private String conditions;;
    private double cloudCover;
    private double feelsLike;
    private double humidity;
    private int uvIndex;
    private String sunrise;
    private String sunset;
    private double highTemp;
    private double lowTemp;
    private TreeMap<String, Double> hourlyTemps = new TreeMap<>();
    private List<Long> hourlyDatetimeEpochs = new ArrayList<>();
    private List<String> hourlyIcons = new ArrayList<>();
    private List<Double> hourlyTemp = new ArrayList<>();
    private List<String> hourlyConditions = new ArrayList<>();

    public static Weather parseFromJson(JSONObject json) throws JSONException {
        Weather weather = new Weather();
        weather.resolvedAddress = json.getString("resolvedAddress");

        JSONObject currentConditions = json.getJSONObject("currentConditions");
        weather.temp = currentConditions.getDouble("temp");
        weather.feelsLike = currentConditions.getDouble("feelslike");
        weather.humidity = currentConditions.getDouble("humidity");
        weather.uvIndex = currentConditions.getInt("uvindex");
        weather.conditions = currentConditions.getString("conditions");
        weather.sunrise = currentConditions.getString("sunrise");
        weather.sunset = currentConditions.getString("sunset");
        weather.windDirection = currentConditions.getDouble("winddir");
        weather.windSpeed = currentConditions.getDouble("windspeed");
        weather.windGust = currentConditions.optDouble("windgust", 0);
        weather.conditions = currentConditions.getString("conditions"); // Parse weather description
        weather.cloudCover = currentConditions.getDouble("cloudcover");
        weather.visibility = currentConditions.getDouble("visibility");

        JSONObject todayForecast = json.getJSONArray("days").getJSONObject(0);
        weather.highTemp = todayForecast.getDouble("tempmax");
        weather.lowTemp = todayForecast.getDouble("tempmin");

        JSONArray hours = json.getJSONArray("days").getJSONObject(0).getJSONArray("hours");
        for (int i = 0; i < hours.length(); i++) {
            JSONObject hour = hours.getJSONObject(i);
            String time = hour.getString("datetime");
            double temp = hour.getDouble("temp");
            weather.hourlyTemps.put(time, temp);
        }

        JSONArray daysArray = json.getJSONArray("days");
        if (daysArray.length() > 0) {
            // Get the hours array for the first day (today)
            JSONObject today = daysArray.getJSONObject(0);
            JSONArray hoursArray = today.getJSONArray("hours");

            for (int i = 0; i < hoursArray.length(); i++) {
                JSONObject hour = hoursArray.getJSONObject(i);

                // Extract required data from each hourly entry
                long datetimeEpoch = hour.getLong("datetimeEpoch");
                String icon = hour.getString("icon");
                double temp = hour.getDouble("temp");
                String conditions = hour.getString("conditions");

                // Add to respective lists
                weather.hourlyDatetimeEpochs.add(datetimeEpoch);
                weather.hourlyIcons.add(icon);
                weather.hourlyTemp.add(temp);
                weather.hourlyConditions.add(conditions);
            }
        }

        return weather;
    }

    // Getter for resolvedAddress
    public String getResolvedAddress() {
        return resolvedAddress;
    }

    // Getter for current temperature
    public double getTemp() {
        return temp;
    }

    // Getter for feels like temperature
    public double getFeelsLike() {
        return feelsLike;
    }

    // Getter for humidity
    public double getHumidity() {
        return humidity;
    }

    // Getter for UV index
    public int getUvIndex() {
        return uvIndex;
    }

    // Getter for sunrise time
    public String getSunrise() {
        return sunrise;
    }

    // Getter for sunset time
    public String getSunset() {
        return sunset;
    }

    // Getter for hourly temperatures
    public Map<String, Double> getHourlyTemps() {
        return hourlyTemps;
    }

    public String getConditions() {
        return conditions;
    }

    public double getWindDirection() {
        return windDirection;
    }

    // Getter for windSpeed
    public double getWindSpeed() {
        return windSpeed;
    }

    // Getter for windGust
    public double getWindGust() {
        return windGust;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public static void setWeatherIcon(Context context, String iconName, ImageView imageView) {
        try {
            Field field = R.drawable.class.getField(iconName);
            int resId = field.getInt(null);
            imageView.setImageResource(resId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public double getVisibility() {
        return visibility;
    }

    public double getHighTemp() {
        return highTemp;
    }

    public double getLowTemp() {
        return lowTemp;
    }

    public List<Long> getHourlyDatetimeEpochs() {
        return hourlyDatetimeEpochs;
    }

    public List<String> getHourlyIcons() {
        return hourlyIcons;
    }

    public List<Double> getHourlyTemp() {
        return hourlyTemp;
    }

    public List<String> getHourlyConditions() {
        return hourlyConditions;
    }

    public static int getWeatherIconId(String iconName, Class<?> drawableClass) {
        try {
            // Replace hyphens with underscores to match Android resource naming conventions
            iconName = iconName.replace("-", "_");
            Field field = drawableClass.getField(iconName);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            // Return a default icon if the specified icon is not found
            return R.mipmap.ic_launcher; // Replace with a better default icon if available
        }
    }

}


