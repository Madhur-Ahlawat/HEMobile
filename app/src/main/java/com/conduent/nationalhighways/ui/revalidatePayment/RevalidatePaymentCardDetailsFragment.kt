package com.conduent.nationalhighways.ui.revalidatePayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.profile.AccountInformation
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidatePaymentCardDetailsBinding
import com.conduent.nationalhighways.ui.auth.adapter.SuspendPaymentMethodAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RevalidatePaymentCardDetailsFragment :
    BaseFragment<FragmentRevalidatePaymentCardDetailsBinding>(),
    SuspendPaymentMethodAdapter.PaymentMethodSelectCallBack {

    private lateinit var suspendPaymentMethodAdapter: SuspendPaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var realPaymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var cardSelection: Boolean = false
    private var position: Int = 0
    private var personalInformation: PersonalInformation? = null
    private var accountInformation: AccountInformation? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidatePaymentCardDetailsBinding =
        FragmentRevalidatePaymentCardDetailsBinding.inflate(inflater, container, false)

    override fun init() {

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.PAYMENT_LIST_DATA) != null) {
            realPaymentList =
                arguments?.getParcelableArrayList(Constants.PAYMENT_LIST_DATA)
            paymentList = realPaymentList?.let { ArrayList(it) }
        }
        if (arguments?.getParcelable<AccountInformation>(Constants.ACCOUNTINFORMATION) != null) {
            accountInformation = arguments?.getParcelable(Constants.ACCOUNTINFORMATION)
        }
        if(paymentList?.isNotEmpty() == true){
            val model = Utils.checkReValidationPayment(paymentList,accountInformation).second
            paymentList?.clear()
            paymentList?.add(model)
        }

        initAdapter()

        binding.btnContinue.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
            bundle.putInt(Constants.POSITION, position)
            bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
            bundle.putParcelableArrayList(
                Constants.PAYMENT_LIST_DATA,
                paymentList as ArrayList
            )
            findNavController().navigate(R.id.action_reValidatePaymentCardDetailsFragment_to_reValidateExistingPaymentFragment,bundle)

        }

        binding.btnAddNewPayment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_KEY, Constants.CARD_VALIDATION_REQUIRED)
            bundle.putString(Constants.NAV_FLOW_FROM, Constants.CARD_VALIDATION_REQUIRED)
            bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
            bundle.putParcelable(Constants.ACCOUNTINFORMATION, accountInformation)
            bundle.putDouble(Constants.DATA, 0.0)
            bundle.putInt(Constants.PAYMENT_METHOD_SIZE, paymentList.orEmpty().size)

            findNavController().navigate(
                R.id.action_reValidatePaymentCardDetailsFragment_to_nmiPaymentFragment,
                bundle
            )
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

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