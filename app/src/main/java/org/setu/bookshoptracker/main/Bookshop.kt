package org.setu.bookshoptracker.main

data class Bookshop (
    var id : Long = 0L,
    var name: String = "",
    var address: String = "",
    var description: String = "",
    var sellsNewBooks: Boolean = false,
    var sellsUsedBooks: Boolean = false,
    var openingHours: String = "",
    var rating: Double = 0.0,
    var imageUri: String? = null,
    var lat: Double = 52.245954,
    var lng: Double = -7.139539
) {
    fun validate() {
        require(name.isNotBlank()) {
            "Bookshop name must not be empty."
        }

        require(address.isNotBlank()) {
            "Bookshop address must not be empty."
        }

        require(lat.isFinite() && lat in -90.0..90.0) {
            "Latitude must be between -90 and 90."
        }

        require(lng.isFinite() && lng in -180.0..180.0) {
            "Longitude must be between -180 and 180."
        }
    }
}