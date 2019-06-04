package com.toddburgessmedia.mycameraapp

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.transition.Slide
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.toddburgessmedia.mycameraapp.model.Book
import com.toddburgessmedia.mycameraapp.model.BookUpdate
import com.toddburgessmedia.mycameraapp.model.NewUser

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 99
    val cameraFragment = CameraFragment.newInstance()

    val viewModel : CameraViewModel by viewModel()

    private lateinit var auth : FirebaseAuth

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
                NewUser -> { Log.d("mycamera","new user time")}
            }


        })

        auth = FirebaseAuth.getInstance()
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


        val currentUser = auth.currentUser
        Log.d("mycamera","user ${currentUser?.displayName}")
        currentUser?.displayName?.let {
            cameraFragment.exitTransition = Slide(Gravity.TOP)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_layout, BookListFragment.newInstance(NewUser))
                .commit()
            Log.d("mycamera", "we are logged in")
        } ?:
           loginUser()

    }

    fun loginUser() {

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build(),
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

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {

                val user = FirebaseAuth.getInstance().currentUser

                viewModel.userExists(user?.uid)

                val bundle = Bundle()
                bundle.putString("name",user?.displayName)
                bundle.putString("email",user?.email)
                bundle.putString("uid",user?.uid)

                Log.d("mycamera", "user is ${user?.displayName}")
                cameraFragment.exitTransition = Slide(Gravity.TOP)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout, LoginFragment.newInstance(bundle))
                    .commit()
                Log.d("mycamera", "we logged in")
            } else {
                Log.d("mycamera","we could not login")
            }
        }
    }
}

