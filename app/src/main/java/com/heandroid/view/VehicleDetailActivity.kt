package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VehicleDetailsAdapter
import com.heandroid.databinding.FragmentVehicleDetailBinding
import com.heandroid.model.VehicleDetailsModel
import com.heandroid.model.VehicleTitleAndSub
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VehicleDetailActivity : AppCompatActivity() {

    private lateinit var mAdapter: VehicleDetailsAdapter

    private lateinit var dataBinding: FragmentVehicleDetailBinding

    private lateinit var mVehicleDetails: VehicleDetailsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.fragment_vehicle_detail)
        setUp()
    }

    private fun setUp() {
        mVehicleDetails =
            intent?.getParcelableExtra<VehicleDetailsModel>("list") as VehicleDetailsModel
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        setBtnActivated()
        setAdapter()
        setClickEvents()

    }

    private var isOpen = true
    private fun setClickEvents() {
        dataBinding.vrmTitle.text = "${mVehicleDetails.vrmNo}"
        dataBinding.arrowImg.animate().rotation(180f).start()
        dataBinding.cardviewTop.setOnClickListener {

            if (isOpen) {
                isOpen = false
                dataBinding.cardViewBottom.visibility = View.VISIBLE
                dataBinding.arrowImg.animate().rotation(180f).start()
            } else {
                isOpen = true
                dataBinding.cardViewBottom.visibility = View.GONE
                dataBinding.arrowImg.animate().rotation(0f).start()

            }

        }

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

    private val mList = ArrayList<VehicleTitleAndSub>()
    private fun setAdapter() {
        for (i in 0..3) {
            when (i) {

                0 -> {

                    val mem0 = VehicleTitleAndSub("Country", mVehicleDetails.vrmCountry)
                    mList.add(mem0)
                }
                1 -> {
                    val mem1 = VehicleTitleAndSub("Make", mVehicleDetails.vrmMake)
                    mList.add(mem1)

                }
                2 -> {
                    val mem2 = VehicleTitleAndSub("Model", mVehicleDetails.vrmModel)
                    mList.add(mem2)
                }
                3 -> {
                    val mem2 = VehicleTitleAndSub("Colour", mVehicleDetails.vrmColor)
                    mList.add(mem2)
                }

            }

        }

        Logg.logging(TAG, " mList  $mList ")

        mAdapter = VehicleDetailsAdapter(this)
        mAdapter.setList(mList)
        dataBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        dataBinding.recyclerView.setHasFixedSize(true)
        dataBinding.recyclerView.adapter = mAdapter


    }

    private fun setBtnActivated() {
        dataBinding.conformBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.btn_color))

        dataBinding.conformBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }


}