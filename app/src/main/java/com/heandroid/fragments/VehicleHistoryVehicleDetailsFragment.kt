package com.heandroid.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.VrmHistoryHeaderAdapter
import com.heandroid.databinding.FragmentVehicleHistoryVehicleDetailsBinding
import com.heandroid.hideKeyboard
import com.heandroid.model.*
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.view.OnEditTextValueChangedClickedListener
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory

class VehicleHistoryVehicleDetailsFragment : BaseFragment(), View.OnClickListener,
    OnEditTextValueChangedClickedListener {

    private lateinit var dataBinding: FragmentVehicleHistoryVehicleDetailsBinding
    private lateinit var vehicleMgmtViewModel: VehicleMgmtViewModel
    private lateinit var mVehicleDetails: VehicleResponse
    private var textChanged: Boolean = false
    private val TAG = "VehicleDetailFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding =
            FragmentVehicleHistoryVehicleDetailsBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCtrl()
        setupViewModel()
        setBtnActivated()
        setAdapter()

    }

    private fun initCtrl() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        Log.i("teja1234", mVehicleDetails.toString())
        dataBinding.apply {
            saveBtn.setOnClickListener {
                if (textChanged) {
                    updateVehicleApiCall(mVehicleDetails)
                }
            }
            backToVehiclesBtn.setOnClickListener {
                requireActivity().finish()
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.make_payment_btn -> {
            }
            R.id.back_btn -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun setAdapter() {

        val mList = ArrayList<VehicleTitleAndSub>()
        mList.clear()

        for (i in 0..7) {
            when (i) {

                0 -> {
                    val mem0 =
                        VehicleTitleAndSub("Registration Number", mVehicleDetails.plateInfo.number)
                    mList.add(mem0)

                }

                1 -> {

                    val mem0 =
                        VehicleTitleAndSub("Country marker", mVehicleDetails.plateInfo.country)
                    mList.add(mem0)
                }
                2 -> {
                    val mem1 = VehicleTitleAndSub("Make", mVehicleDetails.vehicleInfo.make)
                    mList.add(mem1)

                }
                3 -> {
                    val mem2 = VehicleTitleAndSub("Model", mVehicleDetails.vehicleInfo.model)
                    mList.add(mem2)
                }
                4 -> {
                    val mem2 = VehicleTitleAndSub("Colour", mVehicleDetails.vehicleInfo.color)
                    mList.add(mem2)
                }
                5 -> {
                    val mem2 =
                        VehicleTitleAndSub("Class", mVehicleDetails.vehicleInfo.vehicleClassDesc)
                    mList.add(mem2)

                }
                6 -> {
                    val mem2 = VehicleTitleAndSub(
                        "DateAdded",
                        mVehicleDetails.vehicleInfo.effectiveStartDate
                    )
                    mList.add(mem2)

                }

                7 -> {
                    val mem2 =
                        VehicleTitleAndSub("Notes", mVehicleDetails.plateInfo.vehicleComments)
                    mList.add(mem2)

                }

            }

        }

        Logg.logging(TAG, " mList  $mList ")
        Logg.logging(TAG, " mList size ${mList.size} ")

        val mAdapter = VrmHistoryHeaderAdapter(requireContext(), this)
        mAdapter.setList(mList)
        dataBinding.recyclerViewHeader.layoutManager = LinearLayoutManager(requireContext())
        dataBinding.recyclerViewHeader.setHasFixedSize(true)
        dataBinding.recyclerViewHeader.adapter = mAdapter

    }

    private fun setBtnActivated() {

        dataBinding.saveBtn.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.btn_color
            )
        )

        dataBinding.saveBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

    }

    private fun updateVehicleApiCall(details: VehicleResponse) {

        val request = details.apply {
            newPlateInfo = plateInfo
        }
        vehicleMgmtViewModel.updateVehicleApi(request)
        dataBinding.progressLayout.visibility = View.VISIBLE

        vehicleMgmtViewModel.updateVehicleApiVal.observe(viewLifecycleOwner,
            {
                when (it.status) {
                    Status.SUCCESS -> {
                        dataBinding.progressLayout.visibility = View.GONE
                        if (it.data!!.body() == null) {
                            val apiResponse = EmptyApiResponse(200, "Updated successfully.")
                            Log.d("ApiSuccess : ", apiResponse.status.toString())

                            showToast("Vehicle is updated successfully")
                        }

                    }

                    Status.ERROR -> {
                        dataBinding.progressLayout.visibility = View.GONE
                        showToast(it.message)
                    }

                    Status.LOADING -> {
                        // show/hide loader
                        dataBinding.progressLayout.visibility = View.VISIBLE
                        Log.d("UpdateApi: ", "Data loading")
                    }
                }
            })

    }

    private fun setupViewModel() {
        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        vehicleMgmtViewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun OnEditTextValueChanged(value: String) {
        if (!TextUtils.isEmpty(value)) {
            hideKeyboard()
            textChanged = true
            mVehicleDetails.plateInfo.vehicleComments = value
        }
    }
}