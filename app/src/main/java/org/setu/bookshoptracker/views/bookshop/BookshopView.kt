package org.setu.bookshoptracker.views.bookshop

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R
import org.setu.bookshoptracker.main.Bookshop
import org.setu.bookshoptracker.views.allbooks.AllBooksView
import org.setu.bookshoptracker.views.bookshoplist.BookshopListView
import org.setu.bookshoptracker.views.mainmenu.MainMenuView
import org.setu.bookshoptracker.views.mappicker.MapPickerView
import org.setu.bookshoptracker.views.settings.SettingsView
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class BookshopView : AppCompatActivity() {

    private lateinit var presenter: BookshopPresenter
    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var openingHoursInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var newBooksCheckbox: CheckBox
    private lateinit var usedBooksCheckbox: CheckBox
    private lateinit var bookshopImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var cameraPhotoButton: Button
    private lateinit var selectTimeButton: Button
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

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                try {
                    val file = File(cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }
                    val uri = Uri.fromFile(file)
                    selectedImageUri = uri
                    bookshopImageView.setImageURI(uri)
                    Timber.i("Camera photo captured: $uri")
                } catch (e: Exception) {
                    Timber.e("Error saving camera photo: ${e.message}")
                }
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

        presenter = BookshopPresenter(this)

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
        selectTimeButton = findViewById(R.id.selectTimeButton)
        ratingBar = findViewById(R.id.ratingBar)

        bookshopImageView = findViewById(R.id.bookshopImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        cameraPhotoButton = findViewById(R.id.cameraPhotoButton)

        selectTimeButton.setOnClickListener {
            showTimePicker()
        }

        selectImageButton.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        cameraPhotoButton.setOnClickListener {
            cameraLauncher.launch(null)
        }

        findViewById<Button>(R.id.buttonLoc).setOnClickListener {
            val intent = Intent(this, MapPickerView::class.java)
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
            Timber.i("BookshopView opened in Edit mode for shop id=$editingId")
            loadExistingBookshop(editingId!!)
        } else {
            formTitle.text = "Add Bookshop"
            Timber.i("BookshopView opened in Add mode")
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
                    startActivity(Intent(this, MainMenuView::class.java))
                    finish()
                    true
                }
                R.id.nav_bookshops -> {
                    startActivity(Intent(this, BookshopListView::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true
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

    private fun showTimePicker() {
        val openPicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(9)
            .setMinute(0)
            .setTitleText("Select Opening Time")
            .build()

        openPicker.addOnPositiveButtonClickListener {
            val openHour = String.format("%02d:%02d", openPicker.hour, openPicker.minute)

            val closePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(18)
                .setMinute(0)
                .setTitleText("Select Closing Time")
                .build()

            closePicker.addOnPositiveButtonClickListener {
                val closeHour = String.format("%02d:%02d", closePicker.hour, closePicker.minute)
                openingHoursInput.setText("$openHour - $closeHour")
            }

            closePicker.show(supportFragmentManager, "CLOSE_TIME_PICKER")
        }

        openPicker.show(supportFragmentManager, "OPEN_TIME_PICKER")
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
        ratingBar.rating = bookshop.rating.toFloat()

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
        val rating = ratingBar.rating.toDouble()

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
                Timber.i("Bookshop created: name=$name, rating=$rating")
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
                Timber.i("Bookshop updated: id=$editingId, name=$name, rating=$rating")
                Toast.makeText(this, "Bookshop updated", Toast.LENGTH_SHORT).show()
            }

            finish()
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message ?: "Please check entered values", Toast.LENGTH_LONG).show()
        }
    }
}