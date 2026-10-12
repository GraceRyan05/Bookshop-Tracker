package org.setu.bookshoptracker.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.setu.bookshoptracker.AppData
import org.setu.bookshoptracker.R
import timber.log.Timber

class BookshopAdapter(
    private var bookshops: List<Bookshop>,
    private val listener: BookshopListener,
    private val onManageBooks: (Bookshop) -> Unit
) : RecyclerView.Adapter<BookshopAdapter.BookshopViewHolder>() {

    class BookshopViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val addressText: TextView =
            itemView.findViewById(R.id.addressText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val sellsNewBooksText: TextView =
            itemView.findViewById(R.id.sellsNewBooksText)

        val sellsUsedBooksText: TextView =
            itemView.findViewById(R.id.sellsUsedBooksText)

        val openingHoursText: TextView =
            itemView.findViewById(R.id.openingHoursText)

        val ratingText: TextView =
            itemView.findViewById(R.id.ratingText)

        val booksCountText: TextView =
            itemView.findViewById(R.id.booksCountText)

        val booksButton: Button =
            itemView.findViewById(R.id.booksButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookshopViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_bookshops,
                parent,
                false
            )

        return BookshopViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: BookshopViewHolder,
        position: Int
    ) {

        val bookshop = bookshops[position]

        holder.titleText.text =
            "${bookshop.id}: ${bookshop.name}"

        holder.addressText.text =
            "Address: ${bookshop.address}"

        holder.descriptionText.text =
            "Description: ${bookshop.description}"

        holder.sellsNewBooksText.text =
            "Sells new books: ${if (bookshop.sellsNewBooks) "Yes" else "No"}"

        holder.sellsUsedBooksText.text =
            "Sells second hand books: ${if (bookshop.sellsUsedBooks) "Yes" else "No"}"

        holder.openingHoursText.text =
            "Opening hours: ${bookshop.openingHours}"

        holder.ratingText.text =
            "Rating: ${bookshop.rating}"

        val shopBooks = AppData.books.findAllByBookshop(bookshop.id)
        val readCount = shopBooks.count { it.isRead }
        holder.booksCountText.text =
            "Books bought: ${shopBooks.size} (${readCount} read)"

        holder.itemView.setOnClickListener {
            Timber.d("Bookshop clicked: id=${bookshop.id}, name=${bookshop.name}")
            listener.onBookshopClick(bookshop)
        }

        holder.itemView.setOnLongClickListener {
            Timber.d("Bookshop long clicked: id=${bookshop.id}, name=${bookshop.name}")
            listener.onBookshopLongClick(bookshop)
        }

        holder.booksButton.setOnClickListener {
            onManageBooks(bookshop)
        }
    }

    override fun getItemCount(): Int {
        return bookshops.size
    }

    fun updateBookshops(newBookshops: List<Bookshop>) {
        bookshops = newBookshops
        notifyDataSetChanged()
    }
}