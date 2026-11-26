package org.setu.bookshoptracker.views.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import org.setu.bookshoptracker.views.allbooks.AllBooksView
import org.setu.bookshoptracker.views.bookshop.BookshopView
import org.setu.bookshoptracker.views.bookshoplist.BookshopListView
import org.setu.bookshoptracker.views.mainmenu.MainMenuView

class SettingsView : AppCompatActivity() {

    private lateinit var presenter: SettingsPresenter
    private lateinit var totalShopsText: TextView
    private lateinit var totalBooksText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        presenter = SettingsPresenter(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        totalShopsText = findViewById(R.id.totalShopsText)
        totalBooksText = findViewById(R.id.totalBooksText)

        val darkModeSwitch = findViewById<SwitchMaterial>(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)

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
                    startActivity(Intent(this, MainMenuView::class.java))
                    finish()
                    true
                }
                R.id.nav_bookshops -> {
                    startActivity(Intent(this, BookshopListView::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, BookshopView::class.java))
                    true
                }
                R.id.nav_books -> {
                    startActivity(Intent(this, AllBooksView::class.java))
                    true
                }
                R.id.nav_edit -> true
                else -> false
            }
        }

        loadAppStats()
    }

    override fun onResume() {
        super.onResume()
        loadAppStats()
    }

    private fun loadAppStats() {
        val totalShops = AppData.bookshops.findAll().size
        val totalBooks = AppData.bookshops.findAll().flatMap {
            AppData.books.findAllByBookshop(it.id)
        }.size

        totalShopsText.text = "Total Bookshops: $totalShops"
        totalBooksText.text = "Total Books Tracked: $totalBooks"
    }
}