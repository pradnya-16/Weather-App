package com.example.weatherapi;

public class DayRecord {
    private String date;
    private double tempMax;
    private double tempMin;
    private String description;
    private int precipProb;
    private int uvIndex;
    private double morningTemp;
    private double afternoonTemp;
    private double eveningTemp;
    private double nightTemp;
    private String icon;

    public DayRecord(String date, double tempMax, double tempMin, String description, int precipProb, int uvIndex,
                     double morningTemp, double afternoonTemp, double eveningTemp, double nightTemp, String icon) {
        this.date = date;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.description = description;
        this.precipProb = precipProb;
        this.uvIndex = uvIndex;
        this.morningTemp = morningTemp;
        this.afternoonTemp = afternoonTemp;
        this.eveningTemp = eveningTemp;
        this.nightTemp = nightTemp;
        this.icon = icon;
    }

    public String getDate() { return date; }
    public double getTempMax() { return tempMax; }
    public double getTempMin() { return tempMin; }
    public String getDescription() { return description; }
    public int getPrecipProb() { return precipProb; }
    public int getUvIndex() { return uvIndex; }
    public double getMorningTemp() { return morningTemp; }
    public double getAfternoonTemp() { return afternoonTemp; }
    public double getEveningTemp() { return eveningTemp; }
    public double getNightTemp() { return nightTemp; }
    public String getIcon() { return icon; }
}
