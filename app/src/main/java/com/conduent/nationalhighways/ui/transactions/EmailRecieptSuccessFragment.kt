package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentEmailRecieptSuccessBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class EmailRecieptSuccessFragment : BaseFragment<FragmentEmailRecieptSuccessBinding>() {

    private var dateRangeModel: PaymentDateRangeModel? = null
    private var topup: String? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null

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
        binding.message.text = resources.getString(
            R.string.text_we_have_sent_a_reciept_email_to,
            HomeActivityMain.accountDetailsData?.personalInformation?.emailAddress
        )
        binding.btnContinue.setOnClickListener {
            findNavController().popBackStack(R.id.crossingHistoryFragment, false)
        }
    }

    override fun observer() {
    }

}