package com.toddburgessmedia.mycameraapp

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.toddburgessmedia.mycameraapp.model.Book

class BookListAdapter(val books : List<Book>) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {



    override fun onBindViewHolder(p0: BookViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BookViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class BookViewHolder(v : View) : RecyclerView.ViewHolder(v) {

        private var view = v


    }
}