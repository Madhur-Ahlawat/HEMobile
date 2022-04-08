package com.heandroid.ui.bottomnav.account.payments.history

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryRequest
import com.heandroid.data.model.accountpayment.AccountPaymentHistoryResponse
import com.heandroid.data.model.accountpayment.TransactionData
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.vehicle.DateRangeModel
import com.heandroid.databinding.FragmentAccountPaymentHistoryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.crossinghistory.CrossingHistoryFilterDialog
import com.heandroid.ui.vehicle.crossinghistory.DownloadFilterDialogListener
import com.heandroid.ui.vehicle.crossinghistory.DownloadFormatSelectionFilterDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountPaymentHistoryFragment : BaseFragment<FragmentAccountPaymentHistoryBinding>(),
    View.OnClickListener,
    DownloadFilterDialogListener {

    private val viewModel: AccountPaymentHistoryViewModel by viewModels()
    private var listData: MutableList<TransactionData?> = ArrayList()
    private var paymentHistoryAdapter: AccountPaymentHistoryAdapter? = null
    private val countPerPage = 10
    private var startIndex = 1

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAccountPaymentHistoryBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentHistoryAdapter = AccountPaymentHistoryAdapter(this, listData)
        getDataForPage(startIndex)
    }

    override fun init() {
        binding.progressBar.visible()
        binding.paymentRecycleView.gone()
        binding.paymentRecycleView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = paymentHistoryAdapter
        }
    }

    override fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@AccountPaymentHistoryFragment)
            tvFilter.setOnClickListener(this@AccountPaymentHistoryFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {
                if (!StorageHelper.checkStoragePermissions(requireActivity())) {
                    StorageHelper.requestStoragePermission(
                        requireActivity(),
                        onScopeResultLaucher = onScopeResultLauncher,
                        onPermissionlaucher = onPermissionLauncher
                    )
                } else {
                    if (listData.isEmpty()) {
                        requireContext().showToast("No payment history to download")
                    } else {
                        val dialog = DownloadFormatSelectionFilterDialog()
                        dialog.setListener(this)
                        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                        dialog.show(requireActivity().supportFragmentManager, "")
                    }
                }
            }
            R.id.tvFilter -> {

            }
        }
    }

    override fun observer() {
        observe(viewModel.paymentHistoryLiveData, ::handlePaymentResponse)
    }

    private fun getDataForPage(index: Int) {
        val request = AccountPaymentHistoryRequest(index, Constants.PAYMENT, countPerPage)
        viewModel.paymentHistoryDetails(request)
    }

    private fun handlePaymentResponse(resource: Resource<AccountPaymentHistoryResponse?>?) {
        binding.progressBar.gone()
        binding.paymentRecycleView.visible()
        when (resource) {
            is Resource.Success -> {
                resource.data?.transactionList?.transaction?.let {
                    if (it.isNotEmpty()) {
                        listData.clear()
                        listData.addAll(it)
                        binding.paymentRecycleView.adapter?.notifyDataSetChanged()
                    }
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

    override fun onOkClickedListener(type: String) {

    }

    override fun onCancelClicked() {

    }

    private var onScopeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.tvDownload.performClick()
            }
        }


    private var onPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission = true
            permissions.entries.forEach {
                if (!it.value) {
                    permission = it.value
                }
            }
            when (permission) {
                true -> {
                    binding.tvDownload.performClick()
                }
                else -> {
                    requireActivity().showToast("Please enable permission to download")
                }
            }
        }


}

