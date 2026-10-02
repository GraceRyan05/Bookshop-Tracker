package org.setu.bookshoptracker.main

data class Bookshop (
    var id : Long = 0L,
    var name: String = "",
    var address: String = "",
    var description: String = "",
    var sellsNewBooks: Boolean = false,
    var sellsUsedBooks: Boolean = false,
    var openingHours: String = "",
    var rating: Double = 0.0
)