package org.setu.bookshoptracker.main

import java.util.concurrent.atomic.AtomicLong

class BookshopList {

    private val bookshops = ArrayList<Bookshop>()
    private val lastId = AtomicLong(0L)

    fun findAll(): List<Bookshop> {
        return bookshops
    }

    fun create(bookshop: Bookshop) {
        bookshop.id = lastId.incrementAndGet()
        bookshops.add(bookshop)
    }

    fun update(bookshop: Bookshop): Boolean {
        val foundBookshop = findOne(bookshop.id)

        return if (foundBookshop != null) {
            foundBookshop.name = bookshop.name
            foundBookshop.address = bookshop.address
            foundBookshop.description = bookshop.description
            foundBookshop.sellsNewBooks = bookshop.sellsNewBooks
            foundBookshop.sellsUsedBooks = bookshop.sellsUsedBooks
            foundBookshop.openingHours = bookshop.openingHours
            foundBookshop.rating = bookshop.rating
            true
        } else {
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundBookshop = findOne(id)

        return if (foundBookshop != null) {
            bookshops.remove(foundBookshop)
            true
        } else {
            false
        }
    }

    fun findOne(id: Long): Bookshop? {
        return bookshops.find { b -> b.id == id }
    }
}