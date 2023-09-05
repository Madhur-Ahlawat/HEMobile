package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountInformation
import com.conduent.nationalhighways.data.model.account.AccountResponse
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.ReplenishmentInformation
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodEditModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodEditResponse
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentPaymentMethod2Binding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.adapter.PaymentMethodAdapter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
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
    private val viewModel: PaymentMethodViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var rowId: String = ""
    private var makeDefault: Boolean = false
    private var isDirectDebitDelete: Boolean = false
    private var loader: LoaderDialog? = null
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null
    private lateinit var title: TextView
    private var position: Int = 0
    private var accountNumber:String=""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentMethod2Binding =
        FragmentPaymentMethod2Binding.inflate(inflater, container, false)


    override fun initCtrl() {
        paymentList = ArrayList()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        title = requireActivity().findViewById(R.id.title_txt)


        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
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
            observe(viewModel.deleteCard, ::handleDeleteCardResponse)

            observe(viewModel.defaultCard, ::handleDefaultCardResponse)

        }
    }


    private fun handleAccountDetails(status: Resource<AccountResponse?>?) {

        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                personalInformation = status.data?.personalInformation
                accountInformation = status.data?.accountInformation
                replenishmentInformation = status.data?.replenishmentInformation

                if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                    binding.cardViewThresholdLimit.visibility = View.GONE
                    binding.cardViewTopYourBalance.visibility = View.GONE

                } else {
                    binding.cardViewThresholdLimit.visibility = View.VISIBLE
                    binding.cardViewTopYourBalance.visibility = View.VISIBLE
                }

            }

            is Resource.DataError -> {
            }

            else -> {
            }
        }

    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                paymentList = status.data?.creditCardListType?.cardsList
                if (paymentList?.isNotEmpty() == true) {
                    binding.paymentMethodInformation.visible()
                    binding.paymentRecycleView.visible()

                    binding.warningIcon.gone()
                    binding.maximumVehicleAdded.gone()
                    binding.textMaximumVehicle.gone()

                    paymentMethodAdapter.updateList(paymentList)

                    if ((paymentList?.size ?: 0) < 2) {
                        binding.addNewPaymentMethod.visibility = View.VISIBLE

                    } else {
                        binding.addNewPaymentMethod.visibility = View.GONE

                    }


                } else {
                    binding.paymentMethodInformation.gone()
                    binding.paymentRecycleView.gone()

                    binding.warningIcon.visible()
                    binding.maximumVehicleAdded.visible()
                    binding.textMaximumVehicle.visible()
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.addNewPaymentMethod -> {
                val bundle = Bundle()

                if (accountInformation?.accSubType.equals(Constants.PAYG)) {
                    bundle.putString(Constants.NAV_FLOW_KEY, Constants.ADD_PAYMENT_METHOD)
                    bundle.putDouble(Constants.DATA, 0.0)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList?.size ?: 0)


                    findNavController().navigate(
                        R.id.action_paymentMethodFragment_to_nmiPaymentFragment,
                        bundle
                    )

                } else {
                    bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList?.size ?: 0)


                    findNavController().navigate(
                        R.id.action_paymentMethodFragment_to_selectPaymentMethodFragment,
                        bundle
                    )

                }


            }

            R.id.cardViewTopYourBalance -> {
                title.text = getString(R.string.top_up)

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

                findNavController().navigate(
                    R.id.action_paymentMethodFragment_to_accountSuspendedPaymentFragment,
                    bundle
                )


            }

            R.id.cardViewThresholdLimit -> {
                title.text = getString(R.string.set_threshold_limit)
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, Constants.THRESHOLD)
                bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList?.size ?: 0)

                findNavController().navigate(
                    R.id.action_paymentMethodFragment_to_topUpFragment,
                    bundle
                )

            }
        }
    }

    override fun paymentMethodCallback(position: Int, value: String) {

        if (value == Constants.DELETE_CARD) {
            accountNumber= paymentList?.get(position)?.cardNumber.toString()

            if (paymentList?.get(position)?.primaryCard == true) {
                val bundle = Bundle()

                if (paymentList?.size == 1) {
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYG)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    } else {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PRE_PAY_ACCOUNT)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)
                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    }
                } else {
                    if ((paymentList?.size ?: 0) > 1) {
                        rowId = paymentList?.get(position)?.rowId ?: ""
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )

                        makeSecondaryCardAsPrimary(
                            paymentList?.get(position + 1)?.cardType,
                            paymentList?.get(position + 1)?.rowId
                        )
                    }

                }


            } else {
                isDirectDebitDelete = false

                this.position = position
                deletePaymentDialog(getString(R.string.str_payment_method_deleted),paymentList?.get(position)?.rowId,getString(R.string.str_are_you_sure_you_want_to_remove_payment_method,paymentList?.get(position)?.cardNumber,
                    paymentList?.get(position)?.expMonth+"/"+paymentList?.get(position)?.expMonth))


            }


        } else if (value == Constants.DIRECT_DEBIT) {
            accountNumber= paymentList?.get(position)?.bankAccountNumber.toString()

            if (paymentList?.get(position)?.primaryCard == true) {
                val bundle = Bundle()

                if (paymentList?.size == 1) {
                    if (accountInformation?.accSubType.equals(Constants.PAYG)) {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PAYG)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    } else {

                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.PRE_PAY_ACCOUNT)
                        bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                        bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)

                        findNavController().navigate(
                            R.id.action_paymentMethodFragment_to_deletePaymentMethodFragment,
                            bundle
                        )
                    }
                } else {
                    if ((paymentList?.size ?: 0) > 1) {
                        rowId = paymentList?.get(position)?.rowId ?: ""


                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )

                        makeSecondaryCardAsPrimary(
                            paymentList?.get(position + 1)?.cardType,
                            paymentList?.get(position + 1)?.rowId
                        )
                    }

                }


            } else {
                this.position = position
                isDirectDebitDelete = true
                deletePaymentDialog(getString(R.string.str_payment_method_deleted),paymentList?.get(position)?.rowId,getString(R.string.str_are_you_sure_you_want_to_remove_direct_payment_method,paymentList?.get(position)?.cardNumber))




            }

        } else if (value == Constants.MAKE_DEFAULT) {
            makeDefault = true
            loader?.show(
                requireActivity().supportFragmentManager,
                Constants.LOADER_DIALOG
            )

            makeSecondaryCardAsPrimary(
                paymentList?.get(position)?.cardType,
                paymentList?.get(position)?.rowId
            )
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
        title.text = getString(R.string.payment_management)
        super.onResume()
    }

    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
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
                    bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)

                    findNavController().navigate(
                        R.id.paymentMethodFragment_to_action_paymentSuccessFragment,
                        bundle
                    )
                } else {
                    bundle.putString(Constants.CARD_IS_ALREADY_REGISTERED, Constants.DELETE_CARD)
                    bundle.putParcelable(Constants.PAYMENT_DATA, paymentList?.get(position))
                    bundle.putString(Constants.ACCOUNT_NUMBER,accountNumber)


                    findNavController().navigate(
                        R.id.paymentMethodFragment_to_action_paymentSuccessFragment,
                        bundle
                    )
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    private fun handleDefaultCardResponse(status: Resource<PaymentMethodEditResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                if (makeDefault) {
                    viewModel.saveCardList()
                } else {
                    viewModel.deleteCard(PaymentMethodDeleteModel(rowId))

                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }

    }

    private fun deletePaymentDialog(
        title: String,
        rowId: String?,
        message:String,

    ) {

        displayCustomMessage(title,
            message,
            getString(R.string.cancel),
            getString(R.string.delete),
            object : DialogPositiveBtnListener {
                override fun positiveBtnClick(dialog: DialogInterface) {
                    viewModel.deleteCard(PaymentMethodDeleteModel(rowId))
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)



                }
            },
            object : DialogNegativeBtnListener {
                override fun negativeBtnClick(dialog: DialogInterface) {
                    dialog.dismiss()

                }
            })
    }



}


