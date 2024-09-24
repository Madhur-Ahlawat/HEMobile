package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidateInfoBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RevalidateInfoFragment : BaseFragment<FragmentRevalidateInfoBinding>() {

    private var paymentModel: CardListResponseModel? = null
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var paymentListSize: Int = 0
    private var position: Int = 0
    private var cardValidationFirstTime: Boolean = false
    private var cardValidationSecondTime: Boolean = false
    private var cardValidationExisting: Boolean = false
    private var cardValidationPaymentFail: Boolean = false
    private var paymentList: ArrayList<CardListResponseModel> = ArrayList()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidateInfoBinding =
        FragmentRevalidateInfoBinding.inflate(inflater, container, false)

    override fun init() {

        if (cardValidationPaymentFail) {
            binding.titleTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.warningicon, 0, 0)
            binding.titleTv.text =
                resources.getString(R.string.str_we_could_not_add_this_card)
            binding.cancelBtn.gone()
            binding.btnContinue.visible()
            if (cardValidationFirstTime) {
                binding.btnContinue.text = resources.getString(R.string.str_try_again)
                binding.descTv.text =
                    resources.getString(R.string.str_we_could_not_add_this_card_desc1)
            } else if (cardValidationSecondTime) {
                binding.btnContinue.text = resources.getString(R.string.str_continue)
                binding.descTv.text =
                    resources.getString(R.string.str_we_could_not_add_this_card_desc2)
            }

        } else {

            if (navFlowFrom == Constants.CARD_VALIDATION_LATER_DATE) {
                binding.titleTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.warningicon, 0, 0)
                binding.titleTv.text = resources.getString(R.string.str_important)
                binding.descTv.text = resources.getString(R.string.str_donot_have_valid_payment_method)
                binding.cancelBtn.visible()
                binding.btnContinue.visible()
            } else if (navFlowFrom == Constants.CARD_VALIDATION_REQUIRED) {
                if (accountInformation?.accSubType.equals(Constants.PAYG) && accountInformation?.status.equals(Constants.SUSPENDED, true)){
                    binding.titleTv.text = resources.getString(R.string.str_account_reopened)
                    binding.descTv.text =  Html.fromHtml(
                        getString(
                            R.string.str_we_have_sent_confirmation,
                            personalInformation?.emailAddress
                        ), Html.FROM_HTML_MODE_COMPACT
                    )
                }else{
                    binding.titleTv.text = resources.getString(R.string.str_payment_card_details_confirmed)
                    binding.descTv.text = resources.getString(R.string.str_payment_card_details_confirmed_desc1)
                }

                binding.cancelBtn.gone()
                binding.btnContinue.visible()
            }
        }
        binding.btnContinue.setOnClickListener {
            if (cardValidationPaymentFail) {
                if (cardValidationFirstTime) {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
                    bundle.putString(
                        Constants.NAV_FLOW_FROM,
                        Constants.CARD_VALIDATION_REQUIRED
                    )
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                    bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, false)
                    bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, true)

                    if (cardValidationExisting) {
                        bundle.putInt(Constants.POSITION, position)
                        bundle.putParcelableArrayList(
                            Constants.PAYMENT_LIST_DATA,
                            paymentList
                        )
                        findNavController().navigate(
                            R.id.action_reValidateInfoFragment_to_reValidateExistingPaymentFragment,
                            bundle
                        )
                    } else {

                        bundle.putDouble(Constants.DATA, 0.0)
                        bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)

                        findNavController().navigate(
                            R.id.action_reValidateInfoFragment_to_nmiPaymentFragment,
                            bundle
                        )
                    }
                } else if (cardValidationSecondTime) {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
                    bundle.putString(
                        Constants.NAV_FLOW_FROM,
                        Constants.CARD_VALIDATION_REQUIRED
                    )
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                    bundle.putDouble(Constants.DATA, 0.0)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentListSize)
                    bundle.putBoolean(Constants.CARD_VALIDATION_FIRST_TIME, false)
                    bundle.putBoolean(Constants.CARD_VALIDATION_SECOND_TIME, true)

                    findNavController().navigate(
                        R.id.action_reValidateInfoFragment_to_nmiPaymentFragment,
                        bundle
                    )
                }
            } else {
                requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                    putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                    putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                }
            }

        }

        binding.cancelBtn.setOnClickListener {
            if (navFlowFrom == Constants.CARD_VALIDATION_LATER_DATE) {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
                bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_REQUIRED)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putDouble(Constants.DATA, 0.0)

                findNavController().navigate(
                    R.id.action_reValidateInfoFragment_to_nmiPaymentFragment,
                    bundle
                )
            }
        }
        if(requireActivity() is AuthActivity){
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }

        if(backButton){
            if(requireActivity() is AuthActivity){
                (requireActivity() as AuthActivity).showBackButton()
            }
        }
    }

    override fun initCtrl() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBarHome()
        } else if (requireActivity() is AuthActivity) {
            (requireActivity() as AuthActivity).focusToolBarAuth()
        }

        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.PAYMENT_LIST_DATA) != null) {
            paymentList =
                arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA) ?: ArrayList()
        }
        if (arguments?.containsKey(Constants.PAYMENT_METHOD_SIZE) == true) {
            paymentListSize = arguments?.getInt(Constants.PAYMENT_METHOD_SIZE) ?: 0
        }

        if (arguments?.containsKey(Constants.CARD_VALIDATION_FIRST_TIME) == true) {
            cardValidationFirstTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_FIRST_TIME) ?: false
        }

        if (arguments?.containsKey(Constants.CARD_VALIDATION_SECOND_TIME) == true) {
            cardValidationSecondTime =
                arguments?.getBoolean(Constants.CARD_VALIDATION_SECOND_TIME) ?: false
        }
        if (arguments?.containsKey(Constants.CARD_VALIDATION_EXISTING_CARD) == true) {
            cardValidationExisting =
                arguments?.getBoolean(Constants.CARD_VALIDATION_EXISTING_CARD) ?: false
        }


        if (arguments?.containsKey(Constants.CARD_VALIDATION_PAYMENT_FAIL) == true) {
            cardValidationPaymentFail =
                arguments?.getBoolean(Constants.CARD_VALIDATION_PAYMENT_FAIL) ?: false
        }

        if (arguments?.containsKey(Constants.POSITION) == true) {
            position =
                arguments?.getInt(Constants.POSITION) ?: 0
        }


        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }
        Log.e("TAG", "initCtrl:-)-> ")

        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }

        if (arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA) != null) {
            paymentModel = arguments?.getParcelable(Constants.PAYMENT_DATA)

        }

    }

    override fun observer() {

    }

}