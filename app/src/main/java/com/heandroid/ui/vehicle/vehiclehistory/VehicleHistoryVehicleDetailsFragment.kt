package com.heandroid.ui.vehicle.vehiclehistory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.Constants
import com.heandroid.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryVehicleDetailsFragment : BaseFragment(), View.OnClickListener{

    private lateinit var dataBinding: FragmentVehicleHistoryVehicleDetailsBinding
    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private var mVehicleDetails: VehicleResponse? = null

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
        setBtnDisabled()
    }

    private fun initCtrl() {
        mVehicleDetails = arguments?.getSerializable(Constants.DATA) as VehicleResponse
        mVehicleDetails?.let { response ->
            dataBinding.apply {
                regNum.text = response.plateInfo.number
                countryMarker.text = response.plateInfo.country
                vehicleClass.text = response.vehicleInfo.vehicleClassDesc
                make.text = response.vehicleInfo.make
                model.text = response.vehicleInfo.model
                color.text = response.vehicleInfo.color
                addedDate.text =  response.vehicleInfo.effectiveStartDate
                edtNote.setText(response.plateInfo.vehicleComments)

                saveBtn.setOnClickListener {
                    updateVehicleApiCall(response)
                }
                backToVehiclesBtn.setOnClickListener {
                    findNavController().popBackStack()
                }

                edtNote.addTextChangedListener( object : TextWatcher{
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) { }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (edtNote.text?.isEmpty() == true || edtNote.text?.equals(mVehicleDetails?.plateInfo?.vehicleComments)!!){
                            setBtnDisabled()
                        } else {
                            setBtnActivated()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) { }
                })
            }
        }
    }


    override fun onClick(v: View?) { }

    private fun setBtnActivated() {
        dataBinding.saveBtn.apply {
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.btn_color
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            isClickable = true
        }

    }

    private fun setBtnDisabled() {
        dataBinding.saveBtn.apply{
            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_C9C9C9
                )
            )
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_7D7D7D))
            isClickable = false
        }
    }

    private fun updateVehicleApiCall(details: VehicleResponse) {
        val request = details.apply {
            newPlateInfo = plateInfo
        }
        vehicleMgmtViewModel.updateVehicleApi(request)
        dataBinding.progressLayout.visibility = View.VISIBLE

        vehicleMgmtViewModel.updateVehicleApiVal.observe(viewLifecycleOwner,
            { resource ->
                when (resource) {
                    is Resource.Success -> {
                        dataBinding.progressLayout.visibility = View.GONE
                        resource.data!!.body()?.let {
                            showToast("Vehicle is updated successfully")
                        }
                    }

                    is Resource.DataError -> {
                        dataBinding.progressLayout.visibility = View.GONE
                        showToast(resource.errorMsg)
                    }

                    is Resource.Loading -> {
                        dataBinding.progressLayout.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}