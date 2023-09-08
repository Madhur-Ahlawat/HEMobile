package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.FragmentCasesEnquiryDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CasesEnquiryDetailsFragment : BaseFragment<FragmentCasesEnquiryDetailsBinding>() {

    lateinit var viewModel: RaiseNewEnquiryViewModel
    var serviceRequest: ServiceRequest? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCasesEnquiryDetailsBinding =
        FragmentCasesEnquiryDetailsBinding.inflate(inflater, container, false)

    override fun init() {
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

        viewModel.enquiryDetailsModel.value=serviceRequest
    }

    override fun initCtrl() {

    }

    override fun observer() {
        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

}