package com.heandroid.ui.viewcharges

import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.tollrates.ViewChargesResponse
import com.heandroid.databinding.ActivityViewChargesBinding
import com.heandroid.ui.base.BaseActivity
import com.heandroid.utils.extn.toolbar

class ActivityViewCharges : BaseActivity<ActivityViewChargesBinding>() {

    private val mList = ArrayList<ViewChargesResponse>()

    private lateinit var binding: ActivityViewChargesBinding

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
        mList.add(mData1)
        mList.add(mData2)
        mList.add(mData3)
        mList.add(mData4)
        val vehicleListAdapter = ViewChargesListAdapter(this)
        vehicleListAdapter.setList(mList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = vehicleListAdapter

        toolbar(getString(R.string.str_charges_6am_10pm))


    }

    override fun observeViewModel() {
    }

    override fun initViewBinding() {
        binding = ActivityViewChargesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContent()

    }
}