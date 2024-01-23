package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodEditModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodEditResponse
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.data.model.profile.ReplenishmentInformation
import com.conduent.nationalhighways.databinding.FragmentPaymentMethod2Binding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.adapter.PaymentMethodAdapter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewPaymentMethodFragment : BaseFragment<FragmentPaymentMethod2Binding>(),
    PaymentMethodAdapter.PaymentMethodCallback, View.OnClickListener {
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var directDebitPaymentList: ArrayList<CardListResponseModel?>? = ArrayList()
    private var cardPaymentList: ArrayList<CardListResponseModel?>? = ArrayList()
    private val viewModel: PaymentMethodViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var rowId: String = ""
    private var makeDefault: Boolean = false
    private var isDirectDebitDelete: Boolean = false
    private var loader: LoaderDialog? = null
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private lateinit var title: AppCompatTextView
    private var position: Int = 0
    private var accountNumber: String = ""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentMethod2Binding =
        FragmentPaymentMethod2Binding.inflate(inflater, container, false)


    override fun initCtrl() {

        if(arguments?.containsKey(Constants.PERSONALDATA) == true){
            if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
                personalInformation =
                    arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)
            }

        }
        if(arguments?.containsKey(Constants.ACCOUNTINFORMATION) == true){
            if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
                accountInformation =
                    arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION)
            }

        }
        paymentList = ArrayList()
        directDebitPaymentList = ArrayList()
        cardPaymentList = ArrayList()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if(requireActivity() is HomeActivityMain){
            title = HomeActivityMain.dataBinding?.titleTxt!!
        }

        Log.e("TAG", "initCtrl: isViewCreated "+isViewCreated )
        if (!isViewCreated) {
            showLoader()
            viewModel.saveCardList()
            dashboardViewModel.getAccountDetailsData()

        }
        binding.paymentRecycleView.layoutManager = LinearLayoutManager(requireContext())

        paymentMethodAdapter = PaymentMethodAdapter(requireContext(), paymentList, this)
        binding.paymentRecycleView.adapter = paymentMethodAdapter


        isViewCreated = false

        binding.addNewPaymentMethod.setOnClickListener(this)
        binding.cardViewTopYourBalance.setOnClickListener(this)
        binding.cardViewThresholdLimit.setOnClickListener(this)
    }

    override fun init() {
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.savedCardList, ::handleSaveCardResponse)
            observe(dashboardViewModel.accountOverviewVal, ::handleAccountDetails)
            observe(viewModel.defaultCard, ::handleDefaultCardResponse)


            viewModel.deleteCardState.collect {
                handleDeleteCardResponse(it)
            }


        }
    }


    private fun handleAccountDetails(status: Resource<ProfileDetailModel?>?) {
        Log.e("TAG", "handleAccountDetails: response")
        hideLoader()
        when (status) {
            is Resource.Success -> {
                Log.e("TAG", "handleAccountDetails: data"+status.data?.accountInformation)
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation
                dashboardViewModel.accountSubType.value = accountInformation?.accSubType
                dashboardViewModel.personalInformationData.value = personalInformation
                dashboardViewModel.accountInformationData.value = accountInformation

                if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                    binding.cardViewThresholdLimit.visibility = View.GONE
                    binding.cardViewTopYourBalance.visibility = View.GONE
                } else {
                    binding.cardViewThresholdLimit.visibility = View.VISIBLE
                    binding.cardViewTopYourBalance.visibility = View.VISIBLE
                }
            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(status.errorModel)
                }
            }

            else -> {
            }
        }

    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        hideLoader()
        when (status) {
            is Resource.Success -> {

                paymentList?.clear()
                status.data?.let {
                    it.creditCardListType?.let {
                        it.cardsList?.let {
                            it.forEach {
                                if ((it?.bankAccountType.equals("CURRENT") && it?.emandateStatus.equals(
                                        "ACTIVE"
                                    ) || it?.primaryCard == true)
                                ) {
                                    paymentList?.add(0, it!!)
                                } else {
                                    paymentList?.add(it)
                                }

                            }
                        }
                    }
                }

                directDebitPaymentList = (paymentList?.filter { it?.bankAccount == true }
                    ?: ArrayList()) as ArrayList<CardListResponseModel?>?
                dashboardViewModel.directDebitCardListSize.value =
                    directDebitPaymentList.orEmpty().size
                dashboardViewModel.paymentListSize.value = paymentList.orEmpty().size
                cardPaymentList = (paymentList?.filter { it?.bankAccount == false }
                    ?: ArrayList()) as ArrayList<CardListResponseModel?>?

                for (i in 0 until paymentList.orEmpty().size) {
                    checkNullValuesOfModel(paymentList?.get(i))
                }

                if (paymentList?.isNotEmpty() == true) {
                    binding.paymentMethodInformation.visible()
                    binding.paymentRecycleView.visible()

                    binding.warningIcon.gone()
                    binding.maximumVehicleAdded.gone()
                    binding.textMaximumVehicle.gone()

                    paymentMethodAdapter.updateList(paymentList)



                } else {
                    binding.paymentMethodInformation.gone()
                    binding.paymentRecycleView.gone()
                    binding.warningIcon.visible()
                    binding.maximumVehicleAdded.visible()
                    binding.textMaximumVehicle.visible()
                }


            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
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

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.addNewPaymentMethod -> {

                if(paymentList.orEmpty().size==2){

                    displayCustomMessage(
                        resources.getString(R.string.str_max_payment_methods),
                        resources.getString(R.string.str_max_payment_methods_desc),
                        resources.getString(R.string.str_continue),
                        resources.getString(R.string.str_continue),
                        object : DialogPositiveBtnListener {
                            override fun positiveBtnClick(dialog: DialogInterface) {
                                dialog.dismiss()
                            }
                        },
                        object : DialogNegativeBtnListener {
                            override fun negativeBtnClick(dialog: DialogInterface) {
                                dialog.dismiss()
                            }
                        }
                    ,cancelVisibility=View.GONE, lineView = true)

                }else{
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.ADD_PAYMENT_METHOD)
                    if(navFlowCall.equals(Constants.SUSPENDED)){
                        bundle.putString(Constants.NAV_FLOW_FROM, Constants.PAYG_SUSPENDED)
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                        bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                    }
                    bundle.putDouble(Constants.DATA, 0.0)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                        if (directDebitPaymentList.orEmpty().size == 1) {
                            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, true)
                        } else {
                            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, false)
                        }

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_nmiPaymentFragment,
                            bundle
                        )

                    } else {
                        bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

                        if (directDebitPaymentList.orEmpty().size == 1) {
                            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, true)
                            findNavController().navigate(
                                R.id.action_paymentMethodFragment_to_nmiPaymentFragment,
                                bundle
                            )
                        } else {
                            bundle.putBoolean(Constants.IS_DIRECT_DEBIT, false)
                            findNavController().navigate(
                                R.id.action_paymentMethodFragment_to_selectPaymentMethodFragment,
                                bundle
                            )
                        }


                    }


                }
            }

            R.id.cardViewTopYourBalance -> {
                if(requireActivity() is HomeActivityMain) {
                    title.text = getString(R.string.top_up)
                }

                val bundle = Bundle()
                if (accountInformation?.status.equals(Constants.SUSPENDED, true)) {
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.SUSPENDED)
                }else{
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                }
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)
                findNavController().navigate(
                    R.id.action_paymentMethodFragment_to_accountSuspendedPaymentFragment,
                    bundle
                )


            }

            R.id.cardViewThresholdLimit -> {
                if(requireActivity() is HomeActivityMain) {
                    title.text = getString(R.string.set_threshold_limit)
                }
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.THRESHOLD)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)

                findNavController().navigate(
                    R.id.action_paymentMethodFragment_to_topUpFragment,
                    bundle
                )

            }
        }
    }

    override fun paymentMethodCallback(position: Int, value: String) {
        if (value == Constants.DELETE_CARD) {
            accountNumber = paymentList?.get(position)?.cardNumber.toString()
            this.position = position
            val bundle = Bundle()
            if (paymentList?.get(position)?.primaryCard == true) {
            } else {
                isDirectDebitDelete = false
            }

            if (paymentList.orEmpty().size >= 2) {
                var expMonth = ""
                if (paymentList?.get(position)?.expMonth?.length!! < 2) {
                    expMonth = "0" + paymentList?.get(position)?.expMonth
                } else {
                    expMonth = paymentList?.get(position)?.expMonth!!
                }
                deletePaymentDialog(
                    getString(R.string.str_remove_payment_method),
                    paymentList?.get(position)?.rowId,
                    getString(
                        R.string.str_are_you_sure_you_want_to_remove_payment_method,
                        Utils.setStarmaskcardnumber(
                            requireActivity(),
                            paymentList?.get(position)?.cardNumber
                        ),

                    expMonth + "/" + if (paymentList?.get(
                                position
                            )?.expYear!!.length > 2
                        ) paymentList?.get(
                            position
                        )?.expYear!!.substring(
                            2,
                            paymentList?.get(position)?.expYear!!.length
                        ) else paymentList?.get(position)?.expYear!!
                    )
                )
            } else {
                if (paymentList.orEmpty().size == 1) {
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYG)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    } else {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PRE_PAY_ACCOUNT)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)
                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    }
                } else {
                    if (paymentList.orEmpty().size > 1) {
                        rowId = paymentList?.get(position)?.rowId ?: ""
                        showLoader()
                        viewModel.deleteCardState(PaymentMethodDeleteModel(rowId))
                    }

                }

            }

        } else if (value == Constants.DIRECT_DEBIT) {
            isDirectDebitDelete = true
            this.position = position

            accountNumber = paymentList?.get(position)?.bankAccountNumber.toString()
            if (paymentList?.get(position)?.primaryCard == true) {
                val bundle = Bundle()

                if (paymentList.orEmpty().size == 1) {
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYG)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    } else {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PRE_PAY_ACCOUNT)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    }
                } else {
                    if (paymentList.orEmpty().size > 1) {
                        rowId = paymentList?.get(position)?.rowId ?: ""
                        deletePaymentDialog(
                            getString(R.string.str_remove_payment_method),
                            paymentList?.get(position)?.rowId,
                            getString(
                                R.string.str_are_you_sure_you_want_to_remove_direct_payment_method,
                                paymentList?.get(position)?.bankAccountNumber
                            )
                        )


                    }

                }


            } else {
                this.position = position
                isDirectDebitDelete = true
                deletePaymentDialog(
                    getString(R.string.str_remove_payment_method),
                    paymentList?.get(position)?.rowId,
                    getString(
                        R.string.str_are_you_sure_you_want_to_remove_direct_payment_method,
                        paymentList?.get(position)?.cardNumber
                    )
                )


            }

        } else if (value == Constants.MAKE_DEFAULT) {
            makeDefault = true
            hideLoader()
            showLoader()
            makeSecondaryCardAsPrimary(
                paymentList?.get(position)?.cardType,
                paymentList?.get(position)?.rowId
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoader()
    }

    private fun checkNullValuesOfModel(model: CardListResponseModel?) {
        if (model?.check == null) {
            model?.check = false
        }
        if (model?.isSelected == null) {
            model?.isSelected = false
        }
        if (model?.bankRoutingNumber == null) {
            model?.bankRoutingNumber = ""
        }
        if (model?.cardType == null) {
            model?.cardType = ""
        }
        if (model?.cardNumber == null) {
            model?.cardNumber = ""
        }
        if (model?.middleName == null) {
            model?.middleName = ""
        }
        if (model?.expMonth == null) {
            model?.expMonth = ""
        }
        if (model?.expYear == null) {
            model?.expYear = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }

        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.rowId == null) {
            model?.rowId = ""
        }
        if (model?.bankAccountType == null) {
            model?.bankAccountType = ""
        }
        if (model?.bankAccountNumber == null) {
            model?.bankAccountNumber = ""
        }
        if (model?.firstName == null) {
            model?.firstName = ""
        }
        if (model?.lastName == null) {
            model?.lastName = ""
        }
        if (model?.customerVaultId == null) {
            model?.customerVaultId = ""
        }
        if (model?.addressLine1 == null) {
            model?.addressLine1 = ""
        }
        if (model?.city == null) {
            model?.city = ""
        }
        if (model?.state == null) {
            model?.state = ""
        }
        if (model?.zipCode == null) {
            model?.zipCode = ""
        }
        if (model?.country == null) {
            model?.country = ""
        }
        if (model?.emandateStatus == null) {
            model?.emandateStatus = ""
        }
        if (model?.paymentSeqNumber == null) {
            model?.paymentSeqNumber = 0
        }
        if (model?.bankAccount == null) {
            model?.bankAccount = false
        }
    }

    private fun makeSecondaryCardAsPrimary(cardType: String?, rowId: String?) {
        val paymentMethodEditModel = PaymentMethodEditModel(

            cardType = cardType,

            easyPay = "Y",

            paymentType = "card",
            primaryCard = "Y",
            rowId = rowId
        )

        viewModel.editDefaultCard(paymentMethodEditModel)
    }

    override fun onResume() {
        if(requireActivity() is HomeActivityMain){
            title.text = getString(R.string.payment_management)
        }
        super.onResume()
    }

    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        Log.e("TAG", "handleDeleteCardResponse() called with: status = $status")
        hideLoader()

        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true || status.data?.statusCode?.equals(
                        "1209"
                    ) == true
                ) {
                    ErrorUtil.showError(binding.root, status.data.message)
                    return
                }
                val bundle = Bundle()

                if (isDirectDebitDelete) {
                    bundle.putString(
                        Constants.CARD_IS_ALREADY_REGISTERED,
                        Constants.DIRECT_DEBIT_DELETE
                    )
                    bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                    bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                    findNavController().navigate(
                        R.id.paymentMethodFragment_to_action_paymentSuccessFragment,
                        bundle
                    )
                } else {
                    bundle.putString(Constants.CARD_IS_ALREADY_REGISTERED, Constants.DELETE_CARD)
                    bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                    bundle.putString(Constants.ACCOUNT_NUMBER, accountNumber)

                    findNavController().navigate(
                        R.id.paymentMethodFragment_to_action_paymentSuccessFragment,
                        bundle
                    )
                }


            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }

            else -> {
            }


        }
        lifecycleScope.launch {
            viewModel._deleteCard_State.emit(null)
        }

    }

    private fun handleDefaultCardResponse(status: Resource<PaymentMethodEditResponse?>?) {
        hideLoader()
        when (status) {
            is Resource.Success -> {
                showLoader()
                Log.e("TAG", "handleDefaultCardResponse: makeDefault " + makeDefault)
                if (makeDefault) {
                    viewModel.saveCardList()
                } else {
                    viewModel.deleteCardState(PaymentMethodDeleteModel(rowId))

                }


            }

            is Resource.DataError -> {
                if ((status.errorModel?.errorCode == Constants.TOKEN_FAIL && status.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || status.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
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

    private fun deletePaymentDialog(
        title: String,
        rowId_: String?,
        message: String,

        ) {

        displayCustomMessage(title,
            message,
            getString(R.string.cancel),
            getString(R.string.delete),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    dialog.dismiss()

                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    showLoader()
                    viewModel.deleteCardState(PaymentMethodDeleteModel(rowId_))
                }
            })
    }

    fun showLoader() {
        val fragmentManager = requireActivity().supportFragmentManager
        val existingFragment = fragmentManager.findFragmentByTag(Constants.LOADER_DIALOG)

        if (existingFragment == null) {
            // Fragment is not added, add it now
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomLoaderDialog)
            loader?.show(fragmentManager, Constants.LOADER_DIALOG)
        }
    }

    fun hideLoader() {
        if (loader?.isVisible == true) {
            loader?.dismiss()
            loader = null
        }
    }

    companion object {
        var isDirectDebit: Boolean = false
    }

}


