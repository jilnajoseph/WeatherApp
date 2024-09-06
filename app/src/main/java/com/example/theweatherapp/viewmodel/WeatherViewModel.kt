package com.example.theweatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.theweatherapp.model.GeoResponse
import com.example.theweatherapp.model.WeatherResponse
import com.example.theweatherapp.repository.WeatherRepo
import com.example.theweatherapp.utils.NetworkUtils
import com.example.theweatherapp.utils.WeatherContants
import kotlinx.coroutines.launch

class WeatherViewModel(val repository: WeatherRepo) : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _geoData = MutableLiveData<List<GeoResponse>>()
    val geoData: LiveData<List<GeoResponse>> get() = _geoData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _showError = MutableLiveData<Boolean>()
    val showError: LiveData<Boolean> get() = _showError

    fun getWeather(city: String) {

        _loading.value = true
        _showError.value = false
        viewModelScope.launch {
            val data: WeatherResponse? =
                repository.getWeather(city, WeatherContants.API_KEY, WeatherContants.METRIC)
            _loading.value = false
            data?.let {
                _weatherData.value = it
            } ?: run {
                _showError.value = true
            }
        }
    }

    fun getGeoWeather(lat: Double, lon: Double) {

        _loading.value = true
        _showError.value = false
        viewModelScope.launch {
            val data: List<GeoResponse>? =
                repository.getGeoWeather(lat, lon, WeatherContants.API_KEY)
            _loading.value = false
            data?.let {
                _geoData.value = it
            } ?: run {
                _showError.value = true
            }
        }
    }
}




