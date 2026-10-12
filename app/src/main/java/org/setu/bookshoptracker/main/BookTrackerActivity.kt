package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class BookTrackerActivity : AppCompatActivity() {

    private var bookshopId: Long = -1L
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookAdapter
    private lateinit var searchBookView: SearchView

    private lateinit var titleInput: EditText
    private lateinit var authorInput: EditText
    private lateinit var readInput: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_tracker)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        bookshopId = intent.getLongExtra("bookshopId", -1L)
        val bookshop = AppData.bookshops.findOne(bookshopId)

        val shopNameTitle = findViewById<TextView>(R.id.shopNameTitle)
        if (bookshop != null) {
            shopNameTitle.text = "Books Bought at ${bookshop.name}"
        } else {
            shopNameTitle.text = "Books Bought"
        }

        titleInput = findViewById(R.id.bookTitleInput)
        authorInput = findViewById(R.id.bookAuthorInput)
        readInput = findViewById(R.id.bookReadInput)

        val addBookButton = findViewById<Button>(R.id.addBookButton)
        addBookButton.setOnClickListener {
            addBook()
        }

        searchBookView = findViewById(R.id.searchBookView)
        searchBookView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText.orEmpty())
                return true
            }
        })

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.booksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BookAdapter(
            books = AppData.books.findAllByBookshop(bookshopId),
            onReadToggle = { book ->
                AppData.books.update(book)
            },
            onDelete = { book ->
                AppData.books.delete(book.id)
                refreshList()
            }
        )

        recyclerView.adapter = adapter

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
                R.id.nav_edit -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun addBook() {
        val title = titleInput.text.toString().trim()
        val author = authorInput.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a book title", Toast.LENGTH_SHORT).show()
            return
        }

        val book = Book(
            bookshopId = bookshopId,
            title = title,
            author = author,
            isRead = readInput.isChecked
        )

        AppData.books.create(book)

        titleInput.text.clear()
        authorInput.text.clear()
        readInput.isChecked = false

        Toast.makeText(this, "Book added", Toast.LENGTH_SHORT).show()
        refreshList()
    }

    private fun refreshList() {
        val query = if (::searchBookView.isInitialized) searchBookView.query.toString() else ""
        performSearch(query)
    }

    private fun performSearch(query: String) {
        val filteredBooks = AppData.books.search(bookshopId = bookshopId, query = query)
        adapter.updateBooks(filteredBooks)
    }
}