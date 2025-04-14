package com.example.weatherapi;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyViewHolder extends RecyclerView.ViewHolder {
    private final TextView date, condition, uvIndex, morningTemp, afternoonTemp, eveningTemp, nightTemp;
    private final TextView highLowTempF;
    private final ImageView icon;
    private final View rootView;

    public DailyViewHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        date = itemView.findViewById(R.id.date);
        condition = itemView.findViewById(R.id.condition);
        uvIndex = itemView.findViewById(R.id.uv_index);
        morningTemp = itemView.findViewById(R.id.morning_temp);
        afternoonTemp = itemView.findViewById(R.id.afternoon_temp);
        eveningTemp = itemView.findViewById(R.id.evening_temp);
        nightTemp = itemView.findViewById(R.id.night_temp);
        highLowTempF = itemView.findViewById(R.id.high_low_temp_f);
        icon = itemView.findViewById(R.id.weather_icon);
    }

    public void bind(DayRecord day) {
        String formattedDate = getFormattedDate(day.getDate());
        date.setText(formattedDate);
        condition.setText(day.getDescription());
        uvIndex.setText(String.format("UV Index: %d", day.getUvIndex()));
        morningTemp.setText(String.format("%.0f°F", day.getMorningTemp()));
        afternoonTemp.setText(String.format("%.0f°F", day.getAfternoonTemp()));
        eveningTemp.setText(String.format("%.0f°F", day.getEveningTemp()));
        nightTemp.setText(String.format("%.0f°F", day.getNightTemp()));
        ColorMaker.setColorGradient(rootView, day.getTempMax(), "F");

        highLowTempF.setText(String.format("%.0f°F / %.0f°F", day.getTempMax(), day.getTempMin()));

        int iconResId = Weather.getWeatherIconId(day.getIcon(), R.drawable.class);
        icon.setImageResource(iconResId);

        ColorMaker.setColorGradient(itemView, day.getTempMax(), "F");
        ColorMaker.setDarkerColorGradient(date, day.getTempMax(), "F");

    }

    private String getFormattedDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // fallback to original date if parsing fails
        }
    }
}
