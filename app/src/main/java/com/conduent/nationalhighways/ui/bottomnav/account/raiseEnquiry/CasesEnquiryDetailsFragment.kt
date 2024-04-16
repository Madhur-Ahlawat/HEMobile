package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.ServiceRequest
import com.conduent.nationalhighways.databinding.FragmentCasesEnquiryDetailsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseAPIViewModel
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CasesEnquiryDetailsFragment : BaseFragment<FragmentCasesEnquiryDetailsBinding>() {
    private val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    private var serviceRequest: ServiceRequest? = null
    private val apiViewModel: RaiseAPIViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isViewCreated: Boolean = false
    @Inject
    lateinit var sm: SessionManager
    private var categoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    private var subcategoryList: ArrayList<CaseCategoriesModel?>? = ArrayList()
    private var apiCallPos: Int = 0
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCasesEnquiryDetailsBinding =
        FragmentCasesEnquiryDetailsBinding.inflate(inflater, container, false)

    override fun init() {

        if (requireActivity() is RaiseEnquiryActivity) {
            binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
        } else {
            binding.btnNext.text = resources.getString(R.string.str_continue)
        }
        if(requireActivity() is HomeActivityMain){
            (requireActivity() as HomeActivityMain).setTitle(requireActivity().resources.getString(R.string.enquiry_status))
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

        binding.categoryDataTv.text = Utils.capitalizeString(viewModel.enquiryDetailsModel.value?.category)
        binding.subcategoryDataTv.text = Utils.capitalizeString(viewModel.enquiryDetailsModel.value?.subcategory)

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
            binding.dateEnquiryClosedDataTv.text = DateUtils.convertDateToFullDate(
                viewModel.enquiryDetailsModel.value?.closedDate ?: ""
            )

        }
        binding.dateTimeDataTv.text =
            DateUtils.convertDateToFullDate(viewModel.enquiryDetailsModel.value?.created ?: "")

        setCategoryData()
        setEnquiryContentDescription()
    }
    private fun setEnquiryContentDescription() {
        val builder = StringBuilder()
        for (i in 0 until
                viewModel.enquiryDetailsModel.value!!.id!!.length) {
            builder.append(viewModel.enquiryDetailsModel.value!!.id!![i])
            builder.append("\u00A0")
        }
        binding.referenceNumberCv.contentDescription = getString(R.string.reference_number) + ", " + builder.toString().trim()
        binding.referenceNumberCl.contentDescription = getString(R.string.reference_number) + ", " + builder.toString().trim()

        binding.dateTimeCv.contentDescription = getString(R.string.date_time_submitted) + ", " + DateUtils.convertDateToFullDate(viewModel.enquiryDetailsModel.value?.created ?: "")
        binding.dateTimeCl.contentDescription = getString(R.string.date_time_submitted) + ", " + DateUtils.convertDateToFullDate(viewModel.enquiryDetailsModel.value?.created ?: "")

        binding.statusEnquiryCv.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.str_status_of_complaint) else getString(R.string.str_status_of_enquiry) + ", " + viewModel.enquiryDetailsModel.value!!.status
        binding.statusEnquiryCl.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.str_status_of_complaint) else getString(R.string.str_status_of_enquiry) + ", " + viewModel.enquiryDetailsModel.value!!.status

        binding.detailsEnquiryCv.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.details_of_complaint) else getString(R.string.details_of_enquiry) + ", " + viewModel.enquiryDetailsModel.value!!.description
        binding.detailsEnquiryCl.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.details_of_complaint) else getString(R.string.details_of_enquiry) + ", " + viewModel.enquiryDetailsModel.value!!.description

        binding.dateEnquiryClosedCv.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.str_date_complaint_closed) else getString(R.string.str_date_enquiry_closed) + ", " + DateUtils.convertDateToFullDate(
            viewModel.enquiryDetailsModel.value?.closedDate ?: ""
        )
        binding.dateEnquiryClosedCl.contentDescription = if(viewModel.enquiryDetailsModel.value!!.category.equals("COMPLAINT"))  getString(R.string.str_date_complaint_closed) else getString(R.string.str_date_enquiry_closed) + ", " + DateUtils.convertDateToFullDate(
            viewModel.enquiryDetailsModel.value?.closedDate ?: ""
        )
    }

    private fun setCategoryData() {
        if (sm.fetchSubCategoriesData().size == 0) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            apiViewModel.getCategories()
        } else {
            getCategoryDataFromSession()
        }
    }

    override fun initCtrl() {
//        setEnquiryContentDescription()
    }

    override fun observer() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (!isViewCreated) {

            observe(apiViewModel.categoriesLiveData, ::categoriesData)
            observe(apiViewModel.subcategoriesLiveData, ::subCategoriesData)
        }
        isViewCreated = true

    }

    private fun categoriesData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data.orEmpty().isNotEmpty()) {
                    categoryList.clear()
                    categoryList = resource.data as ArrayList<CaseCategoriesModel>
                    callSubCategoryApi()
                    setEnquiryContentDescription()
                }
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                }
            }

            else -> {

            }
        }
    }

    private fun callSubCategoryApi() {
        if (categoryList.size > apiCallPos) {
            apiViewModel.getSubCategories(categoryList[apiCallPos].name.toString())
        } else {
            getCategoryDataFromSession()
        }
        apiCallPos++
    }

    private fun getCategoryDataFromSession() {
        val subCategories = sm.fetchSubCategoriesData()

        for (i in 0 until subCategories.size) {
            val subcategorySplit = subCategories[i].name?.split("~")
            var seletedArea = ""
            var selectSubArea = ""
            if (subcategorySplit.orEmpty().isNotEmpty()) {
                seletedArea = subcategorySplit?.get(0).toString()
            }
            if (subcategorySplit.orEmpty().size > 1) {
                selectSubArea = subcategorySplit?.get(1).toString()
            }


            if (seletedArea == viewModel.enquiryDetailsModel.value?.category.toString()
                && selectSubArea == viewModel.enquiryDetailsModel.value?.subcategory.toString()
            ) {
                binding.categoryDataTv.text = Utils.capitalizeString(subCategories[i].category)
                binding.subcategoryDataTv.text = Utils.capitalizeString(subCategories[i].value)
                binding.categoryCv.contentDescription = getString(R.string.category) + ", " + Utils.capitalizeString(subCategories[i].category)
                binding.categoryCl.contentDescription = getString(R.string.category) + ", " + Utils.capitalizeString(subCategories[i].category)
                binding.subcategoryCv.contentDescription = getString(R.string.sub_category) + ", " + Utils.capitalizeString(subCategories[i].value)
                binding.subcategoryCl.contentDescription = getString(R.string.sub_category) + ", " + Utils.capitalizeString(subCategories[i].value)
                break
            }
        }

        binding.dataLl.visible()
    }

    private fun subCategoriesData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        if (categoryList.size <= apiCallPos) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
        }
        if (apiCallPos == 1) {
            subcategoryList?.clear()
        }

        when (resource) {
            is Resource.Success -> {
                if (resource.data.orEmpty().isNotEmpty()) {
                    resource.data?.forEach {
                        it?.apply {
                            category = categoryList[apiCallPos - 1].name ?: ""
                        }
                    }
                    resource.data?.let { subcategoryList?.addAll(it) }

                    callSubCategoryApi()
                    if (categoryList.size <= apiCallPos) {
                        sm.saveSubCategoriesData(subcategoryList)
                    }
                }
                setEnquiryContentDescription()
            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(resource.errorModel)
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                }
            }

            else -> {

            }
        }
    }


}