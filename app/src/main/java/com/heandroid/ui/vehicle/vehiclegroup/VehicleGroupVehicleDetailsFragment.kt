package com.heandroid.ui.vehicle.vehiclegroup

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
import com.heandroid.databinding.FragmentGroupVehicleDetailBinding
import com.heandroid.databinding.FragmentVehicleHistoryVehicleDetailsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.SelectedVehicleViewModel
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.DateUtils
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class VehicleGroupVehicleDetailsFragment :
    BaseFragment<FragmentGroupVehicleDetailBinding>() {

    private var mVehicleDetails: VehicleResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentGroupVehicleDetailBinding.inflate(inflater, container, false)

    override fun init() {
        mVehicleDetails = arguments?.getParcelable(Constants.DATA)
        setDataToView()
    }

    override fun initCtrl() {
        binding.apply {
            editDetailsBtn.setOnClickListener {
                mVehicleDetails?.let {

                }
            }
        }
    }

    override fun observer() {
    }


    private fun setDataToView() {
        mVehicleDetails?.let { response ->
            binding.vehicleData = response
            binding.tvAddedDate.text =
                DateUtils.convertDateFormat(response.vehicleInfo?.effectiveStartDate, 1)
        }
    }

}
