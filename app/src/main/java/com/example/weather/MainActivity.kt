package com.example.weather

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weather.api.NetworkClient
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.dto.WeatherTime
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val client = NetworkClient()

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            super.onLocationResult(location)

            location.lastLocation?.let { lastLocation ->
                val latitude = lastLocation.latitude
                val longitude = lastLocation.longitude

                binding.latitude.setText(latitude.toString())
                binding.longitude.setText(longitude.toString())
                fetchWeatherData(latitude, longitude)

                fusedLocationProviderClient.removeLocationUpdates(this)
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
                binding.progressBar.visibility = View.VISIBLE // Show the progress bar
                fetchWeatherData(latitude, longitude)
            } else {
                Toast.makeText(this, "Invalid latitude or longitude", Toast.LENGTH_SHORT).show()
            }
        }

        binding.lokacija.setOnClickListener {
            requestLocationPermission()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                // Handle the "Settings" menu item click here
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        binding.progressBar.visibility = View.VISIBLE

        client.getForecast(latitude, longitude).enqueue(object : Callback<WeatherTime> {
            override fun onResponse(call: Call<WeatherTime>, response: Response<WeatherTime>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val data = response.body()

                    val intent = Intent(this@MainActivity, WeatherDetailsActivity::class.java)
                    intent.putExtras(WeatherDetailsActivity.createBundle(data?.currentWeather))
                    startActivity(intent)
                } else {
                    binding.label.text = getString(R.string.response_error, response.code(), response.errorBody())
                }
            }

            override fun onFailure(call: Call<WeatherTime>, t: Throwable) {
                binding.progressBar.visibility = View.GONE

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
            fusedLocationProviderClient.requestLocationUpdates(
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
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
