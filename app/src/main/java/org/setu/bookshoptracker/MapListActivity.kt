package org.setu.bookshoptracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.setu.bookshoptracker.main.AddEditActivity
import org.setu.bookshoptracker.main.AllBooksActivity
import org.setu.bookshoptracker.main.Bookshop
import org.setu.bookshoptracker.main.BookshopListActivity
import org.setu.bookshoptracker.main.MainActivity
import org.setu.bookshoptracker.main.SettingsActivity

class MapListActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var imgPlace: ImageView
    private lateinit var mapEditButton: Button

    private var selectedBookshop: Bookshop? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Configuration.getInstance().userAgentValue = packageName
        setContentView(R.layout.activity_map_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        mapView = findViewById(R.id.mapList)
        txtTitle = findViewById(R.id.mapTitleText)
        txtDescription = findViewById(R.id.mapDescText)
        imgPlace = findViewById(R.id.mapImageView)
        mapEditButton = findViewById(R.id.mapEditButton)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.overlays.add(CopyrightOverlay(this))

        mapEditButton.setOnClickListener {
            selectedBookshop?.let { shop ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("id", shop.id)
                startActivity(intent)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_bookshops

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_bookshops -> {
                    startActivity(Intent(this, BookshopListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddEditActivity::class.java))
                    true
                }
                R.id.nav_books -> {
                    startActivity(Intent(this, AllBooksActivity::class.java))
                    true
                }
                R.id.nav_edit -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        loadMarkers()
    }

    private fun loadMarkers() {
        mapView.overlays.clear()
        mapView.overlays.add(CopyrightOverlay(this))

        val bookshops = AppData.bookshops.findAll()

        if (bookshops.isEmpty()) {
            txtTitle.text = "No bookshops found"
            txtDescription.text = "Add a bookshop to view it on the map."
            imgPlace.visibility = View.GONE
            mapEditButton.visibility = View.GONE

            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(GeoPoint(52.245954, -7.139539))
            mapView.invalidate()
            return
        }

        val points = ArrayList<GeoPoint>()

        bookshops.forEach { shop ->
            val position = GeoPoint(shop.lat, shop.lng)
            points.add(position)

            val marker = Marker(mapView).apply {
                this.position = position
                this.title = shop.name
                this.snippet = shop.address
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }

            marker.setOnMarkerClickListener { _, _ ->
                displayShop(shop)
                true
            }

            mapView.overlays.add(marker)
        }

        if (points.size == 1) {
            mapView.controller.setZoom(16.0)
            mapView.controller.setCenter(points.first())
            displayShop(bookshops.first())
        } else {
            val boundingBox = BoundingBox.fromGeoPoints(points)
            mapView.post {
                mapView.zoomToBoundingBox(boundingBox, true, 100)
            }
            displayShop(bookshops.first())
        }

        mapView.invalidate()
    }

    private fun displayShop(shop: Bookshop) {
        selectedBookshop = shop

        txtTitle.text = shop.name
        txtDescription.text = "${shop.address}\n${shop.description}"

        if (!shop.imageUri.isNullOrEmpty()) {
            try {
                imgPlace.setImageURI(Uri.parse(shop.imageUri))
                imgPlace.visibility = View.VISIBLE
            } catch (_: Exception) {
                imgPlace.visibility = View.GONE
            }
        } else {
            imgPlace.visibility = View.GONE
        }

        mapEditButton.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        loadMarkers()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDetach()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.tileProvider.clearTileCache()
        mapView.invalidate()
    }
}