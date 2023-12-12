package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentServiceUnavailableBinding
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServiceUnavailableFragment : BaseFragment<FragmentServiceUnavailableBinding>(),
    BackPressListener {
    @Inject
    lateinit var sm: SessionManager
  private  var serviceType: String = ""
   private var endTime: String = ""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentServiceUnavailableBinding =
        FragmentServiceUnavailableBinding.inflate(inflater, container, false)

    override fun init() {

    }

    override fun initCtrl() {

        if (arguments?.containsKey(Constants.SERVICE_TYPE) == true) {
            serviceType = arguments?.getString(Constants.SERVICE_TYPE, "").toString()
        }

        if (arguments?.containsKey(Constants.END_TIME) == true) {
            endTime = arguments?.getString(Constants.END_TIME, "").toString()
        }
        showToolBar(true)

        setBackPressListener(this)

        binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
        Log.e("TAG", "loadFragment:screenType "+serviceType )
        binding.decs1Tv.gravity=Gravity.CENTER_HORIZONTAL

        when (serviceType) {
            Constants.MAINTENANCE -> {
                binding.decs1Tv.text = resources.getString(R.string.str_able_to_use_service, endTime)
                binding.btnNext.visible()
                binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
                binding.decs2Tv.gone()
                binding.decs3Tv.gone()
                binding.decs4Tv.gone()
                binding.btnGoToWebsite.gone()

            }
            Constants.UNAVAILABLE -> {
                binding.decs1Tv.text =
                    resources.getString(R.string.try_again_later_did_not_save_changes)
                binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
                binding.btnNext.visible()
                binding.decs2Tv.visible()
                binding.decs3Tv.visible()
                binding.decs4Tv.visible()
                binding.decs5Tv.visible()
                binding.btnGoToWebsite.gone()
            }
            Constants.LRDS_SCREEN -> {
                binding.titleTv.text = resources.getString(R.string.str_local_resident_discount_scheme)
                binding.decs1Tv.text =
                    resources.getString(R.string.str_manage_local_resident_discount_scheme)
                binding.decs1Tv.gravity=Gravity.LEFT
                binding.decs2Tv.gone()
                binding.decs3Tv.gone()
                binding.decs4Tv.gone()
                binding.decs5Tv.gone()
                binding.btnNext.gone()
                binding.btnGoToWebsite.visible()
                binding.btnGoToWebsite.text = resources.getString(R.string.str_go_to_website)
            }
        }

        binding.btnNext.setOnClickListener {
            if (serviceType == Constants.UNAVAILABLE || serviceType == Constants.MAINTENANCE) {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
            } else if(serviceType==Constants.LRDS_SCREEN){
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://web-highwaystest.services.conduent.com/sign-in")
                )
                startActivity(browserIntent)
            }

        }

    }

    override fun observer() {

    }

    override fun onBackButtonPressed() {
    }


}