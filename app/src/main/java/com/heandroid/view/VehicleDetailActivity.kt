package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VrmHeaderAdapter
import com.heandroid.databinding.FragmentVehicleDetailBinding
import com.heandroid.model.PlateInfoResponse
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleDetailActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var mAdapter: VrmHeaderAdapter

    private lateinit var dataBinding: FragmentVehicleDetailBinding

    private lateinit var mVehicleDetails: VehicleResponse

    private var mScreeType = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_vehicle_detail)
        setUp()
        setupViewModel()
    }

    private fun setUp() {
        mVehicleDetails =
            intent?.getSerializableExtra(Constants.DATA) as VehicleResponse
        mScreeType = intent?.getIntExtra(Constants.VEHICLE_SCREEN_KEY, 0)!!
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        Logg.logging(TAG, " mScreeType  $mScreeType ")

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD) {

            dataBinding.tickLayout.visibility = View.VISIBLE
            dataBinding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
            dataBinding.conformBtn.text = getString(R.string.str_back_to_vehicles_list)
            dataBinding.addVehiclesTxt.visibility = View.GONE

        }

        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_HISTORY) {

            dataBinding.llyt.visibility = View.VISIBLE
            dataBinding.addVehiclesTxt.visibility = View.GONE
            dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_vehicle_history)
            dataBinding.conformBtn.text = getString(R.string.str_save)

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

        dataBinding.conformBtn.setOnClickListener {
            val intent = Intent(this, ActivityFutureCrossing::class.java)
            intent.putExtra("list", mVehicleDetails)
            startActivity(intent)

        }

        dataBinding.idToolBarLyt.back_button.setOnClickListener {
//            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val TAG = "VehicleDetailFragment"

    private val mList = ArrayList<VehicleResponse>()
    private fun setAdapter() {

        val plateInfoResp = PlateInfoResponse(
            mVehicleDetails!!.plateInfo.number!!,
            mVehicleDetails.plateInfo.country!!,
            "HE",
            "-",
            "",
            "",
            ""
        )

        val vehicleInfoResp = VehicleInfoResponse(
            mVehicleDetails.vehicleInfo.make,
            mVehicleDetails.vehicleInfo.model,
            "2019",
            "",
            "1_GVVKGV",
            "",
            mVehicleDetails.vehicleInfo.color,
            "B",
            "23 Aug 2022"
        )

        val mVehicleResponse1 = VehicleResponse(plateInfoResp, vehicleInfoResp,true)
        mList.add(mVehicleResponse1)
        Logg.logging(TAG, " mList  $mList ")

        mAdapter = VrmHeaderAdapter(this, this)
        mAdapter.setList(mList)
        dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        dataBinding.recyclerViewHeader.setHasFixedSize(true)
        dataBinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setBtnActivated() {
        dataBinding.conformBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.conformBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        updateVehicleApiCall(details)
    }


    private fun updateVehicleApiCall(details: VehicleResponse) {

//        var request = VehicleResponse(
//            PlateInfoResponse(
//                number = "HRS112022",
//                "UK", "HE", type = "STANDARD", "", "New vehicle", ""
//            ),
//            VehicleInfoResponse("AUDI", "Q5", "2021", "", "", "", "BLACK", "Class B", "")
//        )
        // vehicleMgmtViewModel.addVehicleApi(request);
        vehicleMgmtViewModel.updateVehicleApi(mVehicleDetails);
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
        message?.let{
            Toast.makeText(this, message , Toast.LENGTH_SHORT).show()
        }
    }


}