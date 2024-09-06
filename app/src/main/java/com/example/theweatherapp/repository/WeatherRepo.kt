package com.example.theweatherapp.repository

import com.example.theweatherapp.model.GeoResponse
import com.example.theweatherapp.model.WeatherResponse
import com.example.theweatherapp.service.RetrofitClient

class WeatherRepo {

   suspend fun getWeather(city: String, apiKey: String, units: String): WeatherResponse?{
       return  RetrofitClient.apiService.getWeather(city, apiKey, units).body()
    }

    suspend fun getGeoWeather(lat: Double, lon: Double, apiKey: String): List<GeoResponse>?{
        return  RetrofitClient.apiService.getGeoWeather(lat, lon, 5, apiKey).body()
    }
}