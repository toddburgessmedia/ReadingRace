package com.toddburgessmedia.mycameraapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddburgessmedia.mycameraapp.model.*
import kotlinx.android.synthetic.main.fragment_booklist.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BookListFragment : Fragment() {

    val viewModel : CameraViewModel by sharedViewModel()

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
            is NewUser -> {
                booklist_rv.visibility = View.GONE
                booklist_text.visibility = View.VISIBLE
            }
            is ReadingUpdate -> {
                booklist_rv.visibility = View.VISIBLE
                booklist_text.visibility = View.GONE

                val adapter = BookListAdapter(bookUpdate.libraryList)
                booklist_rv.apply {
                    layoutManager = LinearLayoutManager(activity)
                    booklist_rv.adapter = adapter
                }
            }
        }

        booklist_fab.setOnClickListener {
            viewModel.takePicture()
        }
    }
}