package com.example.weatherapi;

public class TimeRecord {
    private String time;
    private double temperature;

    public TimeRecord(String time, double temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public double getTemperature() {
        return temperature;
    }
}
