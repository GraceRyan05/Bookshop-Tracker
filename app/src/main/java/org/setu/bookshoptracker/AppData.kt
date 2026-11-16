package org.setu.bookshoptracker

import android.content.Context
import org.setu.bookshoptracker.main.BookList
import org.setu.bookshoptracker.main.BookshopList
import java.io.File

object AppData {

    var bookshops: BookshopList = BookshopList()
        private set

    var books: BookList = BookList()
        private set

    fun initialize(context: Context) {
        val shopFile = File(context.filesDir, "bookshops.json")
        bookshops = BookshopList(shopFile)

        val bookFile = File(context.filesDir, "books.json")
        books = BookList(bookFile)
    }
}