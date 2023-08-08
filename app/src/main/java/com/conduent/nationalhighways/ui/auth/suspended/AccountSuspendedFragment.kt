package com.conduent.nationalhighways.ui.auth.suspended

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSuspendedFragment : BaseFragment<FragmentAccountSuspendHaltTopUpBinding>(),
    View.OnClickListener {
    private var currentBalance: String = ""
    private var personalInformation: PersonalInformation? = null
    private var crossingCount: String = ""
    private var navFlow: String = ""
    private var paymentListSize:Int=0


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltTopUpBinding =
        FragmentAccountSuspendHaltTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        paymentListSize=arguments?.getInt(Constants.PAYMENT_METHOD_SIZE)?:0

        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""
        crossingCount = arguments?.getString(Constants.CROSSINGCOUNT) ?: ""

        if (crossingCount.isNotEmpty()){

            if (crossingCount.toInt()>0){
                binding.maximumVehicleAddedNote.text=getString(R.string.str_you_crossing,"£5.00",crossingCount)
                binding.maximumVehicleAddedNote.visibility=View.VISIBLE
            }else{
                binding.maximumVehicleAddedNote.visibility=View.GONE

            }
        }



        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

        }

        val balance = currentBalance.replace("£", "")
        if (balance.isNotEmpty()){
            val doubleBalance = balance.toDouble()
            val intBalance = doubleBalance.toInt()
            val finalCurrentBalance = 5.00 - doubleBalance


            binding.textMaximumVehicle.text = getString(
                R.string.str_you_will_need_to_pay,
                "£" + String.format("%.2f", finalCurrentBalance)
            )

        }


    }

    override fun initCtrl() {
        binding.btnTopUpNow.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        if (arguments?.getString(Constants.NAV_FLOW_KEY) != null) {
            navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""

        }

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnTopUpNow -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.PERSONALDATA, personalInformation)
                bundle.putString(Constants.CURRENTBALANCE, currentBalance)
                bundle.putString(Constants.NAV_FLOW_KEY,navFlow)
                findNavController().navigate(
                    R.id.action_accountSuspendedFragment_to_accountSuspendedPaymentFragment,
                    bundle
                )
            }

            R.id.cancel_btn -> {
                requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)

            }

        }
    }
}