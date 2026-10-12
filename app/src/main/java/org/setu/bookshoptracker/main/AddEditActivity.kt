package org.setu.bookshoptracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R

class AddEditActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var openingHoursInput: EditText
    private lateinit var ratingInput: EditText

    private lateinit var newBooksCheckbox: CheckBox
    private lateinit var usedBooksCheckbox: CheckBox

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_edit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        nameInput = findViewById(R.id.nameInput)
        addressInput = findViewById(R.id.addressInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        newBooksCheckbox = findViewById(R.id.newBooksCheckbox)
        usedBooksCheckbox = findViewById(R.id.usedBooksCheckbox)
        openingHoursInput = findViewById(R.id.openingHoursInput)
        ratingInput = findViewById(R.id.ratingInput)

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }

        val formTitle = findViewById<TextView>(R.id.formTitle)
        if (editingId != null) {
            formTitle.text = "Edit Bookshop"
            loadExistingBookshop(editingId!!)
        } else {
            formTitle.text = "Add Bookshop"
        }

        saveButton.setOnClickListener {
            saveBookshop()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.nav_add

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
                R.id.nav_add -> true
                else -> false
            }
        }
    }

    private fun loadExistingBookshop(id: Long) {
        val bookshop = AppData.bookshops.findOne(id)

        if (bookshop == null) {
            Toast.makeText(this, "Bookshop not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        nameInput.setText(bookshop.name)
        addressInput.setText(bookshop.address)
        descriptionInput.setText(bookshop.description)

        newBooksCheckbox.isChecked = bookshop.sellsNewBooks
        usedBooksCheckbox.isChecked = bookshop.sellsUsedBooks

        openingHoursInput.setText(bookshop.openingHours)
        ratingInput.setText(bookshop.rating.toString())
    }

    private fun saveBookshop() {
        val name = nameInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val openingHours = openingHoursInput.text.toString().trim()
        val rating = ratingInput.text.toString().toDoubleOrNull() ?: 0.0

        if (name.isEmpty()) {
            nameInput.error = "Name is required"
            return
        }

        if (editingId == null) {
            val bookshop = Bookshop(
                name = name,
                address = address,
                description = description,
                sellsNewBooks = newBooksCheckbox.isChecked,
                sellsUsedBooks = usedBooksCheckbox.isChecked,
                openingHours = openingHours,
                rating = rating
            )
            AppData.bookshops.create(bookshop)
            Toast.makeText(this, "Bookshop created", Toast.LENGTH_SHORT).show()
        } else {
            val bookshop = Bookshop(
                id = editingId!!,
                name = name,
                address = address,
                description = description,
                sellsNewBooks = newBooksCheckbox.isChecked,
                sellsUsedBooks = usedBooksCheckbox.isChecked,
                openingHours = openingHours,
                rating = rating
            )
            AppData.bookshops.update(bookshop)
            Toast.makeText(this, "Bookshop updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}