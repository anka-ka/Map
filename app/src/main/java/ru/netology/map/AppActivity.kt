package ru.netology.map


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.yandex.mapkit.MapKitFactory



class AppActivity : AppCompatActivity(R.layout.preview) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val MAPS_API_KEY = "2af4be20-1874-4d43-b589-a70c7cfd7738"
        MapKitFactory.setApiKey(MAPS_API_KEY)
        MapKitFactory.initialize(this)


        setContentView(R.layout.preview)

        requestLocationPermission()


   }

    private fun requestLocationPermission() {
        val PERMISSIONS_REQUEST_FINE_LOCATION = 1

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }
}


