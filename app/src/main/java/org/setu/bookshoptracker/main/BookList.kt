package org.setu.bookshoptracker.main

import java.io.File
import java.util.concurrent.atomic.AtomicLong
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber

enum class ReadFilter { ALL, READ_ONLY, UNREAD_ONLY }

class BookList(private val file: File? = null) {
    private val books = ArrayList<Book>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    private fun load() {
        if (file == null || !file.exists()) return

        val type = object : TypeToken<List<Book>>() {}.type

        val loaded: ArrayList<Book> = gson.fromJson(file.readText(), type) ?: arrayListOf()

        books.clear()
        books.addAll(loaded)

        lastId.set(books.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        if (file == null) return
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(books))
    }

    fun findAllByBookshop(bookshopId: Long): List<Book> {
        return books.filter { it.bookshopId == bookshopId }
    }

    fun create(book: Book) {
        book.id = lastId.incrementAndGet()
        books.add(book)
        Timber.i("Book created in store: id=${book.id}, title=${book.title}")
        save()
    }

    fun update(book: Book): Boolean {
        val found = findOne(book.id)
        return if (found != null) {
            found.title = book.title
            found.author = book.author
            found.isRead = book.isRead
            Timber.i("Book updated in store: id=${book.id}, title=${book.title}")
            save()
            true
        } else {
            false
        }
    }

    fun delete(id: Long): Boolean {
        val found = findOne(id)
        return if (found != null) {
            books.remove(found)
            Timber.i("Book deleted from store: id=$id, title=${found.title}")
            save()
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