package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.EnterVrmOptionsModel
import com.heandroid.databinding.FragmentEnterVrmCheckBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnterVrmFragment : BaseFragment<FragmentEnterVrmCheckBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var country = "UK"
    private var isCalled = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEnterVrmCheckBinding.inflate(inflater, container, false)

    override fun init() {
        binding.model = EnterVrmOptionsModel(vrm = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.vrmNo.addTextChangedListener { isEnable() }
        binding.findVehicle.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.findVehicleLiveData, ::apiResponseDVRM)
        }
    }

    private fun isEnable() {
        if (binding.vrmNo.length() > 0) binding.model =
            EnterVrmOptionsModel(enable = true, vrm = binding.vrmNo.text.toString())
        else binding.model =
            EnterVrmOptionsModel(enable = false, vrm = binding.vrmNo.text.toString())
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.findVehicle -> {
                country = if (!binding.switchView.isChecked) {
                    "NON UK"
                } else {
                    "UK"
                }
                hideKeyboard()
                isCalled = true
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                viewModel.getVehicleData(binding.model?.vrm, Constants.AGENCY_ID.toInt())
            }
        }
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val bundle = Bundle().apply {
                            putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, it)
                            putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
                            putString(
                                Constants.CHECK_PAID_CROSSING_VRM_ENTERED,
                                binding.model?.vrm
                            )
                            putString(Constants.COUNTRY_TYPE, country)
                        }

                        findNavController().navigate(
                            R.id.action_enterVrmFragment_to_checkPaidCrossingChangeVrm,
                            bundle
                        )
                    }

                }
                is Resource.DataError -> {
                    val bundle = Bundle().apply {
                        putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
                        putString(
                            Constants.CHECK_PAID_CROSSING_VRM_ENTERED,
                            binding.model?.vrm
                        )
                        putString(Constants.COUNTRY_TYPE, country)
                    }

                    findNavController().navigate(
                        R.id.action_enterVrmFragment_to_checkPaidCrossingChangeVrm,
                        bundle
                    )
                }
                else -> {
                }
            }
            isCalled = false
        }
    }

}