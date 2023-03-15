package com.conduent.nationalhighways.ui.account.creation.step6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.databinding.FragmentSelectAddressBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.SelectAddressAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAddressFragment : BaseFragment<FragmentSelectAddressBinding>() {

    private var list:ArrayList<String> = ArrayList()
    private var selectAddressAdapter:SelectAddressAdapter?=null



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectAddressBinding =
        FragmentSelectAddressBinding.inflate(inflater, container, false)

    override fun init() {
        list.add("")
        list.add("")
        list.add("")



        val linearLayoutManager=LinearLayoutManager(requireActivity())
        binding.recylcerview.layoutManager=linearLayoutManager


        selectAddressAdapter=SelectAddressAdapter(requireContext(),list)
        binding.recylcerview.adapter=selectAddressAdapter

    }

    override fun initCtrl() {


    }

    override fun observer() {
    }

}