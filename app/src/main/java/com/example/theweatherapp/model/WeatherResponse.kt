package com.example.theweatherapp.model


data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class Weather(
    val description: String,
    val icon: String
)

data class GeoResponse(
    val name: String
)

