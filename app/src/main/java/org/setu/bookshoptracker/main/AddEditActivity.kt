package org.setu.bookshoptracker.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R
import timber.log.Timber

class AddEditActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var openingHoursInput: EditText
    private lateinit var ratingInput: EditText

    private lateinit var newBooksCheckbox: CheckBox
    private lateinit var usedBooksCheckbox: CheckBox

    private lateinit var bookshopImageView: ImageView
    private lateinit var selectImageButton: Button

    private var selectedImageUri: Uri? = null
    private var selectedLatitude: Double = 52.245954
    private var selectedLongitude: Double = -7.139539

    private var editingId: Long? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                }

                selectedImageUri = uri
                bookshopImageView.setImageURI(uri)
                Timber.i("Image selected: $uri")
            }
        }

    private val mapPicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                selectedLatitude = data.getDoubleExtra("latitude", selectedLatitude)
                selectedLongitude = data.getDoubleExtra("longitude", selectedLongitude)
                Toast.makeText(
                    this,
                    "Location updated: $selectedLatitude, $selectedLongitude",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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

        bookshopImageView = findViewById(R.id.bookshopImageView)
        selectImageButton = findViewById(R.id.selectImageButton)

        selectImageButton.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        findViewById<Button>(R.id.buttonLoc).setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            intent.putExtra("latitude", selectedLatitude)
            intent.putExtra("longitude", selectedLongitude)
            intent.putExtra("title", nameInput.text.toString().ifEmpty { "Bookshop Location" })
            mapPicker.launch(intent)
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L).takeIf { it != -1L }

        val formTitle = findViewById<TextView>(R.id.formTitle)
        if (editingId != null) {
            formTitle.text = "Edit Bookshop"
            Timber.i("AddEditActivity opened in Edit mode for shop id=$editingId")
            loadExistingBookshop(editingId!!)
        } else {
            formTitle.text = "Add Bookshop"
            Timber.i("AddEditActivity opened in Add mode")
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
                R.id.nav_books -> {
                    startActivity(Intent(this, AllBooksActivity::class.java))
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

    private fun loadExistingBookshop(id: Long) {
        val bookshop = AppData.bookshops.findOne(id)

        if (bookshop == null) {
            Timber.w("Bookshop with id=$id not found for editing")
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

        selectedLatitude = bookshop.lat
        selectedLongitude = bookshop.lng

        selectedImageUri = bookshop.imageUri?.let { Uri.parse(it) }
        if (selectedImageUri != null) {
            bookshopImageView.setImageURI(selectedImageUri)
        } else {
            bookshopImageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun saveBookshop() {
        val name = nameInput.text.toString().trim()
        val address = addressInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val openingHours = openingHoursInput.text.toString().trim()
        val rating = ratingInput.text.toString().toDoubleOrNull() ?: 0.0

        try {
            if (editingId == null) {
                val bookshop = Bookshop(
                    name = name,
                    address = address,
                    description = description,
                    sellsNewBooks = newBooksCheckbox.isChecked,
                    sellsUsedBooks = usedBooksCheckbox.isChecked,
                    openingHours = openingHours,
                    rating = rating,
                    imageUri = selectedImageUri?.toString(),
                    lat = selectedLatitude,
                    lng = selectedLongitude
                )
                AppData.bookshops.create(bookshop)
                Timber.i("Bookshop created: name=$name")
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
                    rating = rating,
                    imageUri = selectedImageUri?.toString(),
                    lat = selectedLatitude,
                    lng = selectedLongitude
                )
                AppData.bookshops.update(bookshop)
                Timber.i("Bookshop updated: id=$editingId, name=$name")
                Toast.makeText(this, "Bookshop updated", Toast.LENGTH_SHORT).show()
            }

            finish()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message ?: "Please check entered values", Toast.LENGTH_LONG).show()
        }
    }
}