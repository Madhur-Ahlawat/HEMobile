package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.ViewChargesListAdapter
import com.heandroid.databinding.ActivityViewChargesBinding
import com.heandroid.model.ViewChargesResponse
import kotlinx.android.synthetic.main.tool_bar_white.view.*

class ActivityViewCharges : AppCompatActivity() {

    private val mList = ArrayList<ViewChargesResponse>()


    private lateinit var dataBinding: ActivityViewChargesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_charges)
        setContent()
    }

    private fun setContent() {
        val mData1 =
            ViewChargesResponse(getString(R.string.str_motor_cycles), "Free", "Free", "Free")
        val mData2 =
            ViewChargesResponse(getString(R.string.str_cars_motor_homes), "£2.50", "£2.50", "£2.00")
        val mData3 = ViewChargesResponse(
            getString(R.string.str_vehicles_with_axles),
            "£3.00",
            "£3.00",
            "£2.63"
        )
        val mData4 = ViewChargesResponse(
            getString(R.string.str_vehicle_with_more_axles),
            "£6.00",
            "£6.00",
            "£5.69"
        )
        dataBinding.toolBarLyt.tvHeader.text = getString(R.string.str_charges_6am_10pm)
        mList.add(mData1)
        mList.add(mData2)
        mList.add(mData3)
        mList.add(mData4)
        val vehicleListAdapter = ViewChargesListAdapter(this)
        vehicleListAdapter.setList(mList)
        dataBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        dataBinding.recyclerView.setHasFixedSize(true)
        dataBinding.recyclerView.adapter = vehicleListAdapter
        dataBinding.toolBarLyt.btnBack.setOnClickListener {
            finish()
        }

    }
}