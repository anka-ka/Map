package ru.netology.map.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp
import ru.netology.map.BuildConfig.MAPKIT_API_KEY

@HiltAndroidApp
class App: Application(){

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
    }
}