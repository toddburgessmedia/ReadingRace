package com.toddburgessmedia.mycameraapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.otaliastudios.cameraview.CameraListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.camera_layout.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CameraFragment : Fragment() {

    val viewModel : CameraViewModel by sharedViewModel()

    companion object {

        fun newInstance() : CameraFragment {
            val cameraFragment = CameraFragment()
            return cameraFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.camera_layout,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraView.setLifecycleOwner(this)
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                val bitmap = jpeg?.size?.let { BitmapFactory.decodeByteArray(jpeg, 0, it) }
                bitmap?.let { viewModel.getBarCode(it) }
            }

        })

        frag_fab.show()
        frag_fab.setImageResource(R.drawable.ic_camera_white_24dp)
        frag_fab.setOnClickListener { view ->
            cameraView.captureSnapshot()
        }

    }

    override fun onPause() {
        super.onPause()

        frag_fab?.hide()
    }
}