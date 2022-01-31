package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VehicleDetailsAdapter
import com.heandroid.adapter.VrmHeaderAdapter
import com.heandroid.databinding.FragmentVehicleDetailBinding
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.DashboardViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var mAdapter: VrmHeaderAdapter

    private lateinit var dataBinding: FragmentVehicleDetailBinding

    private lateinit var mVehicleDetails: VehicleDetailsModel
    private lateinit var dashboardViewModel: DashboardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_vehicle_detail)
        setUp()
    }

    private fun setupViewModel() {
        Log.d("DummyLogin", "set up view model")
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.loginApi))
        dashboardViewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
    }

    private fun setUp() {
        mVehicleDetails =
            intent?.getParcelableExtra<VehicleDetailsModel>("list") as VehicleDetailsModel
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        setBtnActivated()
        setAdapter()
        setClickEvents()

    }

    private fun setClickEvents() {

        dataBinding.conformBtn.setOnClickListener {

            val intent = Intent(this, ActivityFutureCrossing::class.java)
            intent.putExtra("list", mVehicleDetails)
            startActivity(intent)


        }
        dataBinding.idToolBarLyt.back_button.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private val TAG = "VehicleDetailFragment"

    private val mList = ArrayList<VehicleResponse>()
    private fun setAdapter() {

        Logg.logging(TAG, " mList  $mList ")

        val plateInfoResp = PlateInfoResponse(mVehicleDetails!!.vrmNo!!,mVehicleDetails.vrmCountry!!,"HE","-","","","")
        val vehicleInfoResp = VehicleInfoResponse(mVehicleDetails.vrmMake!!,mVehicleDetails.vrmModel!!,"2019","","1_GVVKGV","",mVehicleDetails.vrmColor!!,"B","23 Aug 2022")

        val mVehicleResponse1 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse2 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse3 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        val mVehicleResponse4 = VehicleResponse(plateInfoResp, vehicleInfoResp)
        mList.add(mVehicleResponse1)
        mList.add(mVehicleResponse2)
        mList.add(mVehicleResponse3)
        mList.add(mVehicleResponse4)

        mAdapter = VrmHeaderAdapter(this)
        mAdapter.setList(mList)
        dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        dataBinding.recyclerViewHeader.setHasFixedSize(true)
        dataBinding.recyclerViewHeader.adapter = mAdapter


    }

    private fun setBtnActivated() {
        dataBinding.conformBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.conformBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }


}