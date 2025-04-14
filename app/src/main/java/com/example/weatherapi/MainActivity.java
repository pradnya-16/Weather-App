package com.example.weatherapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.Volley;
import com.example.weatherapi.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean isCelsius = false;
    private Weather currentWeather;
    private RequestQueue requestQueue;
    private String currentLocation = "auto:ip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestQueue = Volley.newRequestQueue(this);

        if (!isNetworkAvailable()) {
            showNoInternetConnectionDialog();
        } else {
            fetchWeatherForLocation("Chicago, IL");
        }

        requestQueue = Volley.newRequestQueue(this);

        binding.map.setOnClickListener(v -> openMapForLocation(currentWeather.getResolvedAddress()));
        binding.location.setOnClickListener(v -> showLocationDialog());
        binding.unitToggle.setOnClickListener(v -> toggleTemperatureUnit());
        binding.share.setOnClickListener(v -> shareWeatherDetails());
        binding.calendar.setOnClickListener(v -> openDailyForecastActivity());
        binding.target.setOnClickListener(v -> resetToChicago());

        // Initialize downloader and fetch weather data
        fetchWeatherForLocation("Chicago, IL");
    }

    private void resetToChicago() {
        // Set location to Chicago, IL
        currentLocation = "Chicago, IL";
        Toast.makeText(this, "Location reset to Chicago, IL", Toast.LENGTH_SHORT).show();
        // Fetch weather data for Chicago
        fetchWeatherForLocation("Chicago, IL");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showLocationErrorDialog(String location) {
        new AlertDialog.Builder(this)
                .setTitle("Location Error")
                .setMessage("The specified location '" + location + "' could not be resolved. Please try a different location.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showWeatherDataErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Weather Data Error")
                .setMessage("There was an error retrieving the weather data. Please try again later.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showNoInternetConnectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("This app requires an internet connection to function properly. Please check your connection and try again.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openDailyForecastActivity() {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        startActivity(intent);
    }

    private void shareWeatherDetails() {
        String shareText = String.format(
                "Weather Update:\n%s\n%s\nFeels like: %s\nHumidity: %s\nUV Index: %s\nWind: %s\nSunrise: %s\nSunset: %s",
                binding.address.getText(),
                binding.CurrentTemp.getText(),
                binding.feelslike.getText(),
                binding.humidity.getText(),
                binding.uvIndex.getText(),
                binding.winds.getText(),
                binding.Sunrise.getText(),
                binding.Sunset.getText());

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share weather details"));
    }

    private void openMapForLocation(String location) {
        Uri geoLocation = Uri.parse("geo:0,0?q=" + Uri.encode(location));

        Log.d("MainActivity", "Map URI: " + geoLocation.toString());

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoLocation);

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback to web URL if no map app is found
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(location));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void requestWeatherData(String location) {
        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + location;
        url += "?unitGroup=us&key=" + getString(R.string.API_KEY);

        VisualCrossingDownloader downloader = new VisualCrossingDownloader(this);
        downloader.fetchWeatherData(url, new VisualCrossingDownloader.Listener<Weather>() {
            @Override
            public void onSuccess(Weather weather) {
                runOnUiThread(() -> displayWeatherData(weather));
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Failed to load data for " + location, Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void toggleTemperatureUnit() {
        isCelsius = !isCelsius;

        // Update the unit toggle icon based on the new state
        binding.unitToggle.setImageResource(isCelsius ? R.drawable.units_c : R.drawable.units_f);

        // Update the current temperature and feels like temperature
        int currentTemp = isCelsius ? (int) Math.round(convertToCelsius(currentWeather.getTemp())) : (int) Math.round(currentWeather.getTemp());
        int feelsLikeTemp = isCelsius ? (int) Math.round(convertToCelsius(currentWeather.getFeelsLike())) : (int) Math.round(currentWeather.getFeelsLike());

        // Display temperatures with the degree symbol directly next to the value
        binding.CurrentTemp.setText(String.format(Locale.getDefault(), "%d°%s", currentTemp, isCelsius ? "C" : "F"));
        binding.feelslike.setText(String.format(Locale.getDefault(), "Feels Like: %d°%s", feelsLikeTemp, isCelsius ? "C" : "F"));

        // Update the hourly temperature chart
        TreeMap<String, Double> updatedTemps = new TreeMap<>();
        for (Map.Entry<String, Double> entry : currentWeather.getHourlyTemps().entrySet()) {
            double temp = isCelsius ? convertToCelsius(entry.getValue()) : entry.getValue();
            updatedTemps.put(entry.getKey(), temp);
        }
        new ChartMaker(this, binding).makeChart(updatedTemps, System.currentTimeMillis());

    }

    // Conversion methods
    private double convertToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }

    private double convertToCelsius(double fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    private void setupHourlyRecyclerView(List<HourlyWeather> hourlyData) {
        HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(this, hourlyData);
        binding.hourlyUpdate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.hourlyUpdate.setAdapter(adapter);
    }


    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a Location");

        final EditText input = new EditText(this);
        input.setHint("City, State or City, Country");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newLocation = input.getText().toString().trim();
            if (!newLocation.isEmpty()) {
                fetchWeatherForLocation(newLocation);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void fetchWeatherForLocation(String location) {

        if (!isNetworkAvailable()) {
            showNoInternetConnectionDialog();
            return;
        }

        VisualCrossingDownloader downloader = new VisualCrossingDownloader(this);
        downloader.fetchWeatherData(location, new VisualCrossingDownloader.Listener<Weather>() {
            @Override
            public void onSuccess(Weather weather) {
                runOnUiThread(() -> displayWeatherData(weather));
            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof LocationNotFoundException) { // Custom exception for location not found
                    showLocationErrorDialog(location);
                } else {
                    showWeatherDataErrorDialog();
                }
            }
        });
    }

    public static int getWeatherIconId(String iconName, Class<?> drawableClass) {
        iconName = iconName.replace("-", "_"); // Replace hyphens with underscores for compatibility
        try {
            Field field = drawableClass.getField(iconName);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
            return R.mipmap.ic_launcher; // Default icon if not found
        }
    }


    private void displayWeatherData(Weather weather) {
        currentWeather = weather;
        double currentTempF = weather.getTemp();

        // Set the main layout background color based on temperature
        ColorMaker.setColorGradient(binding.main, currentTempF, "F");

        // Set the iconBar to a darker version of the main background
        ColorMaker.setDarkerColorGradient(binding.iconBar, currentTempF, "F");

        ColorMaker.setColorGradient(binding.main, currentTempF, "F");

        int currentTemp = (int) Math.round(convertToFahrenheit(weather.getTemp()));
        int feelsLikeTemp = (int) Math.round(convertToFahrenheit(weather.getFeelsLike()));

        binding.address.setText(weather.getResolvedAddress());
        binding.CurrentTemp.setText(String.format(Locale.getDefault(), "%.1f °%s", weather.getTemp(), isCelsius ? "C" : "F"));
        binding.feelslike.setText(String.format("Feels Like: %.1f °%s", weather.getFeelsLike(), isCelsius ? "C" : "F"));
        binding.humidity.setText(String.format("Humidity: %.1f%%", weather.getHumidity()));
        binding.uvIndex.setText(String.format("UV Index: %d", weather.getUvIndex()));
        binding.Sunrise.setText("Sunrise: " + weather.getSunrise());
        binding.Sunset.setText("Sunset: " + weather.getSunset());

        String windDirection = getDirection(weather.getWindDirection());
        binding.winds.setText(String.format(Locale.getDefault(), "Winds: %s at %.1f mph gusting to %.1f mph",
                windDirection, weather.getWindSpeed(), weather.getWindGust()));

        binding.visibility.setText(String.format(Locale.getDefault(), "Visibility: %.1f miles", weather.getVisibility()));
        String weatherDetails = String.format("%s, Cloud Cover: %.0f%%", weather.getConditions(), weather.getCloudCover());
        binding.weatherDescription.setText(weatherDetails);

        String iconName = weather.getHourlyIcons().isEmpty() ? "default_icon" : weather.getHourlyIcons().get(0).replace("-", "_");
        Weather.setWeatherIcon(this, iconName, binding.weatherIcon);

        List<HourlyWeather> hourlyData = new ArrayList<>();
        for (int i = 0; i < weather.getHourlyTemps().size(); i++) {
            long datetimeEpoch = weather.getHourlyDatetimeEpochs().get(i);
            String dayName = (i == 0) ? "Today" : new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(datetimeEpoch * 1000));
            String icon = weather.getHourlyIcons().get(i);
            double temp = weather.getHourlyTemp().get(i);
            String conditions = weather.getHourlyConditions().get(i);

            hourlyData.add(new HourlyWeather(dayName, datetimeEpoch, icon, temp, conditions));
        }

        setupHourlyRecyclerView(hourlyData);


        new ChartMaker(this, binding).makeChart(new TreeMap<>(weather.getHourlyTemps()), System.currentTimeMillis());
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // Should never happen
    }

    public void displayChartTemp(float time, float tempVal) {
        SimpleDateFormat sdf = new SimpleDateFormat("h a", Locale.US);
        Date d = new Date((long) time);
        binding.chartTemp.setText(
                String.format(Locale.getDefault(), "%s, %.0f°", sdf.format(d), tempVal));
        binding.chartTemp.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Thread.sleep(5000);
                runOnUiThread(() -> binding.chartTemp.setVisibility(View.GONE));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private TreeMap<String, Double> makeTemperaturePoints() {
        TreeMap<String, Double> timeTempValues = new TreeMap<>();
        timeTempValues.put("00:00:00", 54.7);
        timeTempValues.put("01:00:00", 52.3);
        timeTempValues.put("02:00:00", 50.4);
        timeTempValues.put("03:00:00", 47.8);
        timeTempValues.put("04:00:00", 46.5);
        timeTempValues.put("05:00:00", 45.9);
        timeTempValues.put("06:00:00", 45.9);
        timeTempValues.put("07:00:00", 46.6);
        timeTempValues.put("08:00:00", 46.4);
        timeTempValues.put("09:00:00", 51.8);
        timeTempValues.put("10:00:00", 57.8);
        timeTempValues.put("11:00:00", 63.3);
        timeTempValues.put("12:00:00", 66.8);
        timeTempValues.put("13:00:00", 68.9);
        timeTempValues.put("14:00:00", 70.1);
        timeTempValues.put("15:00:00", 71.7);
        timeTempValues.put("16:00:00", 71.7);
        timeTempValues.put("17:00:00", 71.0);
        timeTempValues.put("18:00:00", 68.6);
        timeTempValues.put("19:00:00", 65.8);
        timeTempValues.put("20:00:00", 62.7);
        timeTempValues.put("21:00:00", 59.2);
        timeTempValues.put("22:00:00", 57.8);
        timeTempValues.put("23:00:00", 58.9);
        timeTempValues.put("24:00:00", 58.9);
        return timeTempValues;
    }
}
