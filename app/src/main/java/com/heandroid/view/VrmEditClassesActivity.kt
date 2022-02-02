package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.databinding.FragmentVrmClassDetailsBinding
import com.heandroid.dialog.VehicleAddConfirm
import com.heandroid.listener.AddVehicleListener
import com.heandroid.model.VehicleResponse
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VrmEditClassesActivity : AppCompatActivity(), AddVehicleListener {

    private lateinit var dataBinding: FragmentVrmClassDetailsBinding
    private lateinit var mVehicleDetails: VehicleResponse
    val TAG = "VrmEditClassesActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_vrm_class_details)
        setUpView()
    }

    private var mClassType = ""

    private fun setUpView() {

        mVehicleDetails =
            intent?.getSerializableExtra("list") as VehicleResponse

        Logg.logging(TAG, " mVehicleDetails  $mVehicleDetails ")
        dataBinding.idToolBarLyt.title_txt.text = getString(R.string.str_add_vehicles)
        dataBinding.title.text = "Vehicle registration number: ${mVehicleDetails.plateInfo.number}"


        dataBinding.classARadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class A"
            }

        }

        dataBinding.classBRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class B"

            }


        }
        dataBinding.classCRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classDRadioButton.isChecked = false
                mClassType = "Class C"

            }


        }
        dataBinding.classDRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                dataBinding.classARadioButton.isChecked = false
                dataBinding.classBRadioButton.isChecked = false
                dataBinding.classCRadioButton.isChecked = false
                mClassType = "Class D"

            }

        }
        dataBinding.continueButton.setOnClickListener {

            if (dataBinding.classVehicleCheckbox.isChecked && mClassType.isNotEmpty()) {
                mVehicleDetails.vehicleInfo.vehicleClassDesc = mClassType

                VehicleAddConfirm.newInstance(
                    mVehicleDetails,
                    this
                ).show(supportFragmentManager, VehicleAddConfirm.TAG)


            } else {
                Snackbar.make(
                    dataBinding.classAView,
                    "Please select the class and checkbox",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onAddClick(details: VehicleResponse) {
        val intent = Intent(this, VehicleDetailActivity::class.java)
        intent.putExtra("list", details)
        intent.putExtra(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_ADD)
        intent.putExtra(
            Constants.VEHICLE_SCREEN_KEY,
            Constants.VEHICLE_SCREEN_TYPE_ADD
        )

        startActivity(intent)

    }


}