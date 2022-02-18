package com.heandroid.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.ActivityVehicleHistoryBinding
import com.heandroid.model.VehicleResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory


class ActivityVehicleHistory : AppCompatActivity(), OnEditTextValueChangedClickedListener {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel

    private lateinit var dataBinding: ActivityVehicleHistoryBinding

    private lateinit var mVehicleDetails: VehicleResponse

    private lateinit var navHost : NavController

    private var textChanged:Boolean = false

    private val TAG = "VehicleDetailFragment"


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
        val bundle = Bundle().apply {
            this.putSerializable(Constants.DATA, mVehicleDetails)
        }

        navHost = Navigation.findNavController(this, R.id.fragmentContainer)
        navHost.setGraph(R.navigation.vehicle_history_nav_graph, bundle)

        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        dataBinding.idToolBarLyt.tvHeader.text = getString(R.string.str_vehicle_history)

        dataBinding.idToolBarLyt.btnBack.setOnClickListener{
            finish()
        }

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

            navHost.navigate(R.id.vehicleHistoryVehicleDetailsFragment, bundle)
        }

        dataBinding.crossingHistoryTxt.setOnClickListener {

            dataBinding.crossingHistoryTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_selected_bg)
            dataBinding.vehicleDetailsTxt.background =
                ContextCompat.getDrawable(this, R.drawable.text_unselected_bg)
            dataBinding.crossingHistoryTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )

            dataBinding.vehicleDetailsTxt.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            navHost.navigate(R.id.vehicleHistoryCrossingHistoryFragment, bundle)

        }

//        dataBinding.backToVehiclesBtn.setOnClickListener {
//            finish()
//        }
//        setBtnActivated()
//        setAdapter()
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
//        dataBinding.saveBtn.setOnClickListener {
//            if(textChanged) {
//                updateVehicleApiCall(mVehicleDetails)
//            }
//        }
//        dataBinding.idToolBarLyt.btnBack.setOnClickListener {
////            onBackPressed()
//            finish()
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        finish()
    }


    //
//    private fun setAdapter() {
//
//        val mList = ArrayList<VehicleTitleAndSub>()
//        mList.clear()
//
//        for (i in 0..7) {
//            when (i) {
//
//                0 -> {
//                    val mem0 =
//                        VehicleTitleAndSub("Registration Number", mVehicleDetails.plateInfo.number)
//                    mList.add(mem0)
//
//                }
//
//                1 -> {
//
//                    val mem0 =
//                        VehicleTitleAndSub("Country marker", mVehicleDetails.plateInfo.country)
//                    mList.add(mem0)
//                }
//                2 -> {
//                    val mem1 = VehicleTitleAndSub("Make", mVehicleDetails.vehicleInfo.make)
//                    mList.add(mem1)
//
//                }
//                3 -> {
//                    val mem2 = VehicleTitleAndSub("Model", mVehicleDetails.vehicleInfo.model)
//                    mList.add(mem2)
//                }
//                4 -> {
//                    val mem2 = VehicleTitleAndSub("Colour", mVehicleDetails.vehicleInfo.color)
//                    mList.add(mem2)
//                }
//                5 -> {
//                    val mem2 =
//                        VehicleTitleAndSub("Class", mVehicleDetails.vehicleInfo.vehicleClassDesc)
//                    mList.add(mem2)
//
//                }
//                6 -> {
//                    val mem2 = VehicleTitleAndSub(
//                        "DateAdded",
//                        mVehicleDetails.vehicleInfo.effectiveStartDate
//                    )
//                    mList.add(mem2)
//
//                }
//
//                7 -> {
//                    val mem2 =
//                        VehicleTitleAndSub("Notes", mVehicleDetails.plateInfo.vehicleComments)
//                    mList.add(mem2)
//
//                }
//
//            }
//
//        }
//
//        Logg.logging(TAG, " mList  $mList ")
//        Logg.logging(TAG, " mList size ${mList.size} ")
//
//        val mAdapter = VrmHistoryHeaderAdapter(this , this)
//        mAdapter.setList(mList)
//        dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
//        dataBinding.recyclerViewHeader.setHasFixedSize(true)
//        dataBinding.recyclerViewHeader.adapter = mAdapter
//
//    }
//
//    private fun setBtnActivated() {
//
//        dataBinding.saveBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))
//
//        dataBinding.saveBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
//
//    }
//
//    private fun updateVehicleApiCall(details: VehicleResponse) {
//
//        var request = details.apply {
//            newPlateInfo = plateInfo
//        }
//        vehicleMgmtViewModel.updateVehicleApi(request);
//        dataBinding.progressLayout.visibility = VISIBLE
//
//        vehicleMgmtViewModel.updateVehicleApiVal.observe(this,
//            {
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        dataBinding.progressLayout.visibility = GONE
//                        if (it.data!!.body() == null) {
//                            var apiResponse = EmptyApiResponse(200, "Updated successfully.")
//                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
//
//                            showToast("Vehicle is updated successfully")
//                        }
//
//                    }
//
//                    Status.ERROR -> {
//                        dataBinding.progressLayout.visibility = GONE
//                        showToast(it.message)
//                    }
//
//                    Status.LOADING -> {
//                        // show/hide loader
//                        dataBinding.progressLayout.visibility = VISIBLE
//                        Log.d("UpdateApi: ", "Data loading")
//                    }
//                }
//            })
//
//    }
//
//    private fun showToast(message: String?) {
//        message?.let {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        }
//    }
//
    override fun OnEditTextValueChanged(value: String) {

//        if(!TextUtils.isEmpty(value))
//        {
//            hideKeyboard()
//            textChanged= true
//            mVehicleDetails.plateInfo.vehicleComments = value
//        }

    }


}

interface OnEditTextValueChangedClickedListener {

    fun OnEditTextValueChanged(value:String)

}
