package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.databinding.FragmentEnquiryCategoryBinding
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class EnquiryCategoryFragment : BaseFragment<FragmentEnquiryCategoryBinding>(),
    DropDownItemSelectListener, BackPressListener {

    lateinit var viewModel: RaiseNewEnquiryViewModel

    private var categoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    private var subcategoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    private var previousCategory: String = ""
    private var previousSubCategory: String = ""

    private var loader: LoaderDialog? = null
    private var isViewCreated: Boolean = false

    private var editRequest: String = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEnquiryCategoryBinding =
        FragmentEnquiryCategoryBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.containsKey(Constants.Edit_REQUEST_KEY) == true) {
            editRequest = arguments?.getString(Constants.Edit_REQUEST_KEY, "").toString()
        }
        setBackPressListener(this)

        binding.includeEnquiryStatus.categoryRb.isChecked = true
        saveEditData()

        binding.categoryDropdown.dropDownItemSelectListener = this
        binding.subcategoryDropdown.dropDownItemSelectListener = this
        binding.btnNext.setOnClickListener {
            if (editRequest == Constants.EDIT_SUMMARY && previousCategory == viewModel.edit_enquiryModel.value?.category?.value && previousSubCategory == viewModel.edit_enquiryModel.value?.subCategory?.value) {
                findNavController().navigate(
                    R.id.action_enquiryCategoryFragment_to_enquirySummaryFragment, getBundleData()
                )
            } else {
                findNavController().navigate(
                    R.id.action_enquiryCategoryFragment_to_enquiryCommentsFragment, getBundleData()
                )
            }

        }
        binding.cancelMb.setOnClickListener {
            Log.e("TAG", "init: navFlowFrom "+navFlowFrom )
            if (navFlowFrom == Constants.DART_CHARGE_GUIDANCE_AND_DOCUMENTS) {
                if(requireActivity() is HomeActivityMain){
                    HomeActivityMain().redirectToDashBoardFragment()
                }
            } else if (navFlowFrom == Constants.ACCOUNT_CONTACT_US) {
                if(requireActivity() is HomeActivityMain){
                    HomeActivityMain().redirectToAccountFragment()
                }
            } else {
                val intent = Intent(requireActivity(), LandingActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

    }

    private fun getBundleData(): Bundle {
        val bundle: Bundle = Bundle()
        if (editRequest == Constants.EDIT_SUMMARY) {
            bundle.putString(Constants.Edit_REQUEST_KEY, Constants.EDIT_CATEGORY_DATA)
        }
        bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)

        return bundle
    }


    private fun saveEditData() {
        previousCategory = viewModel.edit_enquiryModel.value?.category?.value ?: ""
        previousSubCategory = viewModel.edit_enquiryModel.value?.subCategory?.value ?: ""
    }

    override fun initCtrl() {

    }

    private fun getCategoriesApiCall() {
//        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getCategories()
    }

    override fun observer() {
        if (!isViewCreated) {
            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


            viewModel = ViewModelProvider(requireActivity()).get(
                RaiseNewEnquiryViewModel::class.java
            )

            getCategoriesApiCall()
            observe(viewModel.categoriesLiveData, ::categoriesData)
            observe(viewModel.subcategoriesLiveData, ::subCategoriesData)
        }
        isViewCreated = true
    }

    private fun subCategoriesData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data.orEmpty().size > 0) {
                    subcategoryList = resource.data as ArrayList<CaseCategoriesModel>
                    val subcategoryNameList =
                        resource.data.map { it.value ?: "" } as ArrayList<String>
                    binding.apply {
                        subcategoryDropdown.dataSet.clear()
                        subcategoryDropdown.dataSet.addAll(subcategoryNameList)
                    }

                    if (viewModel.edit_enquiryModel.value?.subCategory?.value?.isNotEmpty() == true) {
                        binding.apply {
                            subcategoryDropdown.setSelectedValue(
                                viewModel.edit_enquiryModel.value?.subCategory?.value ?: ""
                            )
                        }

                        binding.btnNext.enable()
                    }

                }
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }
    }


    private fun categoriesData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data.orEmpty().size > 0) {
                    categoryList = resource.data as ArrayList<CaseCategoriesModel>
                    val categoryNameList = resource.data.map { it.value ?: "" } as ArrayList<String>
                    binding.apply {
                        categoryDropdown.dataSet.clear()
                        categoryDropdown.dataSet.addAll(categoryNameList)
                    }

                    if (viewModel.edit_enquiryModel.value?.category?.value != null || viewModel.edit_enquiryModel.value?.category?.value?.isNotEmpty() == true) {
                        binding.apply {
                            categoryDropdown.setSelectedValue(
                                viewModel.edit_enquiryModel.value?.category?.value ?: ""
                            )
                        }
//                        loader?.show(
//                            requireActivity().supportFragmentManager, Constants.LOADER_DIALOG
//                        )

                        viewModel.getSubCategories(viewModel.edit_enquiryModel.value?.category?.name.toString())
                    }
                }
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }
    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {

    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        if (selectedItem.equals("A general enquiry") || selectedItem.equals("A complaint")) {
//            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

            viewModel.getSubCategories(categoryList[position].name.toString())
            viewModel.edit_enquiryModel.value?.category = categoryList.get(position)
            binding.apply {
                subcategoryDropdown.dataSet.clear()
            }
            viewModel.edit_enquiryModel.value?.subCategory = CaseCategoriesModel("", "")
        } else {
            viewModel.edit_enquiryModel.value?.subCategory = subcategoryList.get(position)
        }

        if (viewModel.edit_enquiryModel.value?.category?.name?.isEmpty() == true || viewModel.edit_enquiryModel.value?.subCategory?.name?.isEmpty() == true) {
            binding.btnNext.isEnabled = false
        } else {
            binding.btnNext.isEnabled = true
        }
    }

    override fun onBackButtonPressed() {
        saveOriginalDataToEditModel()
    }

    private fun saveOriginalDataToEditModel() {
        Log.e("TAG", "saveOriginalDataToEditModel: enquiryModel " + viewModel.enquiryModel.value)
        Log.e("TAG", "saveOriginalDataToEditModel: editRequest " + editRequest)
        if (editRequest == Constants.EDIT_SUMMARY) {

            viewModel.edit_enquiryModel.value?.name = viewModel.enquiryModel.value?.name ?: ""
            viewModel.edit_enquiryModel.value?.email = viewModel.enquiryModel.value?.email ?: ""
            viewModel.edit_enquiryModel.value?.mobileNumber =
                viewModel.enquiryModel.value?.mobileNumber ?: ""
            viewModel.edit_enquiryModel.value?.countryCode =
                viewModel.enquiryModel.value?.countryCode ?: ""
            viewModel.edit_enquiryModel.value?.fullcountryCode =
                viewModel.enquiryModel.value?.fullcountryCode ?: ""

            viewModel.edit_enquiryModel.value?.category =
                viewModel.enquiryModel.value?.category ?: CaseCategoriesModel("", "")
            viewModel.edit_enquiryModel.value?.subCategory =
                viewModel.enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")

            viewModel.edit_enquiryModel.value?.comments =
                viewModel.enquiryModel.value?.comments ?: ""
            viewModel.edit_enquiryModel.value?.file = viewModel.enquiryModel.value?.file ?: File("")
            viewModel.edit_enquiryModel.value?.fileName =
                viewModel.enquiryModel.value?.fileName ?: ""


        }
    }


}