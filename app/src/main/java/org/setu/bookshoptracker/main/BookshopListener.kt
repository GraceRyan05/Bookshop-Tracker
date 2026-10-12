package org.setu.bookshoptracker.main

interface BookshopListener {
    fun onBookshopClick(bookshop: Bookshop)
    fun onBookshopLongClick(bookshop: Bookshop): Boolean
}