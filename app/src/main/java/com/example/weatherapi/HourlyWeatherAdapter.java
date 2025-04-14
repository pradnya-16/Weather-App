package com.example.weatherapi;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder> {

    private final Context context;
    private final List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(Context context, List<HourlyWeather> hourlyWeatherList) {
        this.context = context;
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hourly_item, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = timeFormat.format(new Date(hourlyWeather.getDatetimeEpoch() * 1000));

        holder.dayText.setText(hourlyWeather.getDayName());
        holder.timeText.setText(time);
        holder.tempText.setText(String.format(Locale.getDefault(), "%.0f°", hourlyWeather.getTemp()));
        holder.descriptionText.setText(hourlyWeather.getConditions());

        int iconResId = getWeatherIconResource(hourlyWeather.getIcon());
        if (iconResId != 0) {
            holder.weatherIcon.setImageResource(iconResId);
        }
    }


    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView dayText, timeText, tempText, descriptionText;
        ImageView weatherIcon;

        HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.day_text);
            timeText = itemView.findViewById(R.id.time_text);
            tempText = itemView.findViewById(R.id.temp_text);
            descriptionText = itemView.findViewById(R.id.description_text);
            weatherIcon = itemView.findViewById(R.id.weather_icon);
        }
    }

    private int getWeatherIconResource(String icon) {
        switch (icon) {
            case "rain":
                return R.drawable.rain;
            case "snow":
                return R.drawable.snow;
            case "clear_day":
                return R.drawable.clear_day;
            case "clear_night":
                return R.drawable.clear_night;
            case "cloudy":
                return R.drawable.cloudy;
            case "fog":
                return R.drawable.fog;
            case "hail":
                return R.drawable.hail;
            case "partly_cloudy_day":
                return R.drawable.partly_cloudy_day;
            case "partly_cloudy_night":
                return R.drawable.partly_cloudy_night;
            case "showers_day":
                return R.drawable.showers_day;
            case "showers_night":
                return R.drawable.showers_night;
            case "sleet":
                return R.drawable.sleet;
            case "thunder":
                return R.drawable.thunder;
            case "thunder_rain":
                return R.drawable.thunder_rain;
            case "thunder_showers_day":
                return R.drawable.thunder_showers_day;
            case "thunder_showers_night":
                return R.drawable.thunder_showers_night;
            case "wind":
                return R.drawable.wind;
            case "alert":
                return R.drawable.alert;
            case "target":
                return R.drawable.target;
            default:
                return R.drawable.cloudy;
        }
    }
}

