package com.toddburgessmedia.mycameraapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.R
import kotlinx.android.synthetic.main.fragment_bookinfo.*
import kotlinx.coroutines.processNextEventInCurrentThread
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BookInfoFragment : Fragment() {

    val args : BookInfoFragmentArgs by navArgs()

    val viewModel : CameraViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bookinfo,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.book.item
        bookinfo_title.text = item.volumeInfo?.title
        item.volumeInfo?.subtitle?.let {
            bookinfo_subtitle.text = it
            bookinfo_subtitle.visibility = View.VISIBLE
        }
        bookinfo_authors.text = createAuthors(item.volumeInfo?.authors)
        bookinfo_published.text = item.volumeInfo?.publishedDate
        bookinfo_pages.text = "${item.volumeInfo?.pageCount} pages"

        Picasso
            .get()
            .load(item.volumeInfo?.imageLinks?.thumbnail)
            .into(bookinfo_cover)

        bookinfo_description.text = item.volumeInfo?.description

    }



    private fun createAuthors(authors : List<String>?) : String {

        var authorList : String = "by: "

        authors?.let {
            for (author in authors) {
                authorList += "$author, "
            }
        }
        //return authorList.substring(0,authorList.length-2)
        return authorList.trim(',')
    }
}