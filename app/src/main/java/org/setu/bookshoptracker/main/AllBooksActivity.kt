package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class AllBooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private lateinit var searchBookView: SearchView
    private var currentFilter = ReadFilter.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_books)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        recyclerView = findViewById(R.id.allBooksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BookAdapter(
            books = AppData.books.findAllAndFilter(currentFilter),
            onReadToggle = { book ->
                AppData.books.update(book)
                refreshList()
            },
            onDelete = { book ->
                AppData.books.delete(book.id)
                refreshList()
            }
        )
        recyclerView.adapter = adapter

        val chipGroup = findViewById<ChipGroup>(R.id.readFilterChipGroup)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when (checkedIds.firstOrNull()) {
                R.id.chipRead -> ReadFilter.READ_ONLY
                R.id.chipUnread -> ReadFilter.UNREAD_ONLY
                else -> ReadFilter.ALL
            }
            refreshList()
        }

        searchBookView = findViewById(R.id.searchBookView)
        searchBookView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                refreshList()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                refreshList()
                return true
            }
        })

        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_books

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
                R.id.nav_books -> true
                R.id.nav_edit -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val query = if (::searchBookView.isInitialized) searchBookView.query.toString() else ""
        adapter.updateBooks(AppData.books.findAllAndFilter(currentFilter, query))
    }
}