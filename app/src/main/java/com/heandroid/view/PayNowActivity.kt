package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.AddedVehicleListAdapter
import com.heandroid.adapter.PaymentVehicleListAdapter
import com.heandroid.databinding.FragmentConfirmPaymentBinding
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.PlateInfoResponse
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.model.VehicleResponse

class PayNowActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var mModel: VehicleResponse
    private lateinit var dataBinding: FragmentConfirmPaymentBinding
    private var mAdapter: PaymentVehicleListAdapter? = null
    private val mVehicleList = ArrayList<VehicleResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_confirm_payment)
        setView()

        val plateInfoResp = PlateInfoResponse(
            "LK62 NSO",
            "UK",
            "HE",
            "-",
            "",
            "",
            ""
        )
        val vehicleInfoResp = VehicleInfoResponse(
            "Audi",
            "TT FSI",
            "2019",
            "",
            "1_GVVKGV",
            "",
            "Black",
            "B",
            "23 Aug 2022"
        )

        mModel = VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp)

        mVehicleList.add(mModel)
        if (mVehicleList.size == 1) {
            mAdapter = PaymentVehicleListAdapter(this, this)
            mAdapter!!.setList(mVehicleList)
            dataBinding.rvVehicle.layoutManager = LinearLayoutManager(this)
            dataBinding.rvVehicle.setHasFixedSize(true)
            dataBinding.rvVehicle.adapter = mAdapter
        }

    }

    private fun setView() {
        dataBinding.btnPayNow.setOnClickListener {
            var intent = Intent(this, ActivityPaypalPage::class.java)
            startActivity(intent)
        }

        dataBinding.backArrow.setOnClickListener {
            finish()
        }

        dataBinding.tvChange.setOnClickListener {
            finish()
        }
    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {

    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {

    }
}