package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityEditProfileBinding
import com.heandroid.databinding.ActivityProfileBinding
import kotlinx.android.synthetic.main.tool_bar_white.view.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        setUpViews()

    }

    private fun setUpViews() {

        dataBinding.toolBarLyt.tv_header.text = getString(R.string.str_account_management)

        dataBinding.saveBtn.setOnClickListener {

        }

        dataBinding.toolBarLyt.btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}