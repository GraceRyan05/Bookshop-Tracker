package org.setu.bookshoptracker.main

import java.util.concurrent.atomic.AtomicLong
import timber.log.Timber
import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class BookshopList(private val file: File? = null) {

    private val bookshops = ArrayList<Bookshop>()
    private val lastId = AtomicLong(0L)

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    init {
        load()
    }

    private fun load() {
        if (file == null || !file.exists()) return

        val type = object : TypeToken<List<Bookshop>>() {}.type

        val loaded: ArrayList<Bookshop> = gson.fromJson(file.readText(), type) ?: arrayListOf()

        bookshops.clear()
        bookshops.addAll(loaded)

        lastId.set(bookshops.maxOfOrNull { it.id } ?: 0L)
    }

    private fun save() {
        if (file == null) return
        file.parentFile?.mkdirs()
        file.writeText(gson.toJson(bookshops))
    }

    fun findAll(): List<Bookshop> {
        return bookshops
    }

    fun create(bookshop: Bookshop) {
        bookshop.validate()
        bookshop.id = lastId.incrementAndGet()
        bookshops.add(bookshop)
        Timber.i("Bookshop created in store: id=${bookshop.id}, name=${bookshop.name}")
        save()
    }

    fun update(bookshop: Bookshop): Boolean {
        bookshop.validate()

        val foundBookshop = findOne(bookshop.id)

        return if (foundBookshop != null) {
            foundBookshop.name = bookshop.name
            foundBookshop.address = bookshop.address
            foundBookshop.description = bookshop.description
            foundBookshop.sellsNewBooks = bookshop.sellsNewBooks
            foundBookshop.sellsUsedBooks = bookshop.sellsUsedBooks
            foundBookshop.openingHours = bookshop.openingHours
            foundBookshop.rating = bookshop.rating
            foundBookshop.imageUri = bookshop.imageUri
            foundBookshop.lat = bookshop.lat
            foundBookshop.lng = bookshop.lng
            Timber.i("Bookshop updated in store: id=${bookshop.id}, name=${bookshop.name}")
            save()
            true
        } else {
            Timber.w("Failed to update bookshop: id=${bookshop.id} not found")
            false
        }
    }

    fun delete(id: Long): Boolean {
        val foundBookshop = findOne(id)

        return if (foundBookshop != null) {
            bookshops.remove(foundBookshop)
            Timber.i("Bookshop deleted from store: id=$id, name=${foundBookshop.name}")
            save()
            true
        } else {
            Timber.w("Failed to delete bookshop: id=$id not found")
            false
        }
    }

    fun findOne(id: Long): Bookshop? {
        return bookshops.find { b -> b.id == id }
    }

    fun search(
        query: String = "",
        sellsNewBooks: Boolean = false,
        sellsUsedBooks: Boolean = false,
        rating: Int = 0
    ): List<Bookshop> {
        return bookshops.filter { shop ->
            val matchesQuery = query.isEmpty() ||
                shop.name.contains(query, ignoreCase = true) ||
                shop.address.contains(query, ignoreCase = true) ||
                shop.description.contains(query, ignoreCase = true)

            val matchesNew = !sellsNewBooks || shop.sellsNewBooks
            val matchesUsed = !sellsUsedBooks || shop.sellsUsedBooks
            val matchesRating = rating == 0 || shop.rating >= rating

            matchesQuery && matchesNew && matchesUsed && matchesRating
        }
    }
}