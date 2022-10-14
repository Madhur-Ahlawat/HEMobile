package com.conduent.nationalhighways.ui.account.creation.step2.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentBusinessInfoBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessInfoFragment : BaseFragment<FragmentBusinessInfoBinding>(),
    View.OnClickListener{

    private var requestModel : CreateAccountRequestModel?=null
    private var isEditAccountType : Int? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) =  FragmentBusinessInfoBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel=arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
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
               isEditAccountType?.let {
                   bundle.putInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY)
               }
               findNavController().navigate(R.id.action_businessInfoFragment_to_businessPrepayInfoFragment, bundle)

           }
       }
    }
}
