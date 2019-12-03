package com.toddburgessmedia.mycameraapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.R
import com.toddburgessmedia.mycameraapp.model.*
import kotlinx.android.synthetic.main.fragment_booklist.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BookListFragment : Fragment() {

    val args : BookListFragmentArgs by navArgs()

    val viewModel : CameraViewModel by sharedViewModel()

    val clickListener : (View) -> Unit = { Log.d("mycamera","we just clicked!!!!") }

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

        addBookstoList(getBookUpdate(savedInstanceState))

        booklist_fab.setOnClickListener {
            viewModel.takePicture()
        }
    }

    private fun getBookUpdate (savedInstanceState : Bundle?) : BookUpdate {

        var bookUpdate: BookUpdate? = null

            bookUpdate = args.booklist

        return bookUpdate

    }

    private fun addBookstoList(bookUpdate : BookUpdate) {

        when (bookUpdate) {
            is NewUser -> {
                booklist_rv.visibility = View.GONE
                booklist_text.visibility = View.VISIBLE
            }
            is ReadingUpdate -> {
                booklist_rv.visibility = View.VISIBLE
                booklist_text.visibility = View.GONE

                val adapter = BookListAdapter(bookUpdate.libraryList,clickListener)
                booklist_rv.layoutManager = LinearLayoutManager(activity)
                booklist_rv.adapter = adapter

            }
        }
    }
}