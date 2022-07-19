package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.UsedChargesModel
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionsRequest
import com.heandroid.databinding.FragmentUsedChargesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsedChargesFragment : BaseFragment<FragmentUsedChargesBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    val mList = mutableListOf<UsedChargesModel?>()
    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUsedChargesBinding.inflate(inflater, container, false)

    override fun init() {
        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val trans = UsedTollTransactionsRequest()
        viewModel.usedTollTransactions(trans)
    }

    override fun initCtrl() {}

    override fun observer() {
        observe(viewModel.usedTollTransactions, ::handleUsedTollTrans)
    }

    private fun handleUsedTollTrans(resource: Resource<List<UsedTollTransactionResponse?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { data ->
                    data.forEach {
                        val bounds = if (it?.exitDirection.equals("S"))
                            "SouthBound"
                        else
                            "NorthBound"

                        val model = UsedChargesModel(
                            it?.lookupKey,
                            it?.plateNumber,
                            crossingDate = DateUtils.convertDateFormat(it?.entryDate, 0),
                            it?.exitTime, bounds
                        )
                        mList.add(model)
                        binding.rvHistory.adapter = UsedChargesAdapter(mList)
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
    }

    override fun onClick(v: View?) {}

}


