package com.heandroid.ui.base

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.utils.logout.LogoutListener
import com.heandroid.utils.logout.LogoutUtil

abstract class BaseActivity<T> : AppCompatActivity(), LogoutListener {

    abstract fun observeViewModel()
    protected abstract fun initViewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        observeViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        LogoutUtil.startLogoutTimer(this)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        LogoutUtil.startLogoutTimer(this)

    }

    override fun onLogout() {
       Log.e("logout","yes")
    }


}