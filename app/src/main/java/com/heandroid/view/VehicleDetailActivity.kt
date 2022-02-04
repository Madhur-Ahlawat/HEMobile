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
            dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_add_vehicle)
            dataBinding.tickLayout.visibility = View.VISIBLE
            dataBinding.tickTxt.text = getString(R.string.str_new_vehicles_added_success)
            dataBinding.conformBtn.text = getString(R.string.str_back_to_vehicles_list)
            dataBinding.addVehiclesTxt.visibility = View.GONE

        }
        dataBinding.idToolBarLyt.back_button.setOnClickListener {
            finish()
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
            if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {
                val intent = Intent(this, ActivityFutureCrossing::class.java)
                intent.putExtra("list", mVehicleDetails)
                startActivity(intent)
            }
            else{
                finish()
            }

        }

        dataBinding.idToolBarLyt.back_button.setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val TAG = "VehicleDetailFragment"

    private val mList = ArrayList<VehicleResponse>()
    private fun setAdapter() {

        mList.clear()
        if (mScreeType == Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT) {

            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails.plateInfo.number,
                mVehicleDetails.plateInfo.country,
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails.vehicleInfo.make,
                mVehicleDetails.vehicleInfo.model,
                "",
                "",
                "",
                "",
                mVehicleDetails.vehicleInfo.color,
                "",
                ""
            )

            val mVehicleResponse1 = VehicleResponse(plateInfoResp , plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)
        } else {


            val plateInfoResp = PlateInfoResponse(
                mVehicleDetails.plateInfo.number,
                mVehicleDetails.plateInfo.country,
                "HE",
                "-",
                "",
                "",
                ""
            )

            val vehicleInfoResp = VehicleInfoResponse(
                mVehicleDetails.vehicleInfo.make,
                mVehicleDetails.vehicleInfo.model,
                "",
                "",
                "",
                "",
                mVehicleDetails.vehicleInfo.color,
                mVehicleDetails.vehicleInfo.vehicleClassDesc,
                mVehicleDetails.vehicleInfo.effectiveStartDate
            )

            val mVehicleResponse1 = VehicleResponse(plateInfoResp ,plateInfoResp, vehicleInfoResp, true)
            mList.add(mVehicleResponse1)

        }

        Logg.logging(TAG, " mList  $mList ")

        if (mList.size > 0) {
            mAdapter = VrmHeaderAdapter(this, this)
            mAdapter.setList(mList)
            dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
            dataBinding.recyclerViewHeader.setHasFixedSize(true)
            dataBinding.recyclerViewHeader.adapter = mAdapter
        }

    }

    private fun setBtnActivated() {
        dataBinding.conformBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.conformBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
    }


}