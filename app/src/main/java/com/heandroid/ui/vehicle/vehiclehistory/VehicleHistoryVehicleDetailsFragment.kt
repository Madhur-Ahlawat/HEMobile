package com.heandroid.ui.vehicle.vehiclehistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentVehicleHistoryVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.SelectedVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VehicleHistoryVehicleDetailsFragment :
    BaseFragment<FragmentVehicleHistoryVehicleDetailsBinding>() {

    private val vehicleMgmtViewModel: VehicleMgmtViewModel by viewModels()
    private val selectedViewModel: SelectedVehicleViewModel by activityViewModels()
    private var mVehicleDetails: VehicleResponse? = null
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentVehicleHistoryVehicleDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        setBtnDisabled()
    }

    override fun initCtrl() {
        binding.apply {
            backToVehiclesBtn.setOnClickListener {
                findNavController().popBackStack(R.id.vehicleHistoryListFragment, false)
            }
            saveBtn.setOnClickListener {
                mVehicleDetails?.let {
                    val request = it.apply {
                        newPlateInfo = plateInfo
                        newPlateInfo.vehicleComments = binding.edtNote.text.toString().trim()
                        vehicleInfo.vehicleClassDesc =
                            VehicleClassTypeConverter.toClassCode(vehicleInfo.vehicleClassDesc)
                    }
                    loader?.show(requireActivity().supportFragmentManager, "")
                    vehicleMgmtViewModel.updateVehicleApi(request)
                }
            }

            edtNote.doOnTextChanged { _, _, _, _ ->
                if (edtNote.text.toString()
                        .trim() == mVehicleDetails?.plateInfo?.vehicleComments?.trim()
                ) {
                    setBtnDisabled()
                } else {
                    setBtnActivated()
                }
            }
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(selectedViewModel.selectedVehicleResponse, ::handleSelectedVehicleResponse)
            observe(vehicleMgmtViewModel.updateVehicleApiVal, ::handleUpdateVehicleResponse)
        }
    }

    private fun handleSelectedVehicleResponse(vehicleResponse: VehicleResponse?) {
        vehicleResponse?.let {
            mVehicleDetails = it
            setDataToView()
        }
    }

    private fun handleUpdateVehicleResponse(response: Resource<EmptyApiResponse?>?) {
        if (loader?.isVisible == true){
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                requireContext().showToast("Vehicle is updated successfully")
                setBtnDisabled()
                mVehicleDetails?.let {
                    it.plateInfo.vehicleComments = binding.edtNote.text.toString().trim()
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

}
