package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.setAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RevalidatePaymentCardFragment : BaseFragment<FragmentRevalidatePaymentCardBinding>() {

    private var accountInformation: AccountInformation? = null
    private var personalInformation: PersonalInformation? = null
    private var paymentList: ArrayList<CardListResponseModel?>? = ArrayList()
    private var position: Int = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidatePaymentCardBinding =
        FragmentRevalidatePaymentCardBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation = arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation = arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.PAYMENT_LIST_DATA) != null) {
            paymentList = arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA)
        }



        if (accountInformation?.accountType.equals(
                "BUSINESS",
                true
            ) || ((accountInformation?.accSubType.equals(
                "STANDARD", true
            ) && accountInformation?.accountType.equals(
                "PRIVATE", true
            )))
        ) {
            binding.desc2Tv.gravity = Gravity.START

            //private account
            binding.radioGroupYesNo.visible()
            binding.desc2Tv.text = resources.getString(R.string.str_wantto_validate_card_now)
            binding.descTv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_prepay)
            binding.desc3Tv.visible()


            checkContinueButton()
        } else {
            //payg account
            binding.desc2Tv.gravity = Gravity.CENTER
            binding.radioGroupYesNo.gone()
            binding.desc3Tv.gone()
            binding.btnContinue.enable()
            if (accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                binding.titleTv.text =
                    resources.getString(R.string.str_provide_payment_card_details)

                binding.descTv.text =
                    resources.getString(R.string.str_suspend_revalidate_desc)
                binding.desc2Tv.text =
                    resources.getString(R.string.str_revalidate_payment_card_details_desc4)
            } else {
                binding.titleTv.text =
                    resources.getString(R.string.str_revalidate_payment_card_details)
                binding.desc2Tv.text =
                    resources.getString(R.string.str_revalidate_payment_card_details_desc2)
                binding.descTv.text =
                    resources.getString(R.string.str_revalidate_payment_card_details_payg)
            }
        }

        binding.radioButtonYes.setAccessibilityDelegate()
        binding.radioButtonNo.setAccessibilityDelegate()


        if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }


    }

    private fun checkContinueButton() {
        if (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }

    override fun initCtrl() {

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, _ ->
            checkContinueButton()
        }

        binding.btnContinue.setOnClickListener {
            if (accountInformation?.accountType.equals(
                    "BUSINESS",
                    true

                ) || ((accountInformation?.accSubType.equals(
                    "STANDARD", true
                ) && accountInformation?.accountType.equals(
                    "PRIVATE", true
                )))
            ) {
                if (binding.radioButtonNo.isChecked) {

                    val bundle = Bundle()
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, true)
                    bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_LATER_DATE)
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_LATER_DATE)
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                    findNavController().navigate(
                        R.id.action_reValidatePaymentCardFragment_to_reValidateInfoFragment,
                        bundle
                    )

                } else {
                    if (paymentList.orEmpty().isNotEmpty()) {
                        redirectToDetailsPage()
                    } else {
                        redirectToNMIPage()
                    }
                }
            } else {
                redirectToNMIPage()
            }
        }
    }

    private fun redirectToNMIPage() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
        bundle.putString(
            Constants.NAV_FLOW_FROM,
            Constants.CARD_VALIDATION_REQUIRED
        )
        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
        bundle.putDouble(Constants.DATA, 0.0)
        bundle.putInt(Constants.POSITION, position)
        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)
        bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, true)
        bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, false)

        findNavController().navigate(
            R.id.action_reValidatePaymentCardFragment_to_nmiPaymentFragment,
            bundle
        )

    }

    private fun redirectToDetailsPage() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_REQUIRED)
        bundle.putParcelableArrayList(Constants.PAYMENT_LIST_DATA, paymentList as ArrayList)
        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
        bundle.putInt(Constants.POSITION, position)
        findNavController().navigate(
            R.id.action_reValidatePaymentCardFragment_to_reValidatePaymentCardDetailsFragment,
            bundle
        )
    }

    override fun observer() {

    }

}