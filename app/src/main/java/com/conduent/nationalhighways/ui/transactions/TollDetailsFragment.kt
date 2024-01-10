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
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain.Companion.crossing
import com.conduent.nationalhighways.ui.bottomnav.dashboard.DashboardViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
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
        HomeActivityMain.setTitle(getString(R.string.crossing_details))
        if(requireActivity() is HomeActivityMain){
            (requireActivity() as HomeActivityMain).showHideToolbar(true)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            crossingAmount.text = crossing?.amount
            tvCrossingDateValue.text = crossing?.transactionDate
            tvTimeValue.text = crossing?.exitTime
            tvVrnValue.text= crossing?.plateNumber
            if(crossing?.exitDirection.equals("N")){
                tvLocationValue.text = "Northbound"
            }
            else{
                tvLocationValue.text = "Southbound"
            }
            if(crossing?.tranSettleStatus?.isEmpty() == true){
                binding.tvPaymentStatusValue.gone()
                binding.tvPaymentStatus.gone()
            }else{
                binding.tvPaymentStatusValue.visible()
                binding.tvPaymentStatus.visible()
                tvPaymentStatusValue.text = Utils.capitalizeString(crossing?.tranSettleStatus)
            }
        }
        HomeActivityMain.setTitle(resources.getString(R.string.crossing_details))
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

}