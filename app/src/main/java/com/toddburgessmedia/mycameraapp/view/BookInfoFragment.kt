package com.toddburgessmedia.mycameraapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.R
import kotlinx.android.synthetic.main.fragment_bookinfo.*
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

    }
}