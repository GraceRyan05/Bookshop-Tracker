package org.setu.bookshoptracker.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.setu.bookshoptracker.R

class BookAdapter(
    private var books: List<Book>,
    private val onReadToggle: (Book) -> Unit,
    private val onDelete: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitleText: TextView = itemView.findViewById(R.id.bookTitleText)
        val bookAuthorText: TextView = itemView.findViewById(R.id.bookAuthorText)
        val readCheckBox: CheckBox = itemView.findViewById(R.id.readCheckBox)
        val deleteBookButton: Button = itemView.findViewById(R.id.deleteBookButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.bookTitleText.text = book.title
        holder.bookAuthorText.text = if (book.author.isNotEmpty()) "Author: ${book.author}" else ""

        holder.readCheckBox.setOnCheckedChangeListener(null)
        holder.readCheckBox.isChecked = book.isRead

        holder.readCheckBox.setOnCheckedChangeListener { _, isChecked ->
            book.isRead = isChecked
            onReadToggle(book)
        }

        holder.deleteBookButton.setOnClickListener {
            onDelete(book)
        }
    }

    override fun getItemCount(): Int = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}