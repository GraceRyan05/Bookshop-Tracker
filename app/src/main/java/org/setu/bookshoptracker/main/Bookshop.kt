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
    var imageUri: String? = null
) {
    fun validate() {
        require(name.isNotBlank()) {
            "Bookshop name must not be empty."
        }

        require(address.isNotBlank()) {
            "Bookshop address must not be empty."
        }
    }
}