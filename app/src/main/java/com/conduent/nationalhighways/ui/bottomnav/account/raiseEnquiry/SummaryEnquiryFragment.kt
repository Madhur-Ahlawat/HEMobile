package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.databinding.FragmentSummaryEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class SummaryEnquiryFragment : BaseFragment<FragmentSummaryEnquiryBinding>() {
    lateinit var viewModel: RaiseNewEnquiryViewModel
    private var loader: LoaderDialog? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSummaryEnquiryBinding =
        FragmentSummaryEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnNext.setOnClickListener {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

            val subcategorySplit = viewModel.enquiryModel.value?.subCategory?.name?.split("~")
            var selectSubArea = ""
            var seletedArea = ""
            Log.e("TAG", "init: subCategory--> " + viewModel.enquiryModel.value?.subCategory?.name)
            Log.e("TAG", "init: subCategory size--> " + subcategorySplit.orEmpty().size)
            if (subcategorySplit.orEmpty().size > 0) {
                selectSubArea = subcategorySplit?.get(0).toString()
            }
            if (subcategorySplit.orEmpty().size > 1) {
                seletedArea = subcategorySplit?.get(1).toString()
            }
            val enquiryRequestModel = EnquiryRequest(
                viewModel.enquiryModel.value?.name.toString(),
                viewModel.enquiryModel.value?.name.toString(),
                viewModel.enquiryModel.value?.email.toString(),
                viewModel.enquiryModel.value?.mobileNumber.toString(),
                viewModel.enquiryModel.value?.countryCode.toString(),
                viewModel.enquiryModel.value?.comments.toString(),
                seletedArea,
                selectSubArea,
                ArrayList()
            )
            viewModel.raiseEnquiryApi(
                enquiryRequestModel
            )
        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observe(viewModel.enquiryResponseLiveData, ::enquiryResponseModel)


    }

    fun enquiryResponseModel(resource: Resource<EnquiryResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                Log.e("TAG", "enquiryResponseModel: --> " + resource.data.toString())
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }

    }

}


