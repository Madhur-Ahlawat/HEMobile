package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.ActivitySingleMutlipleVrmBinding

class ActivitySingleOrMultipleVrms:AppCompatActivity() {

    private lateinit var databinding: ActivitySingleMutlipleVrmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_single_mutliple_vrm)
    }
}