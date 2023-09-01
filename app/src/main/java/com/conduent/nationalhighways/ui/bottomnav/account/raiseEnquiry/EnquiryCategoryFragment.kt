package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.databinding.FragmentEnquiryCategoryBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EnquiryCategoryFragment : BaseFragment<FragmentEnquiryCategoryBinding>(),
    DropDownItemSelectListener {

    lateinit var viewModel: RaiseNewEnquiryViewModel

    private var categoryList: ArrayList<CaseCategoriesModel> = ArrayList()
    private var subcategoryList: ArrayList<CaseCategoriesModel> = ArrayList()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryCategoryBinding =
        FragmentEnquiryCategoryBinding.inflate(inflater, container, false)

    override fun init() {


        binding.categoryDropdown.dropDownItemSelectListener = this
        binding.subcategoryDropdown.dropDownItemSelectListener = this
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_enquiryCategoryFragment_to_enquiryCommentsFragment)
        }
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
                        subcategoryDropdown.dataSet.addAll(subcategoryNameList)
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
                        categoryDropdown.dataSet.addAll(categoryNameList)
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
        Log.e("TAG", "onItemSlected: selectedItem "+selectedItem )
        if (selectedItem.equals("A general enquiry") || selectedItem.equals("A complaint")) {
            viewModel.getSubCategories(categoryList[position].name.toString())
            viewModel.enquiryModel.value?.category = categoryList.get(position)
        } else {
            viewModel.enquiryModel.value?.subCategory = subcategoryList.get(position)
        }

        if (
            viewModel.enquiryModel.value?.category?.name?.isEmpty() == true ||
            viewModel.enquiryModel.value?.subCategory?.name?.isEmpty() == true
        ) {
            binding.btnNext.isEnabled = false
        } else {
            binding.btnNext.isEnabled = true
        }
    }


}