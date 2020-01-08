package com.toddburgessmedia.mycameraapp.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.R
import com.toddburgessmedia.mycameraapp.model.*
import kotlinx.android.synthetic.main.fragment_booklist.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BookListFragment : Fragment() {

    val args : BookListFragmentArgs by navArgs()

    val viewModel : CameraViewModel by sharedViewModel()

    lateinit var adapter : BookListAdapter

    val clickListener : (Item) -> Unit = {item -> viewModel.getBookDetail(item)}

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

        registerForContextMenu(booklist_rv)

        addBookstoList(getBookUpdate(savedInstanceState))

        booklist_fab.setOnClickListener {
            viewModel.takePicture()
        }

        viewModel.bookListFragmentObserver.observe(this, Observer<ReadingUpdate> { bookUpdate ->
            Log.d("mycamera", "bookDeleted")
            adapter.updateBookList(bookUpdate.libraryList)

        })
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

                adapter = BookListAdapter(bookUpdate.libraryList,clickListener)
                booklist_rv.layoutManager = LinearLayoutManager(activity)
                booklist_rv.adapter = adapter

            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val menuInflater = activity?.menuInflater
        menu.setHeaderTitle("Book Actions")
        menuInflater?.inflate(R.menu.booklist,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val builder = context?.let {
            AlertDialog.Builder(it)
        }
        builder?.setMessage("Delete ${adapter.booklist[adapter.position].items[0].volumeInfo?.title}")
        builder?.setTitle("Confirm Deletion")
        builder?.setPositiveButton("OK"){dialog, which ->  viewModel.deleteBook(adapter.booklist[adapter.position].items[0])}
        builder?.setNegativeButton("Cancel"){dialog, which ->  Log.d("mycamera","Cancel")}
        val dialog = builder?.create()
        dialog?.show()

        Log.d("mycamera","menu item slected is ${adapter.booklist[adapter.position].items[0].id}")
        return false
    }


}