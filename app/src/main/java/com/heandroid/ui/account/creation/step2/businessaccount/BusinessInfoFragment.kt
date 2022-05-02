package com.heandroid.ui.account.creation.step2.businessaccount;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessInfoBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
class BusinessInfoFragment : BaseFragment<FragmentBusinessInfoBinding>(),
    View.OnClickListener{

    private var requestModel : CreateAccountRequestModel?=null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) =  FragmentBusinessInfoBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel=arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
    }

    override fun initCtrl() {
        binding.continueBusiness.setOnClickListener(this@BusinessInfoFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {

       when(view?.id){
           R.id.continue_business -> {
               val bundle = Bundle()
               bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA,requestModel)
               findNavController().navigate(R.id.action_businessInfoFragment_to_businessPrepayInfoFragment, bundle)

           }
       }
    }
}
