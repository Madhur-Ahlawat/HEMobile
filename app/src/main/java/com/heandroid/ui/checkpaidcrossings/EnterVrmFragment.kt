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
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsOptionsModel
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.heandroid.data.model.checkpaidcrossings.CheckPaidCrossingsResponse
import com.heandroid.data.model.checkpaidcrossings.EnterVrmOptionsModel
import com.heandroid.databinding.FragmentEnterVrmCheckBinding
import com.heandroid.databinding.FragmentPaidCrossingCheckBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class EnterVrmFragment : BaseFragment<FragmentEnterVrmCheckBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnterVrmCheckBinding =
        FragmentEnterVrmCheckBinding.inflate(inflater, container, false)

    private var country="UK"
    override fun init() {

        binding.model = EnterVrmOptionsModel(vrm = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when(checkedId){

                R.id.uk->{
                    country = "UK"
                }

                R.id.non_uk->{
                    country = "NON UK"
                }

            }
        }

    }

    override fun initCtrl() {

        binding.vrmNo.addTextChangedListener { isEnable() }
        binding.findVehicle.setOnClickListener(this)

        val mData =
            arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)!!
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!
        val index =
            arguments?.getInt("Index")

        Logg.logging("TestingEnterVrm", " mData $mData ")
        Logg.logging("TestingEnterVrm", " mDataVrmRef $mDataVrmRef ")
        Logg.logging("TestingEnterVrm", " index $index ")

        binding

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
                hideKeyboard()
                val validation = viewModel.validationEnterVrm(binding.model)
                if (validation.first) {
                    loader?.show(requireActivity().supportFragmentManager, "")
                    viewModel.getVehicleData(binding.model?.vrm, Constants.AGENCY_ID)
                } else {
                    ErrorUtil.showError(binding.root, validation.second)
                }
            }
        }

    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {
        Logg.logging("Testing", "AddVehicleDoneFragment  apiResponseDVRM")
        loader?.dismiss()

        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    arguments?.putParcelable(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS, it)
                    arguments?.putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, true)
                    arguments?.putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, binding?.model?.vrm!!)
                    arguments?.putString(Constants.COUNTRY_TYPE, country)
                    findNavController().navigate(
                        R.id.action_enterVrmFragment_to_checkPaidCrossingChangeVrm,
                        arguments
                    )

                }
                Logg.logging(
                    "TestingEnterVrm",
                    "AddVehicleDoneFragment  apiResponseDVRM Success called"
                )


            }
            is Resource.DataError -> {
                loader?.dismiss()
                Logg.logging(
                    "TestingEnterVrm",
                    "AddVehicleDoneFragment  apiResponseDVRM DataError called mVehicleDetails "
                )
                arguments?.putBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS, false)
                arguments?.putString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED, binding.model?.vrm!!)
                arguments?.putString(Constants.COUNTRY_TYPE, country)

                findNavController().navigate(
                    R.id.action_enterVrmFragment_to_checkPaidCrossingChangeVrm,
                    arguments
                )

            }
        }

    }


}