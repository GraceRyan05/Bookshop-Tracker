package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import timber.log.Timber

class BookshopListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookshopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookshop_list)

        Timber.i("BookshopListActivity created with ${AppData.bookshops.findAll().size} shops")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        recyclerView = findViewById(R.id.bookshopsRecyclerView)

        adapter = BookshopAdapter(
            bookshops = AppData.bookshops.findAll(),
            listener = object : BookshopListener {
                override fun onBookshopClick(bookshop: Bookshop) {
                    Timber.d("Editing bookshop id=${bookshop.id}, name=${bookshop.name}")
                    editBookshop(bookshop)
                }

                override fun onBookshopLongClick(bookshop: Bookshop): Boolean {
                    Timber.d("Long press delete requested for bookshop id=${bookshop.id}, name=${bookshop.name}")
                    confirmDeleteBookshop(bookshop)
                    return true
                }
            },
            onManageBooks = { bookshop -> manageBooks(bookshop) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val returnButton = findViewById<Button>(R.id.returnButton)
        returnButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
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
                R.id.nav_bookshops -> true
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

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateBookshops(AppData.bookshops.findAll())
        }
    }

    private fun manageBooks(bookshop: Bookshop) {
        Timber.d("Managing books for bookshop id=${bookshop.id}")
        val intent = Intent(this, BookTrackerActivity::class.java)
        intent.putExtra("bookshopId", bookshop.id)
        startActivity(intent)
    }

    private fun editBookshop(bookshop: Bookshop) {
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", bookshop.id)
        startActivity(intent)
    }

    private fun confirmDeleteBookshop(bookshop: Bookshop) {
        AlertDialog.Builder(this)
            .setTitle("Delete Bookshop")
            .setMessage("Are you sure you want to delete '${bookshop.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                Timber.d("User confirmed deletion of bookshop id=${bookshop.id}, name=${bookshop.name}")
                AppData.bookshops.delete(bookshop.id)
                adapter.updateBookshops(AppData.bookshops.findAll())
            }
            .setNegativeButton("Cancel") { _, _ ->
                Timber.d("User cancelled deletion of bookshop id=${bookshop.id}")
            }
            .show()
    }
}