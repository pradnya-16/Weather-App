package com.example.weatherapi;

public class HourlyWeather {
    private final String dayName;
    private final long datetimeEpoch;
    private final String icon;
    private final double temp;
    private final String conditions;

    public HourlyWeather(String dayName, long datetimeEpoch, String icon, double temp, String conditions) {
        this.dayName = dayName;
        this.datetimeEpoch = datetimeEpoch;
        this.icon = icon;
        this.temp = temp;
        this.conditions = conditions;
    }

    public String getDayName() {
        return dayName;
    }

    public long getDatetimeEpoch() {
        return datetimeEpoch;
    }

    public String getIcon() {
        return icon;
    }

    public double getTemp() {
        return temp;
    }

    public String getConditions() {
        return conditions;
    }
}
