package com.toddburgessmedia.mycameraapp

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log
import android.view.Gravity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.toddburgessmedia.mycameraapp.firebase.FCMManager
import com.toddburgessmedia.mycameraapp.model.*

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 99
    val cameraFragment = CameraFragment.newInstance()

    private val auth : FirebaseAuth by inject()
    val viewModel : CameraViewModel by viewModel()
    val fcmMessaging : FCMManager by inject()

    var user : FirebaseUser? = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel.bookUpdateObserver.observe(this, Observer<BookUpdate> { bookUpdate ->

            when (bookUpdate) {
                is NewUser -> startLogin(bookUpdate)
                is RegisterUser -> registerUser()
                is ReadingUpdate -> startLogin(bookUpdate)
            }
        })

        viewModel.cameraObserver.observe(this, Observer<CameraAction> {

            when (it) {
                is CameraStart -> { startCamera() }
            }
        })

        if (user != null) {
            viewModel.userExists(user?.uid)
        } else {
            loginNewUser()
        }
    }

    override fun onStart() {
        super.onStart()

        fcmMessaging.addAllSubcriptions()
    }

    private fun startCamera() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, cameraFragment)
            .commit()
    }

    private fun startLogin() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, BookListFragment.newInstance(NewUser))
            .commit()
    }

    private fun startLogin(bookUpdate: BookUpdate?) {

        bookUpdate?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, BookListFragment.newInstance(bookUpdate))
                .commit()
        }
    }

    fun loginNewUser() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == RC_SIGN_IN) && (resultCode == Activity.RESULT_OK)) {
            user = auth.currentUser

            viewModel.userExists(user?.uid)
        } else {
            Snackbar.make(
                findViewById(R.id.main_layout),
                "Unable to connect to Internet",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun registerUser() {

        val bundle = Bundle()
        bundle.putString("name", user?.displayName)
        bundle.putString("email", user?.email)
        bundle.putString("uid", user?.uid)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, LoginFragment.newInstance(bundle))
            .commit()
    }

}

