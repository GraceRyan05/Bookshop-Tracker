package org.setu.bookshoptracker.main

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.setu.bookshoptracker.AppData

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

        createUserInterface()

        editingId = intent.getLongExtra("id", -1L)

        if (editingId != -1L) {
            loadExistingBookshop(editingId!!)
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 100, 32, 32)
        }

        nameInput = EditText(this).apply {
            hint = "Name"
        }

        addressInput = EditText(this).apply {
            hint = "Address"
        }

        descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        newBooksCheckbox = CheckBox(this).apply {
            text = "Sells new books"
        }

        usedBooksCheckbox = CheckBox(this).apply {
            text = "Sells used books"
        }

        openingHoursInput = EditText(this).apply {
            hint = "Opening hours"
        }

        ratingInput = EditText(this).apply {
            hint = "Rating"
        }

        val saveButton = Button(this).apply {
            text = "Save"

            setOnClickListener {
                saveBookshop()
            }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"

            setOnClickListener {
                finish()
            }
        }

        root.addView(nameInput)
        root.addView(addressInput)
        root.addView(descriptionInput)
        root.addView(newBooksCheckbox)
        root.addView(usedBooksCheckbox)
        root.addView(openingHoursInput)
        root.addView(ratingInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    private fun loadExistingBookshop(id: Long) {

        val bookshop = AppData.bookshops.findOne(id)

        if (bookshop == null) {

            Toast.makeText(
                this,
                "Bookshop not found",
                Toast.LENGTH_SHORT
            ).show()

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

        val name = nameInput.text.toString()
        val address = addressInput.text.toString()
        val description = descriptionInput.text.toString()
        val openingHours = openingHoursInput.text.toString()

        val rating = ratingInput.text.toString().toDoubleOrNull() ?: 0.0

        if (editingId == null || editingId == -1L) {

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

            Toast.makeText(
                this,
                "Bookshop created",
                Toast.LENGTH_SHORT
            ).show()

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

            Toast.makeText(
                this,
                "Bookshop updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}