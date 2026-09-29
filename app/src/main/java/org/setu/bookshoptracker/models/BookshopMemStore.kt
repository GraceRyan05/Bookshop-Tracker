package org.setu.bookshoptracker.models

import java.util.concurrent.atomic.AtomicLong

class BookshopMemStore : BookshopStore {

    private val bookshops = ArrayList<BookshopModel>()
    private val lastId = AtomicLong(0L)

    override fun findAll(): List<BookshopModel> {
        return bookshops
    }

    override fun create(bookshop: BookshopModel) {
        bookshop.id = lastId.incrementAndGet()
        bookshops.add(bookshop)
    }

    override fun update(bookshop: BookshopModel): Boolean {
        val foundBookshop = findOne(bookshop.id)

        return if (foundBookshop != null) {
            foundBookshop.name = bookshop.name
            foundBookshop.address = bookshop.address
            foundBookshop.description = bookshop.description
            foundBookshop.sellsNewBooks = bookshop.sellsNewBooks
            foundBookshop.sellsSecondHandBooks = bookshop.sellsSecondHandBooks
            foundBookshop.openingHours = bookshop.openingHours

            true
        } else {
            false
        }
    }

    override fun delete(id: Long): Boolean {
        val foundBookshop = findOne(id)

        return if (foundBookshop != null) {
            bookshops.remove(foundBookshop)
            true
        } else {
            false
        }
    }

    override fun findOne(id: Long): BookshopModel? {
        return bookshops.find { bookshop -> bookshop.id == id }
    }
}