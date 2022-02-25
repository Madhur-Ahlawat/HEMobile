package com.heandroid.ui.vehicle.vehiclehistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.response.EmptyApiResponse
import com.heandroid.data.model.response.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleHistoryVehicleDetailsFragment :
    BaseFragment<FragmentVehicleHistoryVehicleDetailsBinding>(), View.OnClickListener {

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by activityViewModels()
    private var mVehicleDetails: VehicleResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleHistoryVehicleDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        setBtnDisabled()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.saveBtn -> {
                mVehicleDetails?.let {
                    val request = it.apply {
                        newPlateInfo = plateInfo
                    }
                    vehicleMgmtViewModel.updateVehicleApi(request)
                }
            }
            R.id.backToVehiclesBtn -> {
                findNavController().popBackStack()
            }
        }
    }


    override fun initCtrl() {
        binding.apply {
            saveBtn.setOnClickListener(this@VehicleHistoryVehicleDetailsFragment)
            backToVehiclesBtn.setOnClickListener(this@VehicleHistoryVehicleDetailsFragment)
            edtNote.doOnTextChanged { _, _, _, _ ->
                if (edtNote.text?.isEmpty() == true ||
                    edtNote.text?.equals(mVehicleDetails?.plateInfo?.vehicleComments) == true
                ) {
                    setBtnDisabled()
                } else {
                    setBtnActivated()
                }
            }
        }
    }

    override fun observer() {
        observe(vehicleMgmtViewModel.selectedVehicleResponse, ::handleSelectedVehicleResponse)
        observe(vehicleMgmtViewModel.updateVehicleApiVal, ::handleUpdateVehicleResponse)
    }

    private fun handleSelectedVehicleResponse(vehicleResponse: VehicleResponse?) {
        vehicleResponse?.let {
            mVehicleDetails = it
            setDataToView()
        }
    }

    private fun handleUpdateVehicleResponse(response: Resource<EmptyApiResponse?>?) {
        when (response) {
            is Resource.Success -> {
                response.data?.let {
                    showToast("Vehicle is updated successfully")
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun setDataToView() {
        mVehicleDetails?.let { response ->
            binding.apply {
                regNum.text = response.plateInfo.number
                countryMarker.text = response.plateInfo.country
                vehicleClass.text = response.vehicleInfo.vehicleClassDesc
                make.text = response.vehicleInfo.make
                model.text = response.vehicleInfo.model
                color.text = response.vehicleInfo.color
                addedDate.text = response.vehicleInfo.effectiveStartDate
                edtNote.setText(response.plateInfo.vehicleComments)
            }
        }
    }

    private fun setBtnActivated() {
        binding.saveBtn.apply {
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
        binding.saveBtn.apply {
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

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
