package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTypesBinding
import com.conduent.nationalhighways.databinding.FragmentPayForCrossingsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.viewcharges.ViewChargesActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PayForCrossingsFragment : BaseFragment<FragmentPayForCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {

    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPayForCrossingsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {
        binding.inputCountry.dropDownItemSelectListener = this
        binding.btnAdditionalCrossing.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAdditionalCrossing -> {
                NewCreateAccountRequestModel.prePay = true
                handleNavigation()
            }
            R.id.btnNext -> {
                NewCreateAccountRequestModel.prePay=false
                handleNavigation()
            }
        }
    }

    private fun handleNavigation() {
        when(navFlowCall){

            EDIT_SUMMARY -> {findNavController().popBackStack()}
            else -> {val bundle=Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.ACCOUNT_CREATION_EMAIL_FLOW)
                findNavController().navigate(
                    R.id.action_createAccountTypes_to_forgotPasswordFragment,
                    bundle
                )}

        }
    }

    override fun onRetryClick() {

    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {

    }
}