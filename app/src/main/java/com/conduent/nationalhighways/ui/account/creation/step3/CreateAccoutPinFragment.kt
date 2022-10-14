package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPinBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.PAYG
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccoutPinFragment : BaseFragment<FragmentCreateAccountPinBinding>(),
    View.OnClickListener {

    private var model: CreateAccountRequestModel? = null
    private var isEditAccountType: Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPinBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = false
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType =
                arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)
        Logg.logging("AccountCreatePin", " step 3  model  $model")
        when (model?.planType) {
            PAYG -> {
                binding.tvLabel.text = getString(R.string.pay_as_you_go)
            }
            Constants.BUSINESS_ACCOUNT -> {
                binding.tvLabel.text = getString(R.string.business_prepay_account)
            }
            else -> {
                binding.tvLabel.text = getString(R.string.personal_pre_pay_account)
            }
        }
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccoutPinFragment)
            tvPinOne.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinTwo.requestFocus()
                checkButton()
            }
            tvPinTwo.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinThree.requestFocus()
                else binding.tvPinOne.requestFocus()
                checkButton()
            }
            tvPinThree.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinFour.requestFocus()
                else binding.tvPinTwo.requestFocus()

                checkButton()
            }
            tvPinFour.doAfterTextChanged {
                if (it?.isNotEmpty() == true) hideKeyboard()
                else binding.tvPinThree.requestFocus()
                checkButton()
            }
            showHide.setOnClickListener {

                if (show) {
                    show = false
                    showHide.setImageResource(R.drawable.ic_invisible)
                    tvPinOne.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    tvPinTwo.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    tvPinThree.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    tvPinFour.transformationMethod = HideReturnsTransformationMethod.getInstance()

                } else {
                    show = true
                    showHide.setImageResource(R.drawable.ic_visible)
                    tvPinOne.transformationMethod = PasswordTransformationMethod.getInstance()
                    tvPinTwo.transformationMethod = PasswordTransformationMethod.getInstance()
                    tvPinThree.transformationMethod = PasswordTransformationMethod.getInstance()
                    tvPinFour.transformationMethod = PasswordTransformationMethod.getInstance()


                }

            }
        }
    }

    private var show = true

    private fun checkButton() {
        binding.enable = binding.tvPinOne.text.toString().isNotEmpty() &&
                binding.tvPinTwo.text.toString().isNotEmpty() &&
                binding.tvPinThree.text.toString().isNotEmpty() &&
                binding.tvPinFour.text.toString().isNotEmpty()

    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                model?.digitPin =
                    binding.tvPinOne.text.toString() + "" + binding.tvPinTwo.text.toString() + "" + binding.tvPinThree.text.toString() + "" + binding.tvPinFour.text.toString()
                Logg.logging("AccountCreatePin", "  click step 3  model  $model")

                val bundle = Bundle().apply {
                    putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
                }
                isEditAccountType?.let {
                    findNavController().navigate(
                        R.id.action_createAccoutPinFragment_to_paymentSummaryFragment,
                        bundle
                    )

                } ?: run {
                    findNavController().navigate(
                        R.id.action_createAccoutPinFragment_to_createCommunicationPrefsFragment,
                        bundle
                    )
/*
                    when(model?.planType){
                        PAYG ->{   findNavController().navigate(R.id.action_createAccoutPinFragment_to_findYourVehicleFragment, bundle)  }
                        Constants.BUSINESS_ACCOUNT -> { findNavController().navigate(R.id.action_createAccountPinFragment_to_businessVehicleTitleFragment, bundle) }
                        else ->{   findNavController().navigate(R.id.action_createAccoutPinFragment_to_findYourVehicleFragment, bundle) }
                    }
*/
                }
            }
        }
    }
}