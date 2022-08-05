package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.UsedChargesModel
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionsRequest
import com.heandroid.databinding.FragmentUsedChargesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.checkpaidcrossings.adapter.UsedChargesAdapter
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsedChargesFragment : BaseFragment<FragmentUsedChargesBinding>(),
    View.OnClickListener {

    private var loader: LoaderDialog? = null
    val mList = mutableListOf<UsedChargesModel?>()
    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()
    private var isClicked = false

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
        isClicked = true
        viewModel.usedTollTransactions(trans)
    }

    override fun initCtrl() {}

    override fun observer() {
        observe(viewModel.usedTollTransactions, ::handleUsedTollTrans)
    }

    private fun handleUsedTollTrans(resource: Resource<List<UsedTollTransactionResponse?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isClicked) {
            when (resource) {
                is Resource.Success -> {
                    mList.clear()
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
                        }
                    }
                    if (mList.isEmpty()) {
                        binding.rvHistory.gone()
                        binding.tvNoCrossing.visible()
                    } else {
                        binding.rvHistory.visible()
                        binding.rvHistory.adapter = UsedChargesAdapter(mList)
                    }
                }
                is Resource.DataError -> {
                    binding.rvHistory.gone()
                    binding.tvNoCrossing.visible()
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
                else -> {
                }
            }
            isClicked = false
        }
    }

    override fun onClick(v: View?) {}

}


