package com.heandroid.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentVrmClassDetailsBinding
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VrmEditClassesActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentVrmClassDetailsBinding
    private lateinit var mVehicleDetails: VehicleResponse
    val TAG = "VrmEditClassesActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_vrm_class_details)
        setUpView()
    }

    private fun setUpView() {

        mVehicleDetails =
            intent?.getSerializableExtra("list") as VehicleResponse

        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_add_vehicles)
        dataBinding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"


    }
}