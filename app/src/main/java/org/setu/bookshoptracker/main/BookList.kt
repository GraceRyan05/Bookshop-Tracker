package org.setu.bookshoptracker.main

import java.util.concurrent.atomic.AtomicLong

class BookList {
    private val books = ArrayList<Book>()
    private val lastId = AtomicLong(0L)

    fun findAllByBookshop(bookshopId: Long): List<Book> {
        return books.filter { it.bookshopId == bookshopId }
    }

    fun create(book: Book) {
        book.id = lastId.incrementAndGet()
        books.add(book)
    }

    fun update(book: Book): Boolean {
        val found = findOne(book.id)
        return if (found != null) {
            found.title = book.title
            found.author = book.author
            found.isRead = book.isRead
            true
        } else {
            false
        }
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id)
        return if (found != null) {
            books.remove(found)
            true
        } else {
            false
        }
    }

    fun findOne(id: Long): Book? {
        return books.find { it.id == id }
    }
}