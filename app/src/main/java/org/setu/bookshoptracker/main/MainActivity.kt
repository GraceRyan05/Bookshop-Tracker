package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.setu.bookshoptracker.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        Timber.i("Bookshop Tracker MainActivity started")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val viewBookshopsButton = findViewById<Button>(R.id.viewBookshopsButton)
        val addBookshopButton = findViewById<Button>(R.id.addBookshopButton)

        viewBookshopsButton.setOnClickListener {
            Timber.d("View Bookshops button clicked")
            startActivity(Intent(this, BookshopListActivity::class.java))
        }

        addBookshopButton.setOnClickListener {
            Timber.d("Add Bookshop button clicked")
            startActivity(Intent(this, AddEditActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_bookshops -> {
                    startActivity(Intent(this, BookshopListActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddEditActivity::class.java))
                    true
                }
                R.id.nav_edit -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}