package com.toddburgessmedia.mycameraapp

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.Slide
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Gravity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.toddburgessmedia.mycameraapp.model.BookUpdate
import com.toddburgessmedia.mycameraapp.model.NewUser

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 99
    val cameraFragment = CameraFragment.newInstance()

    private val auth : FirebaseAuth by inject()
    val viewModel : CameraViewModel by viewModel()

    var user : FirebaseUser? = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        viewModel.bookObserver.observe(this, Observer<Book> { book ->
//
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_layout,BookListFragment.newInstance(book))
//                .commit()
//        })

        viewModel.bookUpdateObserver.observe(this, Observer<BookUpdate> { bookUpdate ->

            when(bookUpdate) {
                NewUser -> { startLogin()}
            }


        })

        if (user != null) {
            startLogin()
        } else {
            loginNewUser()
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onStart() {
        super.onStart()

//        if (user != null) {
//            startLogin()
//        } else {
//            loginNewUser()
//        }

    }

    private fun startLogin() {
        cameraFragment.exitTransition = Slide(Gravity.TOP)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, BookListFragment.newInstance(NewUser))
            .commit()
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

            if (!viewModel.userExists(user?.uid)) {
                registerUser()
            } else {
                startLogin()
            }
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

