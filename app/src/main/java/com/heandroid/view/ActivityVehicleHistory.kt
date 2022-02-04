package com.heandroid.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VrmHistoryHeaderAdapter
import com.heandroid.databinding.ActivityVehicleHistoryBinding
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class ActivityVehicleHistory : AppCompatActivity() {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel

    private lateinit var dataBinding: ActivityVehicleHistoryBinding

    private lateinit var mVehicleDetails: VehicleResponse


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_vehicle_history)
        setUp()
        setupViewModel()
    }

    private fun setUp() {
        mVehicleDetails =
            intent?.getSerializableExtra(Constants.DATA) as VehicleResponse
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_vehicle_history)

        dataBinding.vehicleDetailsTxt.setOnClickListener {

            dataBinding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            dataBinding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            dataBinding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            dataBinding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )

        }

        setBtnActivated()
        setAdapter()
        setClickEvents()

    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setClickEvents() {

        dataBinding.saveBtn.setOnClickListener {
            updateVehicleApiCall(mVehicleDetails)
        }

        dataBinding.idToolBarLyt.back_button.setOnClickListener {
//            onBackPressed()
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val TAG = "VehicleDetailFragment"

    private fun setAdapter() {

        val mList = ArrayList<VehicleTitleAndSub>()
        mList.clear()

        for (i in 0..7) {
            when (i) {

                0 -> {
                    val mem0 =
                        VehicleTitleAndSub("Registration Number", mVehicleDetails.plateInfo.number)
                    mList.add(mem0)

                }

                1 -> {

                    val mem0 =
                        VehicleTitleAndSub("Country marker", mVehicleDetails.plateInfo.country)
                    mList.add(mem0)
                }
                2 -> {
                    val mem1 = VehicleTitleAndSub("Make", mVehicleDetails.vehicleInfo.make)
                    mList.add(mem1)

                }
                3 -> {
                    val mem2 = VehicleTitleAndSub("Model", mVehicleDetails.vehicleInfo.model)
                    mList.add(mem2)
                }
                4 -> {
                    val mem2 = VehicleTitleAndSub("Colour", mVehicleDetails.vehicleInfo.color)
                    mList.add(mem2)
                }
                5 -> {
                    val mem2 =
                        VehicleTitleAndSub("Class", mVehicleDetails.vehicleInfo.vehicleClassDesc)
                    mList.add(mem2)

                }
                6 -> {
                    val mem2 = VehicleTitleAndSub(
                        "DateAdded",
                        mVehicleDetails.vehicleInfo.effectiveStartDate
                    )
                    mList.add(mem2)

                }

                7->{
                    val mem2 = VehicleTitleAndSub("Notes" , mVehicleDetails.plateInfo.vehicleComments)
                    mList.add(mem2)

                }

            }

        }

        Logg.logging(TAG, " mList  $mList ")


        val mAdapter = VrmHistoryHeaderAdapter(this)
        mAdapter.setList(mList)
        dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        dataBinding.recyclerViewHeader.setHasFixedSize(true)
        dataBinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setBtnActivated() {

        dataBinding.saveBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.saveBtn.setTextColor(ContextCompat.getColor(this, R.color.white))

    }

    private fun updateVehicleApiCall(details: VehicleResponse) {

        vehicleMgmtViewModel.updateVehicleApi(details);
        vehicleMgmtViewModel.updateVehicleApiVal.observe(this,
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (it.data!!.body() == null) {
                            var apiResponse = EmptyApiResponse(200, "Updated successfully.")
                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
                        }

                    }

                    Status.ERROR -> {
                        showToast(it.message)
                    }

                    Status.LOADING -> {
                        // show/hide loader
                        Log.d("UpdateApi: ", "Data loading")
                    }
                }
            })

    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}