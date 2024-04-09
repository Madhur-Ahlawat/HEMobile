package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryRequest
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryResponseModel
import com.conduent.nationalhighways.databinding.FragmentSummaryEnquiryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseAPIViewModel
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SummaryEnquiryFragment : BaseFragment<FragmentSummaryEnquiryBinding>() {
    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    private var apiSuccess: Boolean = false
    private var isViewCreated: Boolean = false
    private var editRequest: String = ""
    private val apiViewModel: RaiseAPIViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSummaryEnquiryBinding =
        FragmentSummaryEnquiryBinding.inflate(inflater, container, false)

    override fun init() {
        setEnquiryContentDescription()
        if (arguments?.containsKey(Constants.Edit_REQUEST_KEY) == true) {
            editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        }
        saveData()
        if (viewModel.enquiryModel.value?.category.toString().contains("enquiry")) {
            binding.contactDetailsTv.text =
                resources.getString(R.string.str_check_your_answer_before_enquiry)
            binding.detailsEnquiryTv.text = resources.getString(R.string.details_of_enquiry)
        } else {
            binding.contactDetailsTv.text =
                resources.getString(R.string.str_check_your_answer_before_complaint)
            binding.detailsEnquiryTv.text = resources.getString(R.string.details_of_complaint)
        }
        binding.btnNext.setOnClickListener {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

            val subcategorySplit = viewModel.enquiryModel.value?.subCategory?.name?.split("~")
            var selectSubArea = ""
            var seletedArea = ""
            if (subcategorySplit.orEmpty().isNotEmpty()) {
                selectSubArea = subcategorySplit?.get(0).toString()
            }
            if (subcategorySplit.orEmpty().size > 1) {
                seletedArea = subcategorySplit?.get(1).toString()
            }
            val arrayList: ArrayList<String> = ArrayList()
            if (viewModel.enquiryModel.value?.apiFileName != null && viewModel.enquiryModel.value?.apiFileName?.isNotEmpty() == true) {
                arrayList.add(viewModel.enquiryModel.value?.apiFileName ?: "")
            }

            val enquiryRequestModel = EnquiryRequest(
                viewModel.enquiryModel.value?.firstname.toString(),
                viewModel.enquiryModel.value?.lastname.toString(),
                viewModel.enquiryModel.value?.email.toString(),
                viewModel.enquiryModel.value?.mobileNumber.toString(),
                viewModel.enquiryModel.value?.countryCode.toString(),
                viewModel.enquiryModel.value?.comments.toString(),
                seletedArea,
                selectSubArea,
                arrayList
            )
            apiSuccess = false
            apiViewModel.raiseEnquiryApi(
                enquiryRequestModel
            )
        }
        binding.setCategoryClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryCategoryFragment, getBundleData()
            )
        }
        binding.setCommentsClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryCommentsFragment, getBundleData()
            )
        }
        binding.setContactDetailsClickListener {
            findNavController().navigate(
                R.id.action_enquirySummaryFragment_to_enquiryContactDetailsFragment,
                getBundleData()
            )
        }
        setEnquiryContentDescription()
    }

    private fun setEnquiryContentDescription() {
        binding.cvCategory.contentDescription = getString(R.string.category) + ", " + viewModel.enquiryModel.value!!.category.value
        binding.layoutCategory.contentDescription = getString(R.string.category) + ", " + viewModel.enquiryModel.value!!.category.value
        binding.cvSubCategory.contentDescription = getString(R.string.sub_category) + ", " + viewModel.enquiryModel.value!!.subCategory.value
        binding.layoutSubCategory.contentDescription = getString(R.string.sub_category) + ", " + viewModel.enquiryModel.value!!.subCategory.value
        binding.nameCv.contentDescription = getString(R.string.name) + ", " + viewModel.enquiryModel.value!!.firstname + " " + viewModel.enquiryModel.value!!.lastname
        binding.layoutName.contentDescription = getString(R.string.name) + ", " + viewModel.enquiryModel.value!!.firstname + " " + viewModel.enquiryModel.value!!.lastname
        binding.emailCv.contentDescription = getString(R.string.email_address) + ", " + viewModel.enquiryModel.value!!.email
        binding.layoutEmail.contentDescription = getString(R.string.email_address) + ", " + viewModel.enquiryModel.value!!.email
        val builder = StringBuilder()
        for (i in 0 until
                viewModel.enquiryModel.value!!.countryCode.length) {
            builder.append(viewModel.enquiryModel.value!!.countryCode[i])
            builder.append("\u00A0")
        }
        for (i in 0 until
                viewModel.enquiryModel.value!!.mobileNumber.length) {
            builder.append(viewModel.enquiryModel.value!!.mobileNumber[i])
            builder.append("\u00A0")
        }
        binding.mobileCv.contentDescription = getString(R.string.mobile_number) + ", " + builder.toString()
        binding.layoutMobileNumber.contentDescription = getString(R.string.mobile_number) + ", " + builder.toString()
    }

    private fun saveData() {
        viewModel.enquiryModel.value?.firstname = viewModel.edit_enquiryModel.value?.firstname ?: ""
        viewModel.enquiryModel.value?.lastname = viewModel.edit_enquiryModel.value?.lastname ?: ""
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

        viewModel.enquiryModel.value?.comments = viewModel.edit_enquiryModel.value?.comments ?: ""
        viewModel.enquiryModel.value?.file = viewModel.edit_enquiryModel.value?.file ?: File("")
        viewModel.enquiryModel.value?.fileName = viewModel.edit_enquiryModel.value?.fileName ?: ""
        viewModel.enquiryModel.value?.apiFileName =
            viewModel.edit_enquiryModel.value?.apiFileName ?: ""

        binding.mobileDataTv.setText(viewModel.enquiryModel.value!!.mobileNumber)
        val builder = StringBuilder()
        for (i in 0 until viewModel.enquiryModel.value!!.countryCode.length) {
            builder.append(viewModel.enquiryModel.value!!.countryCode[i])
            builder.append("\u00A0")

        }
        for (i in 0 until viewModel.enquiryModel.value!!.mobileNumber.length) {
            builder.append(viewModel.enquiryModel.value!!.mobileNumber[i])
            builder.append("\u00A0")
        }
        binding.mobileCv.contentDescription = getString(R.string.mobile_number) + " " + builder.toString()

    }

    private fun getBundleData(): Bundle {
        val bundle = Bundle()
        bundle.putString(Constants.Edit_REQUEST_KEY, Constants.EDIT_SUMMARY)
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, true)
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        return bundle
    }

    override fun initCtrl() {
        setEnquiryContentDescription()
    }

    override fun observer() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        if (!isViewCreated) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

            observe(apiViewModel.enquiryResponseLiveData, ::enquiryResponseModel)

        }
        isViewCreated = true
    }

    private fun enquiryResponseModel(resource: Resource<EnquiryResponseModel?>?) {
        if (!apiSuccess) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    resource.data?.email = binding.emailDataTv.text.toString()
                    resource.data?.category = viewModel.enquiryModel.value?.category?.value ?: ""
                    val bundle = Bundle()
                    bundle.putParcelable(
                        Constants.EnquiryResponseModel, resource.data ?: EnquiryResponseModel()
                    )
                    bundle.putBoolean(
                        Constants.SHOW_BACK_BUTTON, false
                    )
                    bundle.putString(
                        Constants.NAV_FLOW_FROM, navFlowFrom
                    )

                    findNavController().navigate(
                        R.id.action_enquirySummaryFragment_to_enquirySuccessFragment, bundle
                    )
                }

                is Resource.DataError -> {
                    if (checkSessionExpiredOrServerError(resource.errorModel)) {
                        displaySessionExpireDialog(resource.errorModel)
                    } else {
                        ErrorUtil.showError(binding.root, resource.errorMsg)
                    }
                }

                else -> {

                }
            }
        }
        apiSuccess = true
    }
}