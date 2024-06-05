package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentEmailRecieptSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailRecieptSuccessFragment : BaseFragment<FragmentEmailRecieptSuccessBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentEmailRecieptSuccessBinding.inflate(inflater, container, false)


    override fun init() {

    }

    override fun onResume() {
        super.onResume()
//        binding.title.text=DashboardFragmentNew.crossing?.amount?
//        binding.tvAccountNumberValue.text=DashboardFragmentNew.accountDetailsData?.accountInformation?.number
//        binding.tvVehicleRegistrationValue.text=DashboardFragmentNew.dateRangeModel?.vehicleNumber
//        binding.tvTimeValue.text=DashboardFragmentNew.crossing?.entryTime
//        binding.tvLocationValue.text=DashboardFragmentNew.crossing?.entryPlaza
    }

    override fun initCtrl() {
        binding.emailTv.text =
            HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress

        binding.btnContinue.setOnClickListener {
            findNavController().popBackStack(R.id.crossingHistoryFragment, false)
        }
        if (requireActivity() is HomeActivityMain) {
            (requireActivity() as HomeActivityMain).setTitle(resources.getString(R.string.payment_details))
        }
    }

    override fun observer() {
    }

}