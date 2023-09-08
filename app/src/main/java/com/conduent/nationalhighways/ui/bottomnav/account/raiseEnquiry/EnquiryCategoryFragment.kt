package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.databinding.FragmentEnquiryCategoryBinding
import com.conduent.nationalhighways.ui.base.BackPressListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EnquiryCategoryFragment : BaseFragment<FragmentEnquiryCategoryBinding>(),
    DropDownItemSelectListener, BackPressListener {

    lateinit var viewModel: RaiseNewEnquiryViewModel

    private var categoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    private var subcategoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    var previousCategory: String = ""
    var previousSubCategory: String = ""
    var onviewCreated: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryCategoryBinding =
        FragmentEnquiryCategoryBinding.inflate(inflater, container, false)

    override fun init() {
        Log.e("TAG", "init: onviewCreated " + onviewCreated)

        if (!onviewCreated) {
            onviewCreated = true
        }
        binding.includeEnquiryStatus.categoryRb.isChecked = true
        saveEditData()

        binding.categoryDropdown.dropDownItemSelectListener = this
        binding.subcategoryDropdown.dropDownItemSelectListener = this
        binding.btnNext.setOnClickListener {
            if (navFlowFrom == Constants.EDIT_SUMMARY &&
                previousCategory == viewModel.edit_enquiryModel.value?.category?.value &&
                previousSubCategory == viewModel.edit_enquiryModel.value?.subCategory?.value
            ) {
                findNavController().navigate(
                    R.id.action_enquiryCategoryFragment_to_enquirySummaryFragment, getBundleData()
                )
            } else {
                if (navFlowFrom.isEmpty()) {
                    saveData()
                }
                findNavController().navigate(
                    R.id.action_enquiryCategoryFragment_to_enquiryCommentsFragment,
                    getBundleData()
                )
            }

        }
        binding.cancelMb.setOnClickListener {
            val intent = Intent(requireActivity(), LandingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

//        backListener()
    }

    private fun backListener() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("TAG", "onCreateView: handleOnBackPressed:--> ")
                saveEditData()

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun getBundleData(): Bundle {
        val bundle: Bundle = Bundle()

        if (navFlowFrom == Constants.EDIT_SUMMARY) {
            bundle.putString(Constants.NAV_FLOW_FROM, Constants.EDIT_CATEGORY_DATA)
        } else {
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
        }

        return bundle
    }

    private fun saveData() {
        viewModel.enquiryModel.value?.category =
            viewModel.edit_enquiryModel.value?.category ?: CaseCategoriesModel("", "")
        viewModel.enquiryModel.value?.subCategory =
            viewModel.edit_enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")
    }

    private fun saveEditData() {
        viewModel.edit_enquiryModel.value?.category =
            viewModel.enquiryModel.value?.category ?: CaseCategoriesModel("", "")
        viewModel.edit_enquiryModel.value?.subCategory =
            viewModel.enquiryModel.value?.subCategory ?: CaseCategoriesModel("", "")

        previousCategory = viewModel.edit_enquiryModel.value?.category?.value ?: ""
        previousSubCategory = viewModel.edit_enquiryModel.value?.subCategory?.value ?: ""
    }

    override fun initCtrl() {

    }

    private fun getCategoriesApiCall() {
        viewModel.getCategories()
    }

    override fun observer() {
        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )

        getCategoriesApiCall()
        observe(viewModel.categoriesLiveData, ::categoriesData)
        observe(viewModel.subcategoriesLiveData, ::subCategoriesData)
    }

    private fun subCategoriesData(resource: Resource<List<CaseCategoriesModel?>?>?) {
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
        when (resource) {
            is Resource.Success -> {
                if (resource.data.orEmpty().size > 0) {
                    categoryList = resource.data as ArrayList<CaseCategoriesModel>
                    val categoryNameList =
                        resource.data.map { it.value ?: "" } as ArrayList<String>
                    binding.apply {
                        categoryDropdown.dataSet.clear()
                        categoryDropdown.dataSet.addAll(categoryNameList)
                    }

//                    if (navFlowCall == Constants.EDIT_SUMMARY) {
                    if (viewModel.edit_enquiryModel.value?.category?.value != null || viewModel.edit_enquiryModel.value?.category?.value?.isNotEmpty() == true) {
                        binding.apply {
                            categoryDropdown.setSelectedValue(
                                viewModel.edit_enquiryModel.value?.category?.value ?: ""
                            )
                        }
                        viewModel.getSubCategories(viewModel.edit_enquiryModel.value?.category?.name.toString())
                    }
//                    }
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
            viewModel.getSubCategories(categoryList[position].name.toString())
            viewModel.edit_enquiryModel.value?.category = categoryList.get(position)
            binding.apply {
                subcategoryDropdown.dataSet.clear()
            }
            viewModel.edit_enquiryModel.value?.subCategory = CaseCategoriesModel("", "")
        } else {
            viewModel.edit_enquiryModel.value?.subCategory = subcategoryList.get(position)
        }

        if (
            viewModel.edit_enquiryModel.value?.category?.name?.isEmpty() == true ||
            viewModel.edit_enquiryModel.value?.subCategory?.name?.isEmpty() == true
        ) {
            binding.btnNext.isEnabled = false
        } else {
            binding.btnNext.isEnabled = true
        }
    }

    override fun onBackButtonPressed() {
        saveEditData()
    }


}