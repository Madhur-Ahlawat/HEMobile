package com.heandroid.ui.account.creation.step2.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.databinding.FragmentBusinessPrepayInfoBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants

class BusinessPrePayInfoFragment : BaseFragment<FragmentBusinessPrepayInfoBinding>(),
    View.OnClickListener {

    private var requestModel : CreateAccountRequestModel?=null
    private var isEditAccountType : Int? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)
    = FragmentBusinessPrepayInfoBinding.inflate(inflater, container, false)

    override fun init() {
        requestModel=arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType = arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
    }

    override fun initCtrl() {
        binding.continueBusiness.setOnClickListener(this@BusinessPrePayInfoFragment)

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
                findNavController().navigate(R.id.action_business_prepayInfoFragment_to_business_prepay_autotopupfragment ,bundle)

            }
        }
    }
}