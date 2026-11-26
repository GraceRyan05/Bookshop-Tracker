package org.setu.bookshoptracker.views.mainmenu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R
import org.setu.bookshoptracker.views.allbooks.AllBooksView
import org.setu.bookshoptracker.views.bookshop.BookshopView
import org.setu.bookshoptracker.views.bookshoplist.BookshopListView
import org.setu.bookshoptracker.views.maplist.MapListView
import org.setu.bookshoptracker.views.settings.SettingsView
import timber.log.Timber

class MainMenuView : AppCompatActivity() {

    private lateinit var presenter: MainMenuPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("Bookshop Tracker MainMenuView started")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        presenter = MainMenuPresenter(this)
        AppData.initialize(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.drawer_home -> true
                R.id.drawer_bookshops -> {
                    startActivity(Intent(this, BookshopListView::class.java))
                    true
                }
                R.id.drawer_map -> {
                    startActivity(Intent(this, MapListView::class.java))
                    true
                }
                R.id.drawer_add -> {
                    startActivity(Intent(this, BookshopView::class.java))
                    true
                }
                R.id.drawer_books -> {
                    startActivity(Intent(this, AllBooksView::class.java))
                    true
                }
                R.id.drawer_settings -> {
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }

        val viewBookshopsButton = findViewById<Button>(R.id.viewBookshopsButton)
        val viewMapButton = findViewById<Button>(R.id.viewMapButton)
        val viewAllBooksButton = findViewById<Button>(R.id.viewAllBooksButton)
        val addBookshopButton = findViewById<Button>(R.id.addBookshopButton)

        viewBookshopsButton.setOnClickListener {
            Timber.d("View Bookshops button clicked")
            startActivity(Intent(this, BookshopListView::class.java))
        }

        viewMapButton.setOnClickListener {
            Timber.d("View Map button clicked")
            startActivity(Intent(this, MapListView::class.java))
        }

        viewAllBooksButton.setOnClickListener {
            Timber.d("View All Books button clicked")
            startActivity(Intent(this, AllBooksView::class.java))
        }

        addBookshopButton.setOnClickListener {
            Timber.d("Add Bookshop button clicked")
            startActivity(Intent(this, BookshopView::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_bookshops -> {
                    startActivity(Intent(this, BookshopListView::class.java))
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
                R.id.nav_edit -> {
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }
    }
}