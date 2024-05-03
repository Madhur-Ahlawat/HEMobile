package com.conduent.nationalhighways.ui.landing

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.tollrates.TollRatesRespNew
import com.conduent.nationalhighways.databinding.FragmentViewChargesNewBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.ui.viewcharges.TollRateAdapterNew
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewChargesFragment : BaseFragment<FragmentViewChargesNewBinding>() {

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
        if(requireActivity() is RaiseEnquiryActivity){
            (requireActivity() as RaiseEnquiryActivity).focusToolBarRaiseEnquiry()
        }
    }

    override fun initCtrl() {
        binding?.apply {
            textFines?.setMovementMethod(LinkMovementMethod.getInstance())
            textMoreDetails?.setMovementMethod(LinkMovementMethod.getInstance())
            textIfYouHaveDisabled?.setMovementMethod(LinkMovementMethod.getInstance())
            textLocalResidentDiscount?.setMovementMethod(LinkMovementMethod.getInstance())
        }

        binding.textLocalResidentDiscount.contentDescription=resources.getString(R.string.b_local_resident_discount_desc)
        binding.textFines.contentDescription=resources.getString(R.string.fines_you_can_get_desc)
    }

    override fun observer() {

    }


}