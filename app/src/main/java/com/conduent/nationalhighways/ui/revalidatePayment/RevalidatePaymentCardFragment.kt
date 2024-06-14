package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RevalidatePaymentCardFragment : BaseFragment<FragmentRevalidatePaymentCardBinding>() {

    private var accountInformation: AccountInformation? = null
    private var personalInformation: PersonalInformation? = null
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private val viewModel: PaymentMethodViewModel by viewModels()

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

        if (accountInformation?.accountType.equals(
                "BUSINESS",
                true
            ) || ((accountInformation?.accSubType.equals(
                "STANDARD", true
            ) && accountInformation?.accountType.equals(
                "PRIVATE", true
            )))
        ) {
            //private account
            binding.radioGroupYesNo.visible()
            binding.desc2Tv.text = resources.getString(R.string.str_wantto_validate_card_now)
            binding.descTv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_prepay)
            binding.desc3Tv.visible()
            checkContinueButton()
        } else {
            //payg account

            binding.radioGroupYesNo.gone()
            binding.desc2Tv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_desc2)
            binding.descTv.text =
                resources.getString(R.string.str_revalidate_payment_card_details_payg)
            binding.desc3Tv.gone()
            binding.btnContinue.enable()
        }

        showLoaderDialog()
        viewModel.saveCardListState()
    }

    private fun checkContinueButton() {
        if (binding.radioButtonYes.isChecked || binding.radioButtonNo.isChecked) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }

    override fun initCtrl() {

        binding.radioGroupYesNo.setOnCheckedChangeListener { _, checkedId ->
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
                    bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
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
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
                        bundle.putString(
                            Constants.NAV_FLOW_FROM,
                            Constants.CARD_VALIDATION_REQUIRED
                        )
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                        bundle.putDouble(Constants.DATA, 0.0)
                        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)

                        findNavController().navigate(
                            R.id.action_reValidatePaymentCardFragment_to_nmiPaymentFragment,
                            bundle
                        )
                    }

                }
            } else {
                redirectToDetailsPage()
            }
        }
    }

    private fun redirectToDetailsPage() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_REQUIRED)
        bundle.putParcelableArrayList(Constants.DATA, paymentList as ArrayList)
        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
        findNavController().navigate(
            R.id.action_reValidatePaymentCardFragment_to_reValidatePaymentCardDetailsFragment,
            bundle
        )
    }

    override fun observer() {
        lifecycleScope.launch {
            viewModel.savedCardState.collect {
                handleSaveCardResponse(it)
            }
        }
    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {
                paymentList?.clear()
                for (i in 0 until status.data?.creditCardListType?.cardsList.orEmpty().size) {
                    if (status.data?.creditCardListType?.cardsList?.get(i)?.bankAccount == false) {
                        paymentList?.add(status.data.creditCardListType.cardsList[i])
                    }
                }
                for (i in 0 until paymentList.orEmpty().size) {
                    Utils.checkNullValuesOfModel(paymentList?.get(i))
                }

                if (paymentList.orEmpty().size == 1) {
                    paymentList?.get(0)?.primaryCard = true
                }

                lifecycleScope.launch {
                    viewModel._savedCardListState.emit(null)
                }


            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }
        }
    }

}