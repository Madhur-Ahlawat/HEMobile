package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.tool_bar_white.view.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        setUpViews()

    }

    private fun setUpViews() {

        dataBinding.toolBarLyt.tv_header.text = getString(R.string.str_account_management)

        dataBinding.toolBarLyt.btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        finish()
    }

}