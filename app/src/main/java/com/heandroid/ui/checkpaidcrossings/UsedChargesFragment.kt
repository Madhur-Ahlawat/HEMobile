package com.heandroid.ui.checkpaidcrossings

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.data.model.checkpaidcrossings.UsedChargesModel
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionResponse
import com.heandroid.data.model.checkpaidcrossings.UsedTollTransactionsRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryApiResponse
import com.heandroid.data.model.crossingHistory.TransactionHistoryDownloadRequest
import com.heandroid.data.model.crossingHistory.CrossingHistoryItem
import com.heandroid.data.model.crossingHistory.CrossingHistoryRequest
import com.heandroid.data.model.vehicle.DateRangeModel
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.databinding.FragmentUsedChargesBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.VehicleMgmtViewModel
import com.heandroid.utils.DateUtils
import com.heandroid.utils.StorageHelper
import com.heandroid.utils.StorageHelper.checkStoragePermissions
import com.heandroid.utils.StorageHelper.requestStoragePermission
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.*

@AndroidEntryPoint
class UsedChargesFragment : BaseFragment<FragmentUsedChargesBinding>(),
    View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUsedChargesBinding.inflate(inflater, container, false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun init() {
        binding.rvHistory.layoutManager = LinearLayoutManager(requireActivity())
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        val trans = UsedTollTransactionsRequest()
        viewModel.usedTollTransactions(trans)


    }

    override fun initCtrl() {
    }

    override fun observer() {
        observe(viewModel.usedTollTransactions, ::handleUsedTollTrans)
    }

    val mList = mutableListOf<UsedChargesModel?>()
    private fun handleUsedTollTrans(resource: Resource<List<UsedTollTransactionResponse?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { data ->
                    Logg.logging("UsedCharges", " data $data ")
                    data.forEach {
                        var bounds = if (it?.exitDirection.equals("S"))
                            "SouthBound"
                        else
                            "NorthBound"

                        val model = UsedChargesModel(
                            it!!.lookupKey,
                            it?.plateNumber!!,
                            crossingDate = DateUtils.convertDateFormat(it!!.entryDate,0),
                            it?.exitTime, bounds

                        )
                        mList.add(model)

                        binding.rvHistory.adapter = UsedChargesAdapter(mList)

                    }
                }
            }
            is Resource.DataError -> {
            }
            else -> {

            }


        }
    }

    override fun onClick(v: View?) {
    }


}


