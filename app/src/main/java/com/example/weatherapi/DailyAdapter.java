package com.example.weatherapi;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DailyAdapter extends RecyclerView.Adapter<DailyViewHolder> {
    private final List<DayRecord> dailyData;

    public DailyAdapter(List<DayRecord> dailyData) {
        this.dailyData = dailyData;
    }

    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DailyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DailyViewHolder holder, int position) {
        holder.bind(dailyData.get(position));
    }

    @Override
    public int getItemCount() {
        return dailyData.size();
    }
}
