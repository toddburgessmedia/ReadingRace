package com.toddburgessmedia.mycameraapp

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.toddburgessmedia.mycameraapp.model.Book
import kotlinx.android.synthetic.main.booklist_adapter.view.*

class BookListAdapter(val booklist : List<Book>) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    var books = mutableListOf<Book>()

    init {
        books.addAll(booklist)
        Log.d("mycamera", "${books.toString()}")

    }

    override fun onBindViewHolder(holder: BookViewHolder, index: Int) {

        val item = books[index].items[0]
        holder.author.setText(item.volumeInfo?.title)
        holder.title.setText(item.volumeInfo?.authors?.get(0))
        holder.pagecount.setText(item.volumeInfo?.pageCount?.toString())

        Picasso
            .get()
            .load(item.volumeInfo?.imageLinks?.thumbnail)
            .into(holder.image)

        Log.d("mycamera","binding view")

    }



    override fun getItemCount(): Int = books.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BookViewHolder {

        return BookViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.booklist_adapter,parent,false))

    }

    class BookViewHolder(v : View) : RecyclerView.ViewHolder(v) {

        private var view = v

        val title  = v.booklist_title
        val author = v.booklist_author
        val pagecount = v.booklist_pages
        val image = v.booklist_cover


    }
}