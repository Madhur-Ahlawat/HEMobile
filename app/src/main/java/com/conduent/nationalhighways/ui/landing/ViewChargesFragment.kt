package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.tollrates.TollRatesResp
import com.conduent.nationalhighways.data.model.tollrates.TollRatesRespNew
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.databinding.FragmentTermsAndConditionsBinding
import com.conduent.nationalhighways.databinding.FragmentViewChargesNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.viewcharges.TollRateAdapter
import com.conduent.nationalhighways.ui.viewcharges.TollRateAdapterNew
import com.conduent.nationalhighways.ui.viewcharges.ViewChargeViewModel
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.extn.visible
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ViewChargesFragment : BaseFragment<FragmentViewChargesNewBinding>() {

    private val viewModel: ViewChargeViewModel by viewModels()

    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentViewChargesNewBinding {
        binding = FragmentViewChargesNewBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        LandingActivity.setToolBarTitle("Charges and Fines")
        LandingActivity.showToolBar(true)
        HomeActivityMain.accountDetailsData = null
        HomeActivityMain.checkedCrossing = null
        HomeActivityMain.crossing = null
        HomeActivityMain.dateRangeModel = null
        HomeActivityMain.paymentHistoryListData = null
        HomeActivityMain.paymentHistoryListDataCheckedCrossings = arrayListOf()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        val backButton: ImageView? = requireActivity().findViewById(R.id.back_button)

        backButton?.visibility = View.GONE

        AdobeAnalytics.setScreenTrack(
            "home",
            "home",
            "english",
            "home",
            "splash",
            "home",
            sessionManager.getLoggedInUser()
        )
        binding.topTitle.visible()
        binding.titleCard.visible()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())

            val mTollRatesList = ArrayList<TollRatesRespNew>()
                mTollRatesList.add(
                    TollRatesRespNew(1, "Motorcycle", "A", "Free", "Free")
                )
                mTollRatesList.add(
                    TollRatesRespNew(2, "Car", "B", "£2.50", "£2.00")
                )
                mTollRatesList.add(
                    TollRatesRespNew(3, "Bus", "C", "£3.00", "£2.63")
                )
                mTollRatesList.add(
                    TollRatesRespNew(4, "Truck", "D", "£6.00", "£5.19")
                )
            adapter = TollRateAdapterNew(requireActivity(), mTollRatesList)
        }
    }

    override fun initCtrl() {
    }

    override fun observer() {

    }


}