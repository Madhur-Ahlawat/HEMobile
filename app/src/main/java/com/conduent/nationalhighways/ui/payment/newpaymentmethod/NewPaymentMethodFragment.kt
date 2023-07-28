package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
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
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentPaymentMethod2Binding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.adapter.PaymentMethodAdapter
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class NewPaymentMethodFragment : BaseFragment<FragmentPaymentMethod2Binding>(),
    PaymentMethodAdapter.PaymentMethodCallback, View.OnClickListener {
    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private val viewModel: PaymentMethodViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()

    private var loader: LoaderDialog? = null
    private var position: Int = 0
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var replenishmentInformation: ReplenishmentInformation? = null
    private var accountInformation: AccountInformation? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentMethod2Binding =
        FragmentPaymentMethod2Binding.inflate(inflater, container, false)


    override fun initCtrl() {
        paymentList = ArrayList()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

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

        }
    }

    override fun paymentMethodCallback(position: Int) {

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
                    paymentMethodAdapter.updateList(paymentList)


                    binding.addNewPaymentMethod.isEnabled = (paymentList?.size ?: 0) < 2

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
                findNavController().navigate(R.id.action_paymentMethodFragment_to_selectPaymentMethodFragment)

            }
            R.id.cardViewTopYourBalance->{
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.putExtra(Constants.NAV_FLOW_KEY, Constants.PAYMENT_TOP_UP)
                intent.putExtra(Constants.PERSONALDATA, personalInformation)

                startActivity(intent)

            }
        }
    }

}