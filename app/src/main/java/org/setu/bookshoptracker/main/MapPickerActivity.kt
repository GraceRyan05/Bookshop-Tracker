package org.setu.bookshoptracker.main

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class MapPickerActivity : AppCompatActivity() {
    private lateinit var map: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val lat = intent.getDoubleExtra("latitude", 52.245954)
        val lon = intent.getDoubleExtra("longitude", -7.139539)
        val title = intent.getStringExtra("title") ?: "Selected Location"
        val start = GeoPoint(lat, lon)

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)
        setSelectedPoint(start, title)
        showExistingMarkers()

        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point, title)
                return true
            }

            override fun longPressHelper(point: GeoPoint): Boolean {
                setSelectedPoint(point, title)
                return true
            }
        }
        map.overlays.add(MapEventsOverlay(receiver))

        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            val point = selectedPoint ?: return@setOnClickListener
            val result = intent
                .putExtra("latitude", point.latitude)
                .putExtra("longitude", point.longitude)

            setResult(RESULT_OK, result)
            finish()
        }
    }

    private fun setSelectedPoint(point: GeoPoint, title: String) {
        selectedPoint = point

        if (marker == null) {
            marker = Marker(map).apply {
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            map.overlays.add(marker)
        }

        marker?.position = point
        marker?.title = title
        map.invalidate()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        map.onPause()
        super.onPause()
    }

    private fun showExistingMarkers() {
        val bookshops = AppData.bookshops.findAll()

        bookshops.forEach { shop ->
            val point = GeoPoint(
                shop.lat,
                shop.lng
            )

            val shopMarker = Marker(map).apply {
                position = point
                title = shop.name
                snippet = shop.address
                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )
            }

            map.overlays.add(shopMarker)
        }

        map.invalidate()
    }
}