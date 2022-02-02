package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.listener.AddVehicleListener

import com.heandroid.R
import com.heandroid.adapter.AddedVehicleListAdapter
import com.heandroid.databinding.OneOfPaymentActivityBinding
import com.heandroid.listener.ItemClickListener
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.utils.Logg
import com.heandroid.dialog.AddVehicle
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Constants
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class OneOfPayment : AppCompatActivity(), AddVehicleListener, ItemClickListener {

    private lateinit var databinding: OneOfPaymentActivityBinding
    private lateinit var mAdapter: AddedVehicleListAdapter

    private val TAG = "OneOfPayment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this, R.layout.one_of_payment_activity)
        setUpViewAction()
        setBtnNormal()
    }

    private fun setUpViewAction() {

        databinding.idToolBarLyt.back_button.setOnClickListener {
            onBackPressed()
        }
        databinding.findVehicle.setOnClickListener {
        }
        databinding.addBtn.setOnClickListener {
            AddVehicle.newInstance(
                getString(R.string.str_title),
                getString(R.string.str_sub_title),
                this
            ).show(supportFragmentManager, AddVehicle.TAG)

        }

    }

    private fun setBtnActivated() {
        databinding.findVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        databinding.findVehicle.setTextColor(ContextCompat.getColor(this, R.color.white))
        databinding.findVehicle.isEnabled = true
    }

    private fun setBtnNormal() {
        databinding.findVehicle.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        databinding.findVehicle.setTextColor(ContextCompat.getColor(this, R.color.black))

        databinding.findVehicle.isEnabled = false

    }

    private val mVehicleList = ArrayList<VehicleResponse>()
    override fun onAddClick(details: VehicleResponse) {
        mVehicleList.add(details)
        Logg.logging(TAG, "onAddClick called $mVehicleList")
        setAdapter()

    }

    private fun setAdapter() {

        databinding.idVehiclesList.visibility = View.VISIBLE
        databinding.noVehiclesAdded.visibility = View.GONE
        databinding.addVehiclesTxt.text = "Your vehicle"
        if (mVehicleList.size == 1) {
            mAdapter = AddedVehicleListAdapter(this, this)
            mAdapter.setList(mVehicleList)
            databinding.idVehiclesList.layoutManager = LinearLayoutManager(this)
            databinding.idVehiclesList.setHasFixedSize(true)
            databinding.idVehiclesList.adapter = mAdapter
        } else {
            if (::mAdapter.isInitialized) {
                mAdapter.setList(mVehicleList)
                mAdapter.notifyDataSetChanged()
            }
        }

    }

    override fun onItemDeleteClick(details: VehicleResponse, pos: Int) {
        if (mVehicleList.size > 0) {
            mVehicleList.remove(details)
            if (::mAdapter.isInitialized) {
                mAdapter.setList(mVehicleList)
                //mAdapter.notifyDataSetChanged()
               // mAdapter.notifyItemChanged(pos)
                mAdapter.notifyItemRemoved(pos)
            }
        }
        if (mVehicleList.size == 0) {
            databinding.idVehiclesList.visibility = View.GONE
            databinding.noVehiclesAdded.visibility = View.VISIBLE
            databinding.addVehiclesTxt.text = getString(R.string.str_add_vehicle_to_account)

        }

    }

    override fun onItemClick(details: VehicleResponse, pos: Int) {
        val intent = Intent(this, VehicleDetailActivity::class.java)
        intent.putExtra("list",details)
        intent.putExtra(
            Constants.VEHICLE_SCREEN_KEY,
            Constants.VEHICLE_SCREEN_TYPE_ADD_ONE_OF_PAYMENT
        )

        startActivity(intent)
/*
        val mFrag = VehicleDetailFragment.newInstance(details)
        databinding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mFrag).commit()
*/

    }

}