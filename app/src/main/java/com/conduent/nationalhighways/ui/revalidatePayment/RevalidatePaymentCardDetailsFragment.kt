package com.conduent.nationalhighways.ui.revalidatePayment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardDetailsBinding
import com.conduent.nationalhighways.ui.auth.adapter.SuspendPaymentMethodAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RevalidatePaymentCardDetailsFragment :
    BaseFragment<FragmentRevalidatePaymentCardDetailsBinding>(),
    SuspendPaymentMethodAdapter.PaymentMethodSelectCallBack {

    private val viewModel: PaymentMethodViewModel by viewModels()
    private lateinit var suspendPaymentMethodAdapter: SuspendPaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var realPaymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var cardSelection: Boolean = false
    private var position: Int = 0

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidatePaymentCardDetailsBinding =
        FragmentRevalidatePaymentCardDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        initAdapter()

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_reValidatePaymentCardDetailsFragment_to_reValidateExistingPaymentFragment)

        }
    }

    override fun initCtrl() {
        showLoaderDialog()
        viewModel.saveCardListState()
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
                realPaymentList?.clear()
                realPaymentList = status.data?.creditCardListType?.cardsList
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
                if (paymentList?.isNotEmpty() == true) {

                    for (i in 0 until (paymentList?.size ?: 0)) {
                        if (paymentList?.get(i)?.primaryCard == true && paymentList?.get(i)?.bankAccount == false) {
                            position = i
                            cardSelection = true
                            checkContinueButton()
                            break
                        }

                    }

                    suspendPaymentMethodAdapter.updateList(paymentList, navFlowCall)
                    binding.rvPaymentMethods.visible()


                } else {
                    binding.rvPaymentMethods.gone()
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

    private fun checkContinueButton() {
        binding.btnContinue.isEnabled = cardSelection
    }

    override fun paymentMethodCallback(position: Int) {
        this.position = position
        for (i in 0 until (paymentList?.size ?: 0)) {
            paymentList?.get(i)?.isSelected = false
            paymentList?.get(i)?.primaryCard = false
        }
        paymentList?.get(position)?.primaryCard = true

        paymentList?.get(position)?.isSelected = true
        suspendPaymentMethodAdapter.updateList(paymentList, navFlowCall)
        cardSelection = paymentList?.get(position)?.isSelected == true
        checkContinueButton()
    }


    private fun initAdapter() {
        binding.rvPaymentMethods.layoutManager = LinearLayoutManager(requireContext())
        suspendPaymentMethodAdapter =
            SuspendPaymentMethodAdapter(requireActivity(), paymentList, this, navFlowCall)
        binding.rvPaymentMethods.adapter = suspendPaymentMethodAdapter
    }
}