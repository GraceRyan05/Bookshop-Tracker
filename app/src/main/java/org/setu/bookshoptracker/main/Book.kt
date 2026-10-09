package org.setu.bookshoptracker.main

data class Book(
    var id: Long = 0L,
    var bookshopId: Long = 0L,
    var title: String = "",
    var author: String = "",
    var isRead: Boolean = false
)