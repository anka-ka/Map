package ru.netology.map


import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import ru.netology.map.BuildConfig.MAPKIT_API_KEY

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.app_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
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


