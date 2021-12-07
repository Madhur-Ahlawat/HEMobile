package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.HomeActivityBinding

class ActivityHome : AppCompatActivity() {

    private lateinit var dataBinding: HomeActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.home_activity)

    }
}