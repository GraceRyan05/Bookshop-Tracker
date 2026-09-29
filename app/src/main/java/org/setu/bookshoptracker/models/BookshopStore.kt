package org.setu.bookshoptracker.models

interface BookshopStore {

    fun findAll(): List<BookshopModel>

    fun create(bookshop: BookshopModel)

    fun update(bookshop: BookshopModel): Boolean

    fun delete(id: Long): Boolean

    fun findOne(id: Long): BookshopModel?
}