package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.FragmentCasesEnquiryDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CasesEnquiryDetailsFragment : BaseFragment<FragmentCasesEnquiryDetailsBinding>() {

    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    var serviceRequest: ServiceRequest? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCasesEnquiryDetailsBinding =
        FragmentCasesEnquiryDetailsBinding.inflate(inflater, container, false)

    override fun init() {
        if (requireActivity() is RaiseEnquiryActivity) {
            binding.btnNext.setText(resources.getString(R.string.str_go_to_start_menu))
        } else {
            binding.btnNext.setText(resources.getString(R.string.str_continue))
        }
        if (arguments?.containsKey(Constants.EnquiryResponseModel) == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (arguments?.getParcelable(
                        Constants.EnquiryResponseModel,
                        ServiceRequest::class.java
                    ) != null
                ) {
                    serviceRequest = arguments?.getParcelable(

                        Constants.EnquiryResponseModel, ServiceRequest::class.java
                    )
                }
            } else {
                if (arguments?.getParcelable<ServiceRequest>(Constants.EnquiryResponseModel) != null) {
                    serviceRequest = arguments?.getParcelable(
                        Constants.EnquiryResponseModel,
                    )
                }
            }

        }

        viewModel.enquiryDetailsModel.value = serviceRequest

        binding.btnNext.setOnClickListener {
            if (requireActivity() is RaiseEnquiryActivity) {
                requireActivity().startNormalActivityWithFinish(LandingActivity::class.java)
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        if (viewModel.enquiryDetailsModel.value?.status.equals("Open")) {
            binding.dateEnquiryClosedCv.gone()
        } else {
            binding.dateEnquiryClosedCv.visible()
            binding.dateEnquiryClosedTv.text=DateUtils.convertDateToFullDate(viewModel.enquiryDetailsModel.value?.closedDate?:"")

        }
        binding.dateTimeDataTv.text=DateUtils.convertDateToFullDate(viewModel.enquiryDetailsModel.value?.created?:"")
    }

    override fun initCtrl() {

    }

    override fun observer() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

}