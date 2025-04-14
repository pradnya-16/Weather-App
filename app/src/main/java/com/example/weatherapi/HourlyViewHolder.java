package com.example.weatherapi;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class HourlyViewHolder extends RecyclerView.ViewHolder {

    TextView timeTextView;
    TextView tempTextView;

    public HourlyViewHolder(View itemView) {
        super(itemView);
        timeTextView = itemView.findViewById(R.id.time_text);
        tempTextView = itemView.findViewById(R.id.temp_text);
    }

    // Bind method to populate data into the view holder
    public void bind(TimeRecord timeRecord) {
        timeTextView.setText(timeRecord.getTime());
        tempTextView.setText(String.format(Locale.getDefault(), "%.0f°", timeRecord.getTemperature()));
    }
}
