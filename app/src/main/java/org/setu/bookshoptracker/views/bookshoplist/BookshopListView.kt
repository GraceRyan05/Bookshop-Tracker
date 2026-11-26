package org.setu.bookshoptracker.views.bookshoplist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R
import org.setu.bookshoptracker.adapters.BookshopAdapter
import org.setu.bookshoptracker.main.Bookshop
import org.setu.bookshoptracker.main.BookshopListener
import org.setu.bookshoptracker.views.allbooks.AllBooksView
import org.setu.bookshoptracker.views.bookshop.BookshopView
import org.setu.bookshoptracker.views.booktracker.BookTrackerView
import org.setu.bookshoptracker.views.mainmenu.MainMenuView
import org.setu.bookshoptracker.views.settings.SettingsView
import timber.log.Timber

class BookshopListView : AppCompatActivity(), BookshopListener {

    private lateinit var presenter: BookshopListPresenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookshopAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookshop_list)

        presenter = BookshopListPresenter(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        recyclerView = findViewById(R.id.bookshopsRecyclerView)

        adapter = BookshopAdapter(
            bookshops = AppData.bookshops.findAll(),
            listener = this,
            onManageBooks = { bookshop ->
                val intent = Intent(this, BookTrackerView::class.java)
                intent.putExtra("bookshopId", bookshop.id)
                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText.orEmpty())
                return true
            }
        })

        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            startActivity(Intent(this, MainMenuView::class.java))
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_bookshops

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainMenuView::class.java))
                    finish()
                    true
                }
                R.id.nav_bookshops -> true
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

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            val query = if (::searchView.isInitialized) searchView.query.toString() else ""
            performSearch(query)
        }
    }

    override fun onBookshopClick(bookshop: Bookshop) {
        Timber.d("Editing bookshop: id=${bookshop.id}")
        val intent = Intent(this, BookshopView::class.java)
        intent.putExtra("id", bookshop.id)
        startActivity(intent)
    }

    override fun onBookshopLongClick(bookshop: Bookshop): Boolean {
        confirmDeleteBookshop(bookshop)
        return true
    }

    private fun confirmDeleteBookshop(bookshop: Bookshop) {
        AlertDialog.Builder(this)
            .setTitle("Delete Bookshop")
            .setMessage("Are you sure you want to delete '${bookshop.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                AppData.bookshops.delete(bookshop.id)
                val query = searchView.query.toString()
                performSearch(query)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSearch(query: String) {
        val filteredShops = AppData.bookshops.search(query = query)
        adapter.updateBookshops(filteredShops)
    }
}