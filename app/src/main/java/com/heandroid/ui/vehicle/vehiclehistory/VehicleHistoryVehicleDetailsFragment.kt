package com.heandroid.ui.vehicle.vehiclehistory

import android.annotation.SuppressLint
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import com.heandroid.utils.DateUtils
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

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
        binding.edtNote.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.edtNote.setRawInputType(InputType.TYPE_CLASS_TEXT)
        setBtnDisabled()
    }

    @SuppressLint("ClickableViewAccessibility")
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

            edtNote.setOnTouchListener { view, event ->
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
                return@setOnTouchListener false
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
        try{
        loader?.dismiss()
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
        }}catch (e: Exception){}
    }

    private fun setDataToView() {
        mVehicleDetails?.let { response ->
            binding.vehicleData = response
            binding.addedDate.text = DateUtils.convertDateFormat(response.vehicleInfo.effectiveStartDate,1)
        }
    }

    private fun setBtnActivated() {
        binding.buttonModel = true
    }

    private fun setBtnDisabled() {
        binding.buttonModel = false
    }

}
