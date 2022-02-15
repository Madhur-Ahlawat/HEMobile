package com.heandroid.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.heandroid.R
import com.heandroid.databinding.ActivityCrossingHistoryBinding
import com.heandroid.model.EmptyApiResponse
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Resource
import com.heandroid.repo.Status
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory

class CrossingHistoryActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityCrossingHistoryBinding
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityCrossingHistoryBinding.inflate(layoutInflater)
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