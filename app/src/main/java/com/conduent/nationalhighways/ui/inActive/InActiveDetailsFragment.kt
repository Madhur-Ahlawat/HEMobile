package com.conduent.nationalhighways.ui.inActive

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentInActiveDetailsBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InActiveDetailsFragment : BaseFragment<FragmentInActiveDetailsBinding>() {

    private var accountInformation: AccountInformation? = null
    private var personalInformation: PersonalInformation? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInActiveDetailsBinding =
        FragmentInActiveDetailsBinding.inflate(inflater, container, false)


    override fun init() {

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation = arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }
        val accountToBeClosedDate = Utils.convertOneFormatDateToAnotherFormat(
            accountInformation?.accountToBeClosedDate,
            "MM/dd/yyyy hh:mm:ss", "dd MMM yyyy"
        )

        binding.descTv.text =
            resources.getString(R.string.str_dart_charge_account_open_desc, accountToBeClosedDate)
        binding.notSureDescTv.text =
            resources.getString(R.string.str_i_am_not_sure_desc, accountToBeClosedDate)

        binding.radioButtonYes.setAccessibilityDelegate()
        binding.radioButtonNo.setAccessibilityDelegate()
        binding.radioButtonNotSure.setAccessibilityDelegate()

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonYes -> {
                    if (binding.radioButtonYes.isChecked) {
                        Log.e("TAG", "init: isChecked ->")
                        // Yes RadioButton is selected
                        binding.radioButtonNotSure.isChecked = false
                    }
                }

                R.id.radioButtonNo -> {
                    if (binding.radioButtonNo.isChecked) {
                        Log.e("TAG", "init: isChecked -->")
                        // No RadioButton is selected
                        binding.radioButtonNotSure.isChecked = false
                    }

                }
            }
            checkContinueButton()
        }

        binding.radioButtonNotSure.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e("TAG", "init: isChecked " + isChecked)
            if (isChecked) {
                binding.radioButtonNo.isChecked = false
                binding.radioButtonYes.isChecked = false
            }
            checkContinueButton()
        }

        binding.btnContinue.setOnClickListener {
            if (binding.radioButtonYes.isChecked) {
                showLoaderDialog()
                dashboardViewModel.changeInActiveStatusApi()
            } else if (binding.radioButtonNo.isChecked) {
                val bundle = Bundle()
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)

                findNavController().navigate(
                    R.id.action_inActiveDetailsFragment_to_closeAccountFragment,
                    bundle
                )
            } else if (binding.radioButtonNotSure.isChecked) {
                val bundle = Bundle()
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)

                findNavController().navigate(
                    R.id.action_inActiveDetailsFragment_to_inActiveNotSureFragment,
                    bundle
                )
            }
        }
        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
        observe(dashboardViewModel.changeInActiveStatusVal, ::handleChangeInActiveStatus)

    }

    private fun handleChangeInActiveStatus(status: Resource<EmptyApiResponse?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {
                val bundle = Bundle()
                bundle.putString(
                    Constants.NAV_FLOW_FROM,
                    Constants.IN_ACTIVE
                )

                bundle.putString(Constants.NAV_FLOW_KEY, Constants.IN_ACTIVE)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_inActiveDetailsFragment_to_resetForgotPassword,
                    bundle
                )
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun checkContinueButton() {
        if (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked || binding.radioButtonNotSure.isChecked) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }
}