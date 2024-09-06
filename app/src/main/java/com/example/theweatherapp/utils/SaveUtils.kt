package com.example.theweatherapp.utils

import android.content.Context
import android.content.SharedPreferences

class SaveUtils {

    fun saveString(context: Context, key: String, value: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(context: Context, key: String): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}