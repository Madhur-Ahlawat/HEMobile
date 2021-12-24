package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityCreateAccountBinding

class ActivityCreateAccount : AppCompatActivity() {

    private lateinit var dataBinding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_account)
    }
}