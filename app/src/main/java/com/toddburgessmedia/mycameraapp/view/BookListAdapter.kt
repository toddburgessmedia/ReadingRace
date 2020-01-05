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

class BookListAdapter(val booklist : List<Book>,val clickListener: (Item) -> Unit) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>() {

    var books = mutableListOf<Book>()

    var position = 0

    init {
        books.addAll(booklist)

    }

    override fun onBindViewHolder(holder: BookViewHolder, index: Int) {

        val item = books[index].items[0]
        holder.bind(item,clickListener)
        holder.itemView.setOnLongClickListener {
            position = holder.adapterPosition
            return@setOnLongClickListener false
        }

    }

    override fun getItemCount(): Int = books.size

    fun updateBookList(newBooks : List<Book>) {
        books.removeAll{e -> true}
        books.addAll(newBooks)

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BookViewHolder =
            BookViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.booklist_adapter, parent, false))



    class BookViewHolder(v : View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        private var view = v

        val title  = v.booklist_title
        val author = v.booklist_author
        val pagecount = v.booklist_pages
        val image = v.booklist_cover

        fun bind(item : Item,clickListener: (Item) -> Unit) {
            author.setText(createAuthors(item.volumeInfo?.authors))
            title.setText(item.volumeInfo?.title)
            pagecount.setText("${item.volumeInfo?.pageCount?.toString()} pages")

            Picasso
                .get()
                .load(item.volumeInfo?.imageLinks?.thumbnail)
                .into(image)

            //val listener : (View, Item) -> { v,i -> Log.d("mycamera","click ${}") }
            view.setOnClickListener { clickListener(item) }
            view.isLongClickable = true
//            view.setOnLongClickListener {
//                Log.d("mycamera","long press")
//                return@setOnLongClickListener true
//            }

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