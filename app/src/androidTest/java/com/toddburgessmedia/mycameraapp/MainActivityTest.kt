package com.toddburgessmedia.mycameraapp

import android.content.Intent
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnitRunner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

//    @get:Rule
//    var intentsRule : IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @get:Rule
    var testRule : ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java,false,false)

    @Test
    fun checkNewUser() {

//        intended(toPackage("com.toddburgessmedia.hell"))
        withText("Sign In")

    }

}