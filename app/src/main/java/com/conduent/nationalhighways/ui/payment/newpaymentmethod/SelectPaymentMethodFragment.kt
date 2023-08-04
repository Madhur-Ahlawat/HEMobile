package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentSelectPaymentMethodBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPaymentMethodFragment : BaseFragment<FragmentSelectPaymentMethodBinding>(),
    View.OnClickListener {

    private var personalInformation: PersonalInformation? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectPaymentMethodBinding =
        FragmentSelectPaymentMethodBinding.inflate(inflater, container, false)



    override fun initCtrl() {

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)

        }
    }

    override fun init() {
        binding.cardViewDebit.setOnClickListener(this)
        binding.cardViewDirectDebit.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

          R.id.cardViewDebit->{
              val bundle=Bundle()
              bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

              bundle.putString(Constants.NAV_FLOW_KEY,Constants.ADD_PAYMENT_METHOD)
              bundle.putDouble(Constants.DATA, 0.0)

              findNavController().navigate(R.id.action_selectPaymentMethodFragment_to_nmiPaymentFragment,bundle)

          }
          R.id.cardViewDirectDebit->{
              val bundle=Bundle()
              bundle.putParcelable(Constants.PERSONALDATA, personalInformation)

              findNavController().navigate(R.id.action_selectPaymentMethodFragment_to_directDebitFragment,bundle)
          }
        }
    }

}