package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
import java.io.File
import java.util.ArrayList

@AndroidEntryPoint
class SummaryEnquiryFragment : BaseFragment<FragmentSummaryEnquiryBinding>() {
    lateinit var viewModel: RaiseNewEnquiryViewModel
    private var loader: LoaderDialog? = null
    var apiSuccess: Boolean = false
    var isViewCreated: Boolean = false
    private var editRequest: String = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSummaryEnquiryBinding =
        FragmentSummaryEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.Edit_REQUEST_KEY) == true) {
            editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        }

        saveData()
        if (viewModel.enquiryModel.value?.category?.equals("A general enquiry")==true) {
            binding.contactDetailsTv.setText(resources.getString(R.string.str_check_your_answer_before_enquiry))
            binding.detailsEnquiryTv.setText(resources.getString(R.string.details_of_enquiry))
        }else{
            binding.contactDetailsTv.setText(resources.getString(R.string.str_check_your_answer_before_complaint))
            binding.detailsEnquiryTv.setText(resources.getString(R.string.details_of_complaint))
        }
        binding.includeEnquiryStatus.summaryRb.isChecked = true


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
            var arrayList: ArrayList<String> = ArrayList()
            if (viewModel.enquiryModel.value?.fileName != null && viewModel.enquiryModel.value?.fileName?.isNotEmpty() == true) {
                arrayList.add(viewModel.enquiryModel.value?.fileName ?: "")
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
                arrayList
            )
            apiSuccess = false
            viewModel.raiseEnquiryApi(
                enquiryRequestModel
            )
        }

        binding.categoryEditIv.setOnClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryCategoryFragment,
                getBundleData()
            )
        }
        binding.subcategoryEditIv.setOnClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryCategoryFragment,
                getBundleData()
            )
        }
        binding.contactMessageEditIv.setOnClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryCommentsFragment,
                getBundleData()
            )
        }
        binding.nameEditIv.setOnClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryContactDetailsFragment,
                getBundleData()
            )
        }
    }

    private fun saveData() {

        viewModel.enquiryModel.value?.name = viewModel.edit_enquiryModel.value?.name ?: ""
        viewModel.enquiryModel.value?.email = viewModel.edit_enquiryModel.value?.email ?: ""
        viewModel.enquiryModel.value?.mobileNumber =
            viewModel.edit_enquiryModel.value?.mobileNumber ?: ""
        viewModel.enquiryModel.value?.countryCode =
            viewModel.edit_enquiryModel.value?.countryCode ?: ""
        viewModel.enquiryModel.value?.fullcountryCode =
            viewModel.edit_enquiryModel.value?.fullcountryCode ?: ""

        viewModel.enquiryModel.value?.category =
            viewModel.edit_enquiryModel.value?.category ?: CaseCategoriesModel("", "")
        viewModel.enquiryModel.value?.subCategory =
            viewModel.edit_enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")

        viewModel.enquiryModel.value?.comments =
            viewModel.edit_enquiryModel.value?.comments ?: ""
        viewModel.enquiryModel.value?.file =
            viewModel.edit_enquiryModel.value?.file ?: File("")
        viewModel.enquiryModel.value?.fileName =
            viewModel.edit_enquiryModel.value?.fileName ?: ""
    }


    private fun getBundleData(): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putString(Constants.Edit_REQUEST_KEY, Constants.EDIT_SUMMARY)
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        return bundle
    }

    override fun initCtrl() {

    }

    override fun observer() {
        if (!isViewCreated) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

            viewModel = ViewModelProvider(requireActivity()).get(
                RaiseNewEnquiryViewModel::class.java
            )
            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            observe(viewModel.enquiryResponseLiveData, ::enquiryResponseModel)

        }
        isViewCreated = true
    }

    fun enquiryResponseModel(resource: Resource<EnquiryResponseModel?>?) {
        if (!apiSuccess) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    resource.data?.email = binding.emailDataTv.getText().toString()
                    resource.data?.category = viewModel.enquiryModel.value?.category?.value ?: ""
                    val bundle = Bundle()
                    bundle.putParcelable(
                        Constants.EnquiryResponseModel,
                        resource.data ?: EnquiryResponseModel()
                    )
                    bundle.putBoolean(
                        Constants.SHOW_BACK_BUTTON,
                        false
                    )
                    bundle.putString(
                        Constants.NAV_FLOW_FROM,
                        navFlowFrom
                    )
                    findNavController().navigate(
                        R.id.action_enquirySummaryFragment_to_enquirySuccessFragment,
                        bundle
                    )
                }

                is Resource.DataError -> {

                }

                else -> {

                }
            }
        }
        apiSuccess = true
    }
}


