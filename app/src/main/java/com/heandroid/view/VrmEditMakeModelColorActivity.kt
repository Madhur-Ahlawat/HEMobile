package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.heandroid.R
import com.heandroid.databinding.FragmentVrmMakeDetailsBinding
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VrmEditMakeModelColorActivity : AppCompatActivity() {

    private lateinit var dataBinding: FragmentVrmMakeDetailsBinding

    private lateinit var mVehicleDetails: VehicleResponse

    val TAG = "VrmEditMakeModelColorActivity"
    private var mScreeType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_vrm_make_details)
        setUpViews()
    }

    private fun setUpViews() {

        mVehicleDetails =
            intent?.getSerializableExtra("list") as VehicleResponse
        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")

        dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_add_vehicles)

        dataBinding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"

        dataBinding.subTitle.text = "Country of registration ${mVehicleDetails.plateInfo.country}"
        dataBinding.nextBtn.setOnClickListener {

            if (dataBinding.makeInputEditText.text!!.isNotEmpty() && dataBinding.modelInputEditText.text!!.isNotEmpty() && dataBinding.colorInputEditText.text!!.isNotEmpty()) {

                mVehicleDetails.vehicleInfo.color = dataBinding.colorInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.make = dataBinding.makeInputEditText.text.toString()
                mVehicleDetails.vehicleInfo.model = dataBinding.modelInputEditText.text.toString()

                val intent = Intent(this, VrmEditClassesActivity::class.java)
                intent.putExtra("list", mVehicleDetails)
                startActivity(intent)

            }


        }
    }
}