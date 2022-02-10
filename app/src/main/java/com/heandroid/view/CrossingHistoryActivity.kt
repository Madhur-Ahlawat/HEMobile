package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.heandroid.R
import com.heandroid.databinding.ActivityCrossingHistoryBinding

class CrossingHistoryActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityCrossingHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding=ActivityCrossingHistoryBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        init()
        initCtrl()
    }

    private fun init() {
        // Toolbar
        dataBinding.toolBarLyt.apply {
            tvHeader.text = getString(R.string.crossing_history)
            btnBack.setOnClickListener { finish() }
        }
    }
    private fun initCtrl() {

    }
}