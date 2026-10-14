package org.setu.bookshoptracker.main

import java.util.concurrent.atomic.AtomicLong

enum class ReadFilter { ALL, READ_ONLY, UNREAD_ONLY }

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

    fun search(
        bookshopId: Long,
        query: String = "",
        isReadFilter: Boolean? = null
    ): List<Book> {
        return books.filter { book ->
            val matchesShop = (book.bookshopId == bookshopId)
            val matchesQuery = query.isEmpty() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)
            val matchesRead = isReadFilter == null || book.isRead == isReadFilter

            matchesShop && matchesQuery && matchesRead
        }
    }

    fun findAllAndFilter(
        filter: ReadFilter = ReadFilter.ALL,
        query: String = ""
    ): List<Book> {
        return books.filter { book ->
            val matchesRead = when (filter) {
                ReadFilter.ALL -> true
                ReadFilter.READ_ONLY -> book.isRead
                ReadFilter.UNREAD_ONLY -> !book.isRead
            }
            val matchesQuery = query.isEmpty() ||
                book.title.contains(query, ignoreCase = true) ||
                book.author.contains(query, ignoreCase = true)

            matchesRead && matchesQuery
        }
    }
}