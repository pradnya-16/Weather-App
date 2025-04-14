package com.example.weatherapi;

import android.graphics.Color;
import android.view.View;
import android.graphics.drawable.GradientDrawable;

public class ColorMaker {

    public static void setColorGradient(View view, double temperature, String unit) {
        if ("C".equals(unit)) {
            temperature = (temperature * 9 / 5) + 32;
        }

        int startColor;
        int endColor;

        if (temperature <= 32) { // Cold (Blue)
            startColor = Color.parseColor("#001f3f");
            endColor = Color.parseColor("#0074D9");
        } else if (temperature <= 60) { // Cool (Green)
            startColor = Color.parseColor("#2ECC40");
            endColor = Color.parseColor("#66BB6A");
        } else { // Hot (Red)
            startColor = Color.parseColor("#FF4136");
            endColor = Color.parseColor("#85144b");
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        view.setBackground(gradientDrawable);
    }

    public static void setDarkerColorGradient(View view, double temperature, String unit) {
        // Convert Celsius to Fahrenheit if needed
        if ("C".equals(unit)) {
            temperature = (temperature * 9 / 5) + 32;
        }

        int startColor;
        int endColor;

        if (temperature <= 32) { // Darker Cold (Blue)
            startColor = Color.parseColor("#00152d");
            endColor = Color.parseColor("#004080");
        } else if (temperature <= 60) { // Darker Cool (Green)
            startColor = Color.parseColor("#1E7C2B");
            endColor = Color.parseColor("#4A8A53");
        } else { // Darker Hot (Red)
            startColor = Color.parseColor("#C0392B");
            endColor = Color.parseColor("#6D213B");
        }

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, new int[]{startColor, endColor});
        view.setBackground(gradientDrawable);
    }
}

