package com.heandroid.ui.startNow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.heandroid.ui.base.BaseActivity
import com.heandroid.ui.login.LoginActivity
import com.heandroid.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartNowBaseActivity : BaseActivity() {

    @Inject
    lateinit var sessionManager: SessionManager


    override fun observeViewModel() {

    }

    override fun initViewBinding() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}