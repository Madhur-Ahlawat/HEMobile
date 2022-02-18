package com.heandroid.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog
import com.heandroid.gone
import com.heandroid.listener.CrossingHistoryFilterDialogListener
import com.heandroid.model.DateRangeModel
import com.heandroid.model.crossingHistory.request.CrossingHistoryRequest
import com.heandroid.model.crossingHistory.response.CrossingHistoryApiResponse
import com.heandroid.model.crossingHistory.response.CrossingHistoryItem
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.viewmodel.VehicleMgmtViewModel
import com.heandroid.viewmodel.ViewModelFactory
import com.heandroid.visible

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener, CrossingHistoryFilterDialogListener {

    private lateinit var viewModel: VehicleMgmtViewModel
    private lateinit var binding: FragmentCrossingHistoryBinding
    private var dateRangeModel : DateRangeModel?=DateRangeModel(type = "", from = "",to="", title = "")

    private var startIndex: Long=1
    private val count:Long=5
    private var isLoading = false
    private var isFirstTime=true
    private var list : MutableList<CrossingHistoryItem?>? = ArrayList()
    private var totalCount: Int=0
    private lateinit var request : CrossingHistoryRequest


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCrossingHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
        observer()
    }

    private fun init() {

        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[VehicleMgmtViewModel::class.java]


        request = CrossingHistoryRequest(startIndex = startIndex, count = count, transactionType = "ALL")
        viewModel.crossingHistoryApiCall(request)

        binding.rvHistory.layoutManager=LinearLayoutManager(requireActivity())
        binding.rvHistory.adapter=CrossingHistoryAdapter(this,list)
    }

    private fun initCtrl() {
        binding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }

    private fun observer() {
        viewModel.crossingHistoryVal.observe(viewLifecycleOwner) {
            when(it.status) {

                Status.SUCCESS ->{

                    val response = it.data?.body() as CrossingHistoryApiResponse
                    totalCount=response.transactionList?.transaction?.size?:0
                    Log.e("totalCount",""+totalCount)
                    if(response.transactionList!=null){
                        list?.addAll(response.transactionList.transaction)
                    }
                    isLoading=false
//                    isLoading = list?.size?:0 != totalCount
                    Handler(Looper.myLooper()!!).postDelayed( {
                        binding.rvHistory.adapter?.notifyDataSetChanged()
                    },2000)

                    if(list?.size==0){
                        binding.rvHistory.gone()
                        binding.tvNoCrossing.visible()
                        binding.progressBar.gone()
                    }else{
                        binding.rvHistory.visible()
                        binding.progressBar.gone()
                        binding.tvNoCrossing.gone()

                    }
                    endlessScroll()

                }

                Status.ERROR -> {
                    binding.rvHistory.visible()
                    binding.progressBar.gone()
                }
            }
        }
    }

    private fun endlessScroll() {
        if(isFirstTime) {
            isFirstTime=false
            binding.rvHistory.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (recyclerView.layoutManager is LinearLayoutManager) {
                        val linearLayoutManager =
                            recyclerView.layoutManager as LinearLayoutManager?
                        if (!isLoading) {
                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ((list?.size?:0)-1)  && totalCount>5) {
                                startIndex += 5
                                isLoading = true
                                request.startIndex = startIndex
                                binding.progressBar.visible()
                                viewModel.crossingHistoryApiCall(request)
                            }
                        }
                    }
                }

            })
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvDownload -> {  }
            R.id.tvFilter -> {
                val dialog = CrossingHistoryFilterDialog()
                dialog.setDateWithListener(dateRangeModel,this)
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager, "")
            }
        }
    }

    override fun onRangedApplied(dataModel: DateRangeModel?) {
        reloadData(dataModel)
    }

    override fun onClearRange(dataModel: DateRangeModel?) {
        reloadData(dataModel)
    }

    private fun reloadData(dataModel: DateRangeModel?) {
        binding.rvHistory.gone()
        binding.tvNoCrossing.gone()
        binding.progressBar.visible()

        startIndex=1
        totalCount=0
        isLoading=false
        list?.clear()
        binding.rvHistory.adapter?.notifyDataSetChanged()
        dateRangeModel=dataModel
        request=loadRequest(dateRangeModel)
        viewModel.crossingHistoryApiCall(request)
    }



    private fun loadRequest(dataModel: DateRangeModel?) : CrossingHistoryRequest{
        return when (dataModel?.type) {
            "Toll_Transaction" -> { CrossingHistoryRequest(startIndex = startIndex,
                count = count,
                transactionType = dataModel.type?:"",
                searchDate = "Transaction Date",
                startDate="11/01/2021"/*dataModel.from?:""*/,
                endDate = "11/30/2021"/*dataModel.to?:""*/) }


            else -> { CrossingHistoryRequest(startIndex = startIndex,
                count = count,
                transactionType = dataModel?.type?:"") }
        }
    }

}