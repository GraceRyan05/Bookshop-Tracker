package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.setu.bookshoptracker.AppData

class MainActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()
    }

    override fun onResume() {
        super.onResume()

        if (::listLayout.isInitialized) {
            displayBookshops()
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 48, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Bookshop Tracker"
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 16)
        }

        val addButton = Button(this).apply {
            text = "Add Bookshop"

            setOnClickListener {

                val intent = Intent(
                    this@MainActivity,
                    AddEditActivity::class.java
                )

                startActivity(intent)
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 16, 0, 16)
        }

        val scrollView = ScrollView(this).apply {
            addView(listLayout)
        }

        root.addView(title)
        root.addView(addButton)
        root.addView(scrollView)

        setContentView(root)

        displayBookshops()
    }

    private fun displayBookshops() {

        listLayout.removeAllViews()

        val bookshops = AppData.bookshops.findAll()

        if (bookshops.isEmpty()) {

            val emptyText = TextView(this).apply {
                text = "No bookshops yet."
                textSize = 18f
            }

            listLayout.addView(emptyText)

            return
        }

        for (bookshop in bookshops) {

            val bookshopLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 24, 0, 24)
            }

            val name = TextView(this).apply {
                text = "${bookshop.id}: ${bookshop.name}"
                textSize = 20f
            }

            val address = TextView(this).apply {
                text = "Address: ${bookshop.address}"
                textSize = 16f
            }

            val description = TextView(this).apply {
                text = "Description: ${bookshop.description}"
                textSize = 16f
            }

            val sellsNewBooks = TextView(this).apply {
                text = "Sells new books: ${if (bookshop.sellsNewBooks) "Yes" else "No"}"
                textSize = 16f
            }

            val sellsUsedBooks = TextView(this).apply {
                text = "Sells used books: ${if (bookshop.sellsUsedBooks) "Yes" else "No"}"
                textSize = 16f
            }

            val openingHours = TextView(this).apply {
                text = "Opening hours: ${bookshop.openingHours}"
                textSize = 16f
            }

            val rating = TextView(this).apply {
                text = "Rating: ${bookshop.rating}"
                textSize = 16f
            }

            val editButton = Button(this).apply {
                text = "Edit"

                setOnClickListener {

                    val intent = Intent(
                        this@MainActivity,
                        AddEditActivity::class.java
                    )

                    intent.putExtra("id", bookshop.id)

                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"

                setOnClickListener {

                    AppData.bookshops.delete(bookshop.id)

                    displayBookshops()
                }
            }

            bookshopLayout.addView(name)
            bookshopLayout.addView(address)
            bookshopLayout.addView(description)
            bookshopLayout.addView(sellsNewBooks)
            bookshopLayout.addView(sellsUsedBooks)
            bookshopLayout.addView(openingHours)
            bookshopLayout.addView(rating)
            bookshopLayout.addView(editButton)
            bookshopLayout.addView(deleteButton)

            listLayout.addView(bookshopLayout)
        }
    }
}