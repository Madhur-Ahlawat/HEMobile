package com.heandroid.ui.bottomnav.account.payments.accountpaymenthistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.accountpayment.TransactionList
import com.heandroid.databinding.FragmentAccountPaymentHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentHistoryFragment : BaseFragment<FragmentAccountPaymentHistoryBinding>() {

    private val viewModel: AccountPaymentHistoryViewModel by viewModels()
  //  private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    )= FragmentAccountPaymentHistoryBinding.inflate(inflater, container, false)

    override fun init() {
     //   loader = LoaderDialog()
     //   loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
     //   loader?.show(requireActivity().supportFragmentManager, "")
    }

    override fun initCtrl() {
        val request = AccountPaymentHistoryRequest(1, "Payment",10)
        viewModel.paymentHistoryDetails(request)
    }

    override fun observer() {
        observe(viewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }

    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?){
    //    if (loader?.isVisible == true){
    //        loader?.dismiss()
    //    }
        when (resource) {
            is Resource.Success -> {
                if(resource.data?.statusCode?.equals("0")==true){
                      updatePaymentTransaction(resource.data.transactionList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                // do nothing
            }
        }
    }

    private fun updatePaymentTransaction(accountPaymentRes: TransactionList?) {
         if(accountPaymentRes?.transaction?.isNullOrEmpty() == false){

                 val mAdapter = AccountPaymentHistoryAdapter(requireActivity(), accountPaymentRes?.transaction)

                 binding.paymentRecycleList.apply {
                     layoutManager = LinearLayoutManager(requireActivity())
                     adapter = mAdapter
                 }
             }

         }
    }

