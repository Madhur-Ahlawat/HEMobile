package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.pushnotification.PushNotificationRequest
import com.conduent.nationalhighways.data.model.webstatus.WebSiteStatus
import com.conduent.nationalhighways.databinding.FragmentDoYouHaveDartChargeAccountBinding
import com.conduent.nationalhighways.databinding.FragmentGuidanceAndDocumentsBinding
import com.conduent.nationalhighways.databinding.FragmentTermsAndConditionsBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.RaiseEnquiryActivity
import com.conduent.nationalhighways.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.ui.websiteservice.WebSiteServiceViewModel
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.startNormalActivity
import com.conduent.nationalhighways.utils.notification.PushNotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DoYouHaveDartChargeFragment : BaseFragment<FragmentDoYouHaveDartChargeAccountBinding>() {

    private val webServiceViewModel: WebSiteServiceViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isChecked = false
    private var isPushNotificationChecked = true
    private var count = 1
    var apiState = Constants.UNAVAILABLE
    var apiEndTime: String = ""

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDoYouHaveDartChargeAccountBinding {
        binding = FragmentDoYouHaveDartChargeAccountBinding.inflate(inflater, container, false)

        return binding
    }

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        LandingActivity.showToolBar(true)
        LandingActivity.setToolBarTitle("Raise a new enquiry")
        HomeActivityMain.accountDetailsData=null
        HomeActivityMain.checkedCrossing=null
        HomeActivityMain.crossing=null
        HomeActivityMain.dateRangeModel=null
        HomeActivityMain.paymentHistoryListData=null
        HomeActivityMain.paymentHistoryListDataCheckedCrossings= arrayListOf()

        if (!isChecked) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            webServiceViewModel.checkServiceStatus()
        }
        isChecked = true
        if (isPushNotificationChecked) {
            //callPushNotificationApi()
        }

        AdobeAnalytics.setScreenTrack(
            "home",
            "home",
            "english",
            "home",
            "splash",
            "home",
            sessionManager.getLoggedInUser()
        )
        binding?.btnContinue?.setOnClickListener {
            if(binding?.radioBtnYes?.isChecked==true){
                requireActivity().startActivity(Intent(requireActivity(),LoginActivity::class.java))
            }
            else if(binding?.radioBtnYes?.isChecked==false && binding?.radioBtnNo?.isChecked==true){
                requireActivity().startActivity(Intent(requireActivity(),CreateAccountActivity::class.java))

            }
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {

    }



    private fun getBundleData(state: String?,endTime:String?=null): Bundle? {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.SERVICE_TYPE, state)
        if(endTime!=null && endTime.replace("null","").isNotEmpty()){
            bundle.putString(Constants.END_TIME, endTime)
        }
        return bundle
    }

    private fun openUrlInWebBrowser() {
        val url = Constants.PCN_RESOLVE_URL
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"))
        }
    }

}