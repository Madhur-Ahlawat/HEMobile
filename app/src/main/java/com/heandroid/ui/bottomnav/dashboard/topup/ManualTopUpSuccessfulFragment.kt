package com.heandroid.ui.bottomnav.dashboard.topup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.heandroid.R
import com.heandroid.data.model.payment.PaymentMethodDeleteResponseModel
import com.heandroid.databinding.FragmentManualTopUpSuccessfulBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.bottomnav.HomeActivityMain
import com.heandroid.utils.DateUtils
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManualTopUpSuccessfulFragment : BaseFragment<FragmentManualTopUpSuccessfulBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)=FragmentManualTopUpSuccessfulBinding.inflate(inflater,container,false)

    override fun init() {
        binding.tvAmount.text="£ ${arguments?.getString("amount")}"
        val model=arguments?.getParcelable<PaymentMethodDeleteResponseModel>(Constants.DATA)
        binding.tvReceiptNo.text=model?.transactionId
        binding.tvEmail.text=model?.emailMessage
        binding.tvDate.text=DateUtils.currentDate()
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnContinue ->{
                requireActivity().finish()
                requireActivity().startNormalActivity(HomeActivityMain::class.java)
            }
        }
    }
}