package com.toddburgessmedia.mycameraapp

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.squareup.picasso.Picasso
import com.toddburgessmedia.mycameraapp.model.Book
import com.toddburgessmedia.mycameraapp.model.BookUpdate
import com.toddburgessmedia.mycameraapp.model.NewUser
import kotlinx.android.synthetic.main.fragment_booklist.*
import java.io.Serializable

class BookListFragment : Fragment() {

    companion object {

        fun newInstance(bookUpdate: BookUpdate) : BookListFragment {

            val bundle = Bundle()
            bundle.putParcelable("bookupdate",bookUpdate)
            val bookListFragment = BookListFragment()
            bookListFragment.arguments = bundle
            return bookListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_booklist,container,false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val bookUpdate = arguments?.getParcelable("bookupdate") as BookUpdate

        when (bookUpdate) {
            NewUser -> {
                booklist_rv.visibility = View.GONE
                booklist_text.visibility = View.VISIBLE
            }

        }


//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(activity))

//
//
//        val book = bundle?.getParcelable("book") as Book?
//
//        val item = book!!.items[0]
//        val cover = item.volumeInfo?.imageLinks?.thumbnail
//        val author = item.volumeInfo?.authors?.get(0)

//        val imageLoader = ImageLoader.getInstance()
//
//        imageLoader.loadImage(cover, object : SimpleImageLoadingListener() {
//            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
//                list_cover.setImageBitmap(loadedImage)
//            }
//        })
//
//        list_title.text = item.volumeInfo?.title
//        list_author.text = author
//        list_pages.text = "${item.volumeInfo?.pageCount} pages"


    }
}