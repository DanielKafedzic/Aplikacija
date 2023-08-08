package com.example.weather

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weather.api.NetworkClient
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.dto.WeatherTime
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var client = NetworkClient()

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            super.onLocationResult(location)

            location.lastLocation?.latitude?.let { latitude ->
                location.lastLocation?.longitude?.let { longitude ->
                    binding.latitude.setText(latitude.toString())
                    binding.longitude.setText(longitude.toString())
                    fetchWeatherData(latitude, longitude)
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            requestLocationUpdates()
        } else {
            showPermissionRationaleDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        binding.button.setOnClickListener {
            val latitude = binding.latitude.text.toString().toDoubleOrNull()
            val longitude = binding.longitude.text.toString().toDoubleOrNull()

            if (latitude != null && longitude != null) {
                fetchWeatherData(latitude, longitude)
            } else {
                Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        client.getForecast(latitude, longitude).enqueue(object : Callback<WeatherTime> {
            override fun onResponse(call: Call<WeatherTime>, response: Response<WeatherTime>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    binding.label.text = "${data?.currentWeather?.temperature} C"
                } else {
                    binding.label.text =
                        "Response code: ${response.code()}, ${response.errorBody()}"
                }
            }

            override fun onFailure(call: Call<WeatherTime>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT
                ).show()
                t.printStackTrace()
            }
        })
    }

    private fun showPermissionRationaleDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Niste dali dozvolu za lokaciju, ne moze aplikacija nastaviti.")
        builder.setTitle("Upozorenje")
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finish()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationUpdates()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }
}
