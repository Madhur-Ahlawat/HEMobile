package com.conduent.nationalhighways.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.payment.PaymentDateRangeModel
import com.conduent.nationalhighways.databinding.FragmentTollDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.accountDetailsData
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.checkedCrossing
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TollDetailsFragment : BaseFragment<FragmentTollDetailsBinding>() {

    private var dateRangeModel: PaymentDateRangeModel? = null
    private var topup: String? = null
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var data: CrossingDetailsModelsResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentTollDetailsBinding.inflate(inflater, container, false)


    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
    }

    override fun onResume() {
        super.onResume()
        if(crossing==null){
            binding?.apply {
                tvStatus.gone()
                tvStatusValue.gone()
                tvAccountNumberHeading.gone()
                tvAccountNumberValue.gone()
                crossingAmount.text = checkedCrossing?.amount
                tvAccountNumberValue.text = accountDetailsData?.accountInformation?.number
                tvVehicleRegistrationValue.text = checkedCrossing?.plateNumber
                tvTimeValue.text = checkedCrossing?.exitTime
                tvLocationValue.text = checkedCrossing?.exitPlazaName
            }

        }
        else{
            binding?.apply {
                tvStatus.visible()
                tvStatusValue.visible()
                tvAccountNumberHeading.visible()
                tvAccountNumberValue.visible()
                crossingAmount.text = crossing?.amount
                tvAccountNumberValue.text = accountDetailsData?.accountInformation?.number
                tvVehicleRegistrationValue.text = dateRangeModel?.vehicleNumber
                tvTimeValue.text = crossing?.exitTime
                tvLocationValue.text = crossing?.entryPlaza
                tvStatusValue.text= crossing?.tranSettleStatus
            }
        }

    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}