package org.setu.bookshoptracker.main

import org.setu.bookshoptracker.models.BookshopMemStore
import org.setu.bookshoptracker.models.BookshopModel
import kotlin.text.isNotEmpty

val store = BookshopMemStore()

fun main() {
    println("=== Bookshop Tracker Console App ===")

    var input: Int

    do {
        input = menu()

        when (input) {
            1 -> addBookshop()
            2 -> listBookshops()
            3 -> updateBookshop()
            4 -> deleteBookshop()
            5 -> searchBookshop()
            0 -> println("\nExiting Bookshop Tracker. Goodbye!")
            else -> println("\nInvalid option. Please try again.")
        }

    } while (input != 0)
}

fun menu(): Int {
    println("\n----------------------------------")
    println(" BOOKSHOP TRACKER")
    println("----------------------------------")
    println(" 1. Add Bookshop")
    println(" 2. List All Bookshops")
    println(" 3. Update a Bookshop")
    println(" 4. Delete a Bookshop")
    println(" 5. Search Bookshop by ID")
    println(" 0. Exit")
    print("\nEnter option: ")

    return readlnOrNull()?.toIntOrNull() ?: -1
}


fun addBookshop() {
    println("\n=== Add Bookshop ===")
    print("Enter the name of the bookshop: ")
    val name = readlnOrNull()?.trim().orEmpty()
    print("Enter the address of the bookshop: ")
    val address = readlnOrNull()?.trim().orEmpty()
    print("Enter the description of the bookshop: ")
    val description = readlnOrNull()?.trim().orEmpty()
    print("Does the bookshop sell new books? (true/false): ")
    val sellsNewBooks = readlnOrNull()?.toBooleanStrictOrNull() ?: false

    if (name.isNotEmpty()) {
        val bookshop = BookshopModel(name = name, address = address, description = description, sellsNewBooks = sellsNewBooks)
        store.create(bookshop)
        println("\nBookshop added successfully!")
    } else {
        println("\nInvalid input. Bookshop not added.")
    }

}


fun listBookshops() {
    println("\n=== List All Bookshops ===")
    val bookshops = store.findAll()
    if (bookshops.isEmpty()) {
        println("No bookshops added yet.")
    }
    bookshops.forEach { println("ID: ${it.id}, Name: ${it.name}, Address: ${it.address}, Description: ${it.description}, Sells New Books: ${it.sellsNewBooks}") }
}


fun updateBookshop() {
    println("\n=== Update Bookshop ===")
    listBookshops()
    if (store.findAll().isEmpty()) return

    print("\nEnter ID of Bookshop to update: ")
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null && store.findOne(id) != null) {
        print("Enter New Name: ")
        val name = readlnOrNull()?.trim().orEmpty()
        print("Enter New Address: ")
        val address = readlnOrNull()?.trim().orEmpty()
        print("Enter New Description: ")
        val description = readlnOrNull()?.trim().orEmpty()
        print("Does the bookshop sell new books? (true/false): ")
        val sellsNewBooks = readlnOrNull()?.toBooleanStrictOrNull() ?: false

        if (name.isNotEmpty()) {
            val updated = store.update(BookshopModel(id = id, name = name, address = address, description = description, sellsNewBooks = sellsNewBooks))
            if (updated) println("\nBookshop updated successfully!")
            else println("\nName cannot be empty. Update cancelled.")
        } else {
            println("\nBookshop with ID $id not found.")

        }
    }

}


fun deleteBookshop() {
    println("\n=== Delete Bookshop ===")
    listBookshops()
    if (store.findAll().isEmpty()) return
    print("\nEnter ID of Bookshop to delete: ")
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null && store.findOne(id) != null) {
        val deleted = store.delete(id)
        if (deleted) println("\nBookshop deleted successfully!")
        else println("\nBookshop not deleted.")
    }
}

fun searchBookshop() {
    println("\n=== Search Bookshop ===")
    listBookshops()
    val id = readlnOrNull()?.toLongOrNull()

    if (id != null) {
        val bookshop = store.findOne(id)
        if (bookshop != null) {
            println("\n=== Bookshop Details ===")
            println("ID: ${bookshop.id}, Name: ${bookshop.name}, Address: ${bookshop.address}, Description: ${bookshop.description}, Sells New Books: ${bookshop.sellsNewBooks}")
        } else {
            println("\nBookshop not found.")
        }
    } else {
        println("\nInvalid input. Please enter a valid ID.")
    }
}

