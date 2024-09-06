package com.example.theweatherapp.service

import androidx.lifecycle.LiveData
import com.example.theweatherapp.model.GeoResponse
import com.example.theweatherapp.model.WeatherResponse
import com.example.theweatherapp.utils.WeatherContants
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
   suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse?>

    @GET("geo/1.0/reverse")
    suspend fun getGeoWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<GeoResponse>?>
}

object RetrofitClient {

    private const val BASE_URL = WeatherContants.WEATHER_BASE_URL

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: WeatherService = retrofit.create(WeatherService::class.java)
}
