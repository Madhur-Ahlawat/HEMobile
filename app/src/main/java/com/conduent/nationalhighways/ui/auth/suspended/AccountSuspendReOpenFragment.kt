package com.conduent.nationalhighways.ui.auth.suspended

import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltReopenedBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.invisible
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountSuspendReOpenFragment : BaseFragment<FragmentAccountSuspendHaltReopenedBinding>(),
    View.OnClickListener {
    private var responseModel: CardResponseModel? = null
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null
    private var currentBalance: String = ""
    private var transactionId: String = ""
    private var navFlow: String = ""
    private var topUpAmount: String = ""
    private var newCard: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltReopenedBinding =
        FragmentAccountSuspendHaltReopenedBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).focusToolBar()
        }else if(requireActivity() is AuthActivity){
            (requireActivity() as AuthActivity).focusToolBar()
        }
        Utils.validationsToShowRatingDialog(requireActivity(), sessionManager)
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()
        transactionId = arguments?.getString(Constants.TRANSACTIONID).toString()
        topUpAmount = arguments?.getString(Constants.TOP_UP_AMOUNT) ?: ""

        if (arguments?.containsKey(Constants.NEW_CARD) == true) {
            newCard = arguments?.getBoolean(Constants.NEW_CARD, false) ?: false
        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation =
                arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA) != null) {
            responseModel = arguments?.getParcelable<CardResponseModel>(Constants.DATA)
            if (navFlowFrom == Constants.PAYG_SUSPENDED) {
                binding.cardView.gone()
                binding.succesfulCardAdded.gone()
            } else
                if (responseModel?.checkCheckBox == true) {
                    binding.cardView.visible()
                    binding.succesfulCardAdded.visible()
                } else {
                    binding.cardView.gone()
                    binding.succesfulCardAdded.gone()
                }

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText =
                Html.fromHtml(responseModel?.card?.type?.uppercase() + "<br>" + responseModel?.card?.number?.let {
                    Utils.maskCardNumber(
                        it
                    )
                }, Html.FROM_HTML_MODE_COMPACT)

            binding.tvSelectPaymentMethod.text = htmlText


        } else {
            /* binding.succesfulCardAdded.gone()

             binding.cardView.gone()*/

        }

        if (navFlowFrom == Constants.PAYG_SUSPENDED) {
            binding.tvYouWillAlsoNeed.invisible()
            binding.feedbackBt.invisible()
        } else {
            binding.feedbackBt.visible()
            if (currentBalance.isNotEmpty()) {
                val balance = currentBalance.replace("£", "")
                val doubleBalance = balance.toDouble()
                val intBalance = doubleBalance.toInt()
                val finalCurrentBalance = 5.00 - doubleBalance
                if (finalCurrentBalance < 5.00) {
                    binding.tvYouWillAlsoNeed.visible()
                } else {
                    binding.tvYouWillAlsoNeed.gone()

                }
            }
        }


        binding.tvYouWillNeedToPay.text = Html.fromHtml(
            getString(
                R.string.str_we_have_sent_confirmation,
                personalInformation?.emailAddress
            ), Html.FROM_HTML_MODE_COMPACT
        )



        binding.referenceNumberTv.text = transactionId

        if (navFlowCall == Constants.PAYMENT_TOP_UP) {
            binding.tvAccountSuspended.text = getString(
                R.string.str_balance_topped_up_with,
                getString(R.string.pound_symbol) + topUpAmount
            )
            binding.tvYouWillNeedToPay.gone()
            binding.tvYouWillAlsoNeed.gone()
            binding.btnTopUpNow.text = getString(R.string.str_continue)
            HomeActivityMain.setTitle(getString(R.string.top_up_success))
            binding.layoutPaymentReferenceNumber.visible()
        } else {
            binding.btnTopUpNow.text = getString(R.string.str_go_to_dashboard)

            binding.tvAccountSuspended.text = getString(R.string.str_account_reopened)
            if (navFlowFrom == Constants.PAYG_SUSPENDED) {
                binding.layoutPaymentReferenceNumber.invisible()
                binding.tvYouWillAlsoNeed.invisible()
            } else {
                binding.layoutPaymentReferenceNumber.visible()
                binding.tvYouWillAlsoNeed.text = getString(R.string.str_you_have_less_than, "£5.00")
                binding.tvYouWillAlsoNeed.visible()
            }
        }

        if(arguments?.getString(Constants.CARD_IS_ALREADY_REGISTERED).equals(Constants.CARD_IS_ALREADY_REGISTERED)){
            binding.layoutCardAlreadyExists.visible()
            binding.succesfulCardAdded.gone()
            binding.cardView.gone()
        }
        else{
            binding.layoutCardAlreadyExists.gone()
        }
    }

    override fun init() {
        binding.btnTopUpNow.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnTopUpNow -> {
                when (binding.btnTopUpNow.text) {
                    getString(R.string.str_go_to_dashboard) ->

                        requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java) {
                            putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                            putBoolean(Constants.FIRST_TYM_REDIRECTS, true)
                        }

                    getString(R.string.str_continue) -> {
                        if (navFlowFrom.equals(Constants.DASHBOARD)) {
                            findNavController().navigate(R.id.accountSuspendReOpenFragment_to_dashboardFragment)
                        } else {
                            findNavController().navigate(R.id.accountSuspendReOpenFragment_to_paymentMethodFragment)
                        }

                    }
                }


            }
        }
    }
}