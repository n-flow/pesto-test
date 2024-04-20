package com.parth.pestotest.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.parth.pestotest.network.firebase.FirebaseDataListen
import com.parth.pestotest.ui.dashboard.view.DashboardActivity
import com.parth.pestotest.ui.login.view.LoginActivity
import com.parth.pestotest.utils.extensions.email
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var firebaseDataListen: FirebaseDataListen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initScreen()

        setObserver()
    }

    private fun initScreen() {
        if (preferences.email.isNotEmpty()) {
            firebaseDataListen.signInUser()
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 500)
        }
    }

    private fun setObserver() {
        firebaseDataListen.user.observe(this) {
            it?.let {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
    }
}