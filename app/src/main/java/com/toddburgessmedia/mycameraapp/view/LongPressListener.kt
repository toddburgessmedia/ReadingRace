package com.toddburgessmedia.mycameraapp.view

import android.util.Log
import android.view.View

class LongPressListener : View.OnLongClickListener {

    override fun onLongClick(v: View?): Boolean {
        Log.d("mycamera","long press activiated")
        return true
    }
}