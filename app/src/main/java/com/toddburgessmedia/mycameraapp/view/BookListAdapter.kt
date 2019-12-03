package com.toddburgessmedia.mycameraapp.view

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.toddburgessmedia.mycameraapp.R
import com.toddburgessmedia.mycameraapp.model.Book
import com.toddburgessmedia.mycameraapp.model.Item
import kotlinx.android.synthetic.main.booklist_adapter.view.*

class BookListAdapter(val booklist : List<Book>) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    var click : (View) -> Unit = { Log.d("mycamera", "blah!") }

    constructor(booklist: List<Book>, clickListener : (v : View) -> Unit) : this(booklist) {
        //click = clickListener
    }

    val books = mutableListOf<Book>()

    init {
        books.addAll(booklist)

    }

    override fun onBindViewHolder(holder: BookViewHolder, index: Int) {

        val item = books[index].items[0]
        holder.bind(item)
    }

    override fun getItemCount(): Int = books.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BookViewHolder =
            BookViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.booklist_adapter, parent, false))

    class BookViewHolder(v : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        private var view = v

        val title  = v.booklist_title
        val author = v.booklist_author
        val pagecount = v.booklist_pages
        val image = v.booklist_cover


        fun bind(item : Item) {
            author.setText(createAuthors(item.volumeInfo?.authors))
            title.setText(item.volumeInfo?.title)
            pagecount.setText("${item.volumeInfo?.pageCount?.toString()} pages")

            Picasso
                .get()
                .load(item.volumeInfo?.imageLinks?.thumbnail)
                .into(image)

            view.setOnClickListener { Log.d("mycamera","click") }
        }

        private fun createAuthors(authors : List<String>?) : String {

            when (authors?.size) {
                0 -> return "Unknown"
                1 -> return authors[0]
                else -> return "${authors?.get(0)} et. al"
            }


        }

    }
}