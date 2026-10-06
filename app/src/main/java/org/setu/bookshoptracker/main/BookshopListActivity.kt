package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class BookshopListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookshopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bookshop_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView =
            findViewById(R.id.bookshopsRecyclerView)

        adapter = BookshopAdapter(
            bookshops = AppData.bookshops.findAll(),

            onEdit = { bookshop ->
                editBookshop(bookshop)
            },

            onDelete = { bookshop ->
                deleteBookshop(bookshop)
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {

            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {

            adapter.updateBookshops(
                AppData.bookshops.findAll()
            )
        }
    }

    private fun editBookshop(bookshop: Bookshop) {

        val intent =
            Intent(this, AddEditActivity::class.java)

        intent.putExtra("id", bookshop.id)

        startActivity(intent)
    }

    private fun deleteBookshop(bookshop: Bookshop) {

        AppData.bookshops.delete(bookshop.id)

        adapter.updateBookshops(
            AppData.bookshops.findAll()
        )
    }
}