package com.heandroid.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.AccountTypeSelectionModel
import com.heandroid.data.model.contactdartcharge.CaseCategoriesModel
import com.heandroid.data.model.contactdartcharge.CaseEnquiryHistoryResponse
import com.heandroid.data.model.contactdartcharge.CreateNewCaseResp
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class NewCaseCategoryFragment : BaseFragment<FragmentNewCaseCategoryBinding>(),
    View.OnClickListener {
    private val viewModel: ContactDartChargeViewModel by viewModels()
    private lateinit var accountModel: AccountTypeSelectionModel
    private var loader: LoaderDialog? = null

    private var mSelCat = ""
    private var mSelSubCat = ""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCategoryBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getCaseCategoriesList()

        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCategoryFragment)
            categoryDropdown.setOnItemClickListener { parent, view, position, id ->
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                Logg.logging(
                    "NewCaseCategoryFrag",
                    "categoryDropdown position  $position  parent $parent  view $view  id  $id"
                )
                mSelCat = mCatListName[position]
                Logg.logging(
                    "NewCaseCategoryFrag",
                    "categoryDropdown mSelCat  $mSelCat "
                )
                checkButton()
                viewModel.getCaseSubCategoriesList(mSelCat)
            }
            subCategoryDropdown.setOnItemClickListener { _, _, position, _ ->
//                mSelSubCat = parent.getItemAtPosition(position) as String
                mSelSubCat = mSubCatListName[position]
                checkButton()
            }
        }
    }

    private fun checkButton() {
        binding.apply {
            model = AccountTypeSelectionModel(
                mSelCat.isNotEmpty() &&
                        mSelSubCat.trim()
                            .isNotEmpty()
            )
        }
    }

    override fun observer() {
        observe(viewModel.getCaseCategoriesListVal, ::handleCaseCategoryData)
        observe(viewModel.getCaseSubCategoriesListVal, ::handleCaseSubCategoryData)
    }

    private val mCatListName = ArrayList<String>()
    private val mSubCatListName = ArrayList<String>()

    private fun handleCaseCategoryData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { it ->
                    if (it.isNotEmpty()) {
                        binding.categoryDropdown.setText("Select")

                        val mList = ArrayList<String>()
                        mCatListName.clear()
                        it.forEach { data ->
                            mList.add(data?.value!!)
                            mCatListName.add(data.name!!)
                        }

                        val mAdapter =
                            ArrayAdapter(
                                requireActivity(),
                                R.layout.item_spinner,
                                mList
                            )

                        binding.categoryDropdown.setAdapter(mAdapter)
                        Logg.logging("NewCaseCategoryFrag", "list data $it ")
                    } else {
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    private fun handleCaseSubCategoryData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    if (it.isNotEmpty()) {
                        Logg.logging("NewCaseSubCategoryFrag", "list data $it ")
                        val mSubList = ArrayList<String>()
                        binding.subCategoryDropdown.setText("Select")
                        mSubCatListName.clear()
                        it.forEach { data ->
                            mSubList.add(data?.value!!)
                            mSubCatListName.add(data.name!!)
                        }

                        val mAdapter1 =
                            ArrayAdapter(
                                requireActivity(),
                                R.layout.item_spinner,
                                mSubList
                            )
                        binding.subCategoryDropdown.setAdapter(mAdapter1)


                    } else {
                    }
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }


    override fun onClick(it: View?) {
        when (it?.id) {

            R.id.btnNext -> {

                findNavController().navigate(
                    R.id.action_newCaseCategoryFragment_to_NewCaseCommentsFragment,
                    Bundle().apply {
                        putParcelable(
                            Constants.CASES_PROVIDE_DETAILS_KEY,
                            arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY)
                        )
                        putString(Constants.CASES_CATEGORY, mSelCat)
                        putString(Constants.CASES_SUB_CATEGORY, mSelSubCat)

                    })
            }
            else -> {
            }
        }

    }

}