package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.checkpaidcrossings.EnterVrmOptionsModel
import com.conduent.nationalhighways.databinding.FragmentEnterVrmCheckBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EnterVrmFragment : BaseFragment<FragmentEnterVrmCheckBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var country = "UK"
    private var isCalled = false
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEnterVrmCheckBinding.inflate(inflater, container, false)

    override fun init() {
        AdobeAnalytics.setScreenTrack(
            "check crossings:enter vehicle",
            "vehicle",
            "english",
            "check crossings",
            "home",
            "check crossings:enter vehicle",
            sessionManager.getLoggedInUser()
        )

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