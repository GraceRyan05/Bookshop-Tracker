package org.setu.bookshoptracker.models

data class BookshopModel(
    var id: Long = 0L,
    var name: String = "",
    var address: String = "",
    var description: String = "",
    var sellsNewBooks: Boolean = true,
    var sellsSecondHandBooks: Boolean = false,
    var openingHours: String = ""
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