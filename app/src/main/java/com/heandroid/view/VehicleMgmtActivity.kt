package com.heandroid.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.heandroid.R
import com.heandroid.adapter.VehicleListAdapter
import com.heandroid.adapter.VrmHeaderAdapter
import com.heandroid.adapter.VrmHistoryAdapter
import com.heandroid.databinding.ActivityVehicleMgmtBinding
import com.heandroid.dialog.AddVehicle
import com.heandroid.listener.AddVehicleListener
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleApiResp
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleMgmtActivity : AppCompatActivity(), AddVehicleListener, ItemClickListener {

    private lateinit var databinding: ActivityVehicleMgmtBinding
    private lateinit var mAdapter: VrmHeaderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.activity_vehicle_mgmt)
        setView()
    }

    private var mType: Int? = null
    private fun setView() {

        mType = intent?.getIntExtra(Constants.VEHICLE_SCREEN_KEY, 0)

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {
                    databinding.idToolBarLyt.title_txt.text = "Vehicles list"
                    setListAdapter()

                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                    databinding.idToolBarLyt.title_txt.text = "Add vehicles"
                    databinding.addVehiclesTxt.text = "Add vehicles to your account"
                    databinding.conformBtn.text = "Add Vehicle"
                    databinding.conformBtn.icon = AppCompatResources.getDrawable(
                        this@VehicleMgmtActivity,
                        R.drawable.ic_baseline_add_24
                    )
                    databinding.conformBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                    databinding.conformBtn.iconTint = ColorStateList.valueOf(Color.WHITE)
                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {
                    databinding.idToolBarLyt.title_txt.text = "Vehicles History"
                    databinding.conformBtn.visibility = View.GONE
                    setHistoryAdapter()
                }

            }

        }

        databinding.idToolBarLyt.back_button.setOnClickListener {
            finish()
        }


        databinding.conformBtn.setOnClickListener {

            AddVehicle.newInstance(
                getString(R.string.str_title),
                getString(R.string.str_sub_title),
                this@VehicleMgmtActivity
            ).show(supportFragmentManager, AddVehicle.TAG)

        }

    }

    private val mVehicleList = ArrayList<VehicleResponse>()

    private fun setHistoryAdapter() {
        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        val mAdapter = VrmHistoryAdapter(this, this)
        mVehicleList.clear()
        mVehicleList.addAll(vehicleResp.vehicleList)
        mAdapter.setList(mVehicleList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setListAdapter() {

        var bundle = intent.getBundleExtra(Constants.VEHICLE_DATA)
        var vehicleResp = bundle?.getSerializable(Constants.VEHICLE_RESPONSE) as VehicleApiResp

        mVehicleList.clear()
        mVehicleList.addAll(vehicleResp.vehicleList)

        mAdapter = VrmHeaderAdapter(this, this)
        mAdapter.setList(mVehicleList)
        databinding.recyclerViewHeader.layoutManager = LinearLayoutManager(this)
        databinding.recyclerViewHeader.setHasFixedSize(true)
        databinding.recyclerViewHeader.adapter = mAdapter

    }

    override fun onAddClick(details: VehicleResponse) {

        val intent = Intent(this, VrmEditMakeModelColorActivity::class.java)
        intent.putExtra("list", details)
        startActivity(intent)

    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {

        mType?.apply {
            when (this) {

                Constants.VEHICLE_SCREEN_TYPE_LIST -> {

                    details.isExpanded = !details.isExpanded
                    Logg.logging("VehcileMgm","  details.isExpanded  ${details.isExpanded}")
                    mVehicleList[pos].isExpanded = details.isExpanded

                    mAdapter.notifyItemChanged(pos)

                }

                Constants.VEHICLE_SCREEN_TYPE_ADD -> {
                }

                Constants.VEHICLE_SCREEN_TYPE_HISTORY -> {

                    Intent(this@VehicleMgmtActivity, VehicleDetailActivity::class.java).apply {
                        putExtra("list", details)
                        putExtra(
                            Constants.VEHICLE_SCREEN_KEY,
                            Constants.VEHICLE_SCREEN_TYPE_HISTORY
                        )
                        startActivity(this)

                    }

                }

            }

        }


    }
}