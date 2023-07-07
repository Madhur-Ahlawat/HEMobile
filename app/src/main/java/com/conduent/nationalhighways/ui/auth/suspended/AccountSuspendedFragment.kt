package com.conduent.nationalhighways.ui.auth.suspended

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.annotation.meta.When

@AndroidEntryPoint
class AccountSuspendedFragment: BaseFragment<FragmentAccountSuspendHaltTopUpBinding>(), View.OnClickListener {
    private var currentBalance:String=""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltTopUpBinding= FragmentAccountSuspendHaltTopUpBinding.inflate(inflater,container,false)

    override fun init() {

        currentBalance=arguments?.getString(Constants.CURRENTBALANCE)?:""


        val balance=currentBalance.replace("£","")
        val doubleBalance=balance.toDouble()
        val intBalance=doubleBalance.toInt()
        val finalCurrentBalance=5.00-doubleBalance


        binding.textMaximumVehicle.text=getString(R.string.str_you_will_need_to_pay, "£"+String.format("%.2f", finalCurrentBalance))






    }

    override fun initCtrl() {
        binding.btnTopUpNow.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnTopUpNow->{
                findNavController().navigate(R.id.action_accountSuspendedFragment_to_accountSuspendedPaymentFragment)
            }
            R.id.cancel_btn->{
                val intent = Intent(requireContext(), HomeActivityMain::class.java)
                startActivity(intent)
            }

        }
    }
}