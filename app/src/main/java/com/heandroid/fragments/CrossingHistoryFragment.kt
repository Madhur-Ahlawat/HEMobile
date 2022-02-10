package com.heandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.adapter.CrossingHistoryAdapter
import com.heandroid.databinding.FragmentCrossingHistoryBinding
import com.heandroid.dialog.CrossingHistoryFilterDialog

class CrossingHistoryFragment : BaseFragment(), View.OnClickListener {

    private lateinit var dataBinding : FragmentCrossingHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       dataBinding= FragmentCrossingHistoryBinding.inflate(inflater,container,false)
       return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         init()
         initCtrl()
    }

    private fun init() {
        dataBinding.apply {
            rvHistory.layoutManager=LinearLayoutManager(requireActivity())
            rvHistory.adapter=CrossingHistoryAdapter(requireActivity(),null)
        }
    }
    private fun initCtrl(){
        dataBinding.apply {
            tvDownload.setOnClickListener(this@CrossingHistoryFragment)
            tvFilter.setOnClickListener(this@CrossingHistoryFragment)
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tvDownload -> { }
            R.id.tvFilter ->{
                val dialog = CrossingHistoryFilterDialog()
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.Dialog_NoTitle)
                dialog.show(requireActivity().supportFragmentManager,"") }
        }
    }
}