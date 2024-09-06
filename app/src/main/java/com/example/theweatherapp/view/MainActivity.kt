package com.example.theweatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.theweatherapp.R
import com.example.theweatherapp.databinding.ActivityMainBinding
import com.example.theweatherapp.model.WeatherResponse

import com.example.theweatherapp.repository.WeatherRepo
import com.example.theweatherapp.utils.LocationUtils
import com.example.theweatherapp.utils.NetworkUtils
import com.example.theweatherapp.utils.SaveUtils
import com.example.theweatherapp.viewmodel.WeatherViewModel
import com.example.theweatherapp.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private val repo = WeatherRepo()

    private lateinit var viewModel: WeatherViewModel
    private var locationUtil = LocationUtils(this)
    private var savePreference = SaveUtils()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(repo))[WeatherViewModel::class.java]

        setupSearch()
        setUpObservers()
        getUserLocation()
    }

    private fun setUpObservers() {
        viewModel.weatherData.observe(this){ setWeatherDataObservers(it) }
        viewModel.geoData.observe(this) { getWeatherDetails(it[0].name) }
        viewModel.showError.observe(this) {showErrorMessage(it)}
        viewModel.loading.observe(this) { binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE }
    }

    private fun setWeatherDataObservers(it: WeatherResponse) {
        savePreference.saveString(this, "city", it.name)
        with(binding) {
            tvCity.text = it.name
            tvTemperature.text = it.main.temp.toString()
            tvDescription.text = it.weather[0].description.uppercase()
        }

        // Load the weather icon
        val iconUrl = "https://openweathermap.org/img/wn/${it.weather[0].icon}@2x.png"
        Glide.with(this)
            .load(iconUrl)
            .into(binding.weatherIcon)
    }

    private fun getUserLocation() {

        if (!NetworkUtils().isNetworkAvailable(this)) {
            showErrorMessage(errorMessage = "Error: Please check the internet connectivity")
            return
        }
        if (locationUtil.checkLocationPermissions()) {
            val lastCity = savePreference.getString(this, "city")
            lastCity?.let {
                getWeatherDetails(it)
            } ?: run {
                getLastSavedLocation()
            }
        }
        else {
            requestLocationPermissions()
        }
    }

    private fun setupSearch() {
        with(binding) {
            searchCity.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchCity()
                    true  // Return true to indicate the action was handled
                } else {
                    false  // Return false to allow further handling of the event
                }
            }
            searchButton.setOnClickListener {
                searchCity()
            }
        }
    }

    private fun searchCity() {

        val cityName = binding.searchCity.text.toString().trim()
        // Ensure city name is not empty
        if (cityName.isNotEmpty() && cityName.matches(Regex("^[a-zA-Z ]+$"))) {
            hideKeyboard()
            binding.searchCity.text = null
            binding.searchCity.clearFocus()
            // Fetch weather data for the entered city
            getWeatherDetails(cityName)
        } else {
            // Show message if city name is empty
            showErrorMessage(errorMessage = "Please enter a city name")
        }
    }

    private fun getWeatherDetails(city: String) {
        val networkUtils = NetworkUtils().isNetworkAvailable(this)
        if (!networkUtils) {
            showErrorMessage(errorMessage = "Error: Please check the internet connectivity")
            return
        }
        viewModel.getWeather(city)
    }

    // Helpers

    // Function to request location permissions
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LocationUtils.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    // Handle the result of permission request
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationUtils.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLastSavedLocation()
        } else {
            showErrorMessage(errorMessage = "Location permission denied")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastSavedLocation()  {

        val networkUtils = NetworkUtils().isNetworkAvailable(this)
        if (!networkUtils) {
            showErrorMessage(errorMessage =  "Error: Please check the internet connectivity")
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (locationUtil.checkLocationPermissions()) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Got the location
                        viewModel.getGeoWeather(location.latitude, location.longitude)
                    } else {
                        showErrorMessage(errorMessage = "Unable to get location")
                    }

                }
                .addOnFailureListener {
                    showErrorMessage(errorMessage =  "Failed to get location")
                }
        }
    }

    // Function to hide the keyboard
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showErrorMessage(it: Boolean = true, errorMessage: String = "Error: Something went wrong, please try again") {
        if (it) { Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show() }
    }
}


