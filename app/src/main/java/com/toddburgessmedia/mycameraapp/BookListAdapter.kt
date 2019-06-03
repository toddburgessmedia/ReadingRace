package com.toddburgessmedia.mycameraapp

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddburgessmedia.mycameraapp.model.Book
import kotlinx.android.synthetic.main.booklist_adapter.view.*

class BookListAdapter(val booklist : List<Book>) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    var books = mutableListOf<Book>()

    init {
        books.addAll(booklist)

    }

    override fun onBindViewHolder(holder: BookViewHolder, index: Int) {

        val item = books[index].items[0]
        holder.author.setText(item.volumeInfo?.title)
        holder.title.setText(item.volumeInfo?.authors?.get(0))
        holder.pagecount.setText(item.volumeInfo?.pageCount?.toString())
    }



    override fun getItemCount(): Int = books.size

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookViewHolder {

        return BookViewHolder(LayoutInflater.from(p0.context)
                    .inflate(R.layout.booklist_adapter,p0,false))

    }

    class BookViewHolder(v : View) : RecyclerView.ViewHolder(v) {

        private var view = v

        val title  = v.booklist_title
        val author = v.booklist_author
        val pagecount = v.booklist_pages

    }
}