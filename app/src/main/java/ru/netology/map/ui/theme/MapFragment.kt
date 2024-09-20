package ru.netology.map.ui.theme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.R

import ru.netology.map.constants.Constants.Companion.DEFAULT_POINT

class MapFragment : Fragment(R.layout.map) {

    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.mapView)

        mapView.map.move(
            CameraPosition(DEFAULT_POINT, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )

        mapView.map.addTapListener(object : GeoObjectTapListener {

            override fun onObjectTap(p0: GeoObjectTapEvent): Boolean {
                TODO("Not yet implemented")
            }
        })

        mapView.map.addInputListener(object : InputListener {

            override fun onMapTap(p0: Map, p1: Point) {
                TODO("Not yet implemented")
            }

            override fun onMapLongTap(p0: Map, p1: Point) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onStop() {

        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {

        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }
     fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)

        if (selectionMetadata != null) {
            mapView.mapWindow.map.selectGeoObject(selectionMetadata)
        }

        return selectionMetadata != null
    }

    fun onMapTap(map: Map, point: Point) {
        mapView.mapWindow.map.deselectGeoObject()
    }

    fun onMapLongTap(map: Map, point: Point) {
    }
}
