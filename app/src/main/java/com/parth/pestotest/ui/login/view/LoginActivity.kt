package com.parth.pestotest.ui.login.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.parth.pestotest.R
import com.parth.pestotest.core.googleLogin.GoogleLogin
import com.parth.pestotest.databinding.ActivityLoginBinding
import com.parth.pestotest.ui.BaseActivity
import com.parth.pestotest.ui.dashboard.view.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = this

        setObserver()
    }

    private fun setObserver() {
        firebaseDataListen.user.observe(this) {
            it?.let {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.googleLogin -> {
                GoogleLogin(this).signIn {
                    firebaseDataListen.registerUser(it)
                }
            }
        }
    }
}