package com.heandroid.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.heandroid.R
import com.heandroid.databinding.FragmentVrmClassDetailsBinding
import com.heandroid.dialog.VehicleAddConfirm
import com.heandroid.listener.AddVehicleListener
import com.heandroid.model.EmptyApiResponse
import com.heandroid.model.PlateInfoResponse
import com.heandroid.model.VehicleInfoResponse
import com.heandroid.model.VehicleResponse
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.tool_bar_with_title_back.view.*

class VrmEditClassesActivity : AppCompatActivity(), AddVehicleListener {

    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var dataBinding: FragmentVrmClassDetailsBinding
    private lateinit var mVehicleDetails: VehicleResponse
    val TAG = "VrmEditClassesActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.fragment_vrm_class_details)
        setUpView()
        setupViewModel()
    }

    private fun setupViewModel() {

        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        Log.d("ViewModelSetUp: ", "Setup")
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
        Log.d("ViewModelSetUp: ", "Setup")
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

        //dataBinding.progressLayout.visibility= VISIBLE
        addVehicleApiCall()

    }

    private fun showVehicleDetails() {
        Intent(this, VehicleDetailActivity::class.java).apply {

            putExtra(Constants.DATA, mVehicleDetails)
            putExtra(Constants.VEHICLE_SCREEN_KEY, Constants.VEHICLE_SCREEN_TYPE_ADD)
            putExtra(
                Constants.VEHICLE_SCREEN_KEY,
                Constants.VEHICLE_SCREEN_TYPE_ADD
            )

            startActivity(this)
            finish()
        }
    }

    private fun addVehicleApiCall() {


        /** {

        {

        "plateInfo": {

        "number": "HRS115022",

        "country": "UK",

        "state": "HE",

        "type": "STANDARD",

        "vehicleGroup": "",

        "vehicleComments": "new Vehicle",

        "planName": ""

        },

        "vehicleInfo": {

        "make": "AUDI",

        "model": "Q5",

        "year": 2021,

        "typeId": null,

        "typeDescription": "REGULAR",

        "color": "Black",

        "vehicleClassDesc": "Class B",

        "effectiveStartDate": ""

        }

        }

*/


        var request = mVehicleDetails.apply {
            plateInfo.state = "HE"
            plateInfo.type = "STANDARD"
            plateInfo.vehicleGroup = ""
            plateInfo.vehicleComments = "new Vehicle"
            plateInfo.planName = ""
            vehicleInfo.year = "2022"
            vehicleInfo.typeId=null
            vehicleInfo.typeDescription="REGULAR"
        }

        mVehicleDetails = request;
        vehicleMgmtViewModel.addVehicleApi(request);
        vehicleMgmtViewModel.addVehicleApiVal.observe(this,
            {

                when (it.status) {
                    Status.SUCCESS -> {
                        dataBinding.progressLayout.visibility=GONE
                        if (it.data!!.body() == null) {
                            var apiResponse = EmptyApiResponse(200, "Added successfully.")
                            Log.d("ApiSuccess : ", apiResponse!!.status.toString())
                            showVehicleDetails()
                        }

                    }

                    Status.ERROR -> {
                        //todo we need to update this
                        dataBinding.progressLayout.visibility=GONE
                        showToast(it.message)
                        showVehicleDetails()
                    }

                    Status.LOADING -> {
                        // show/hide loader
                        dataBinding.progressLayout.visibility= VISIBLE
                        Log.d("GetAlert: ", "Data loading")
                    }
                }
            })

    }

    private fun showToast(message: String?) {

        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

}