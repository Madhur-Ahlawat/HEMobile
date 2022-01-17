package com.heandroid.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivityCommunicationBinding

class CommunicationActivity : AppCompatActivity() {

    private lateinit var dataBinding:ActivityCommunicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_communication)
    }
}