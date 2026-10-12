package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var totalShopsText: TextView
    private lateinit var totalBooksText: TextView
    private lateinit var darkModeSwitch: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        totalShopsText = findViewById(R.id.totalShopsText)
        totalBooksText = findViewById(R.id.totalBooksText)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)

        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        darkModeSwitch.isChecked = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_edit

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
                    finish()
                    true
                }
                R.id.nav_edit -> true
                else -> false
            }
        }

        updateStats()
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    private fun updateStats() {
        val shopCount = AppData.bookshops.findAll().size
        val allBooks = AppData.bookshops.findAll().flatMap { AppData.books.findAllByBookshop(it.id) }
        totalShopsText.text = "Total Bookshops: $shopCount"
        totalBooksText.text = "Total Books Tracked: ${allBooks.size}"
    }
}