package com.conduent.nationalhighways.ui.startNow.contactdartcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountTypeSelectionModel
import com.conduent.nationalhighways.data.model.contactdartcharge.CaseCategoriesModel
import com.conduent.nationalhighways.databinding.*
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import javax.inject.Inject

@AndroidEntryPoint
class NewCaseCategoryFragment : BaseFragment<FragmentNewCaseCategoryBinding>(),
    View.OnClickListener {

    private val viewModel: ContactDartChargeViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var mSelCat = ""
    private var mSelSubCat = ""
    private val mCatListName = ArrayList<String>()
    private val mSubCatListName = ArrayList<String>()
    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCategoryBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        checkButton()
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        AdobeAnalytics.setScreenTrack(
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries:raise a new enquiry category",
            "contact dart charge",
            "english",
            "case and enquiry",
            "home",
            "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries:raise a new enquiry category",
            sessionManager.getLoggedInUser()
        )


    }

    override fun initCtrl() {
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
        viewModel.getCaseCategoriesList()

        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCategoryFragment)
            categoryDropdown.setOnItemClickListener { _, _, position, _ ->
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                mSelCat = mCatListName[position]
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

    private fun handleCaseCategoryData(resource: Resource<List<CaseCategoriesModel?>?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let { it ->
                    if (it.isNotEmpty()) {
                        val mList = ArrayList<String>()
                        mCatListName.clear()
                        it.forEach { data ->
                            data?.value?.let { it1 -> mList.add(it1) }
                            data?.name?.let { it1 -> mCatListName.add(it1) }
                        }

                        val mAdapter =
                            ArrayAdapter(
                                requireActivity(),
                                R.layout.item_spinner,
                                mList
                            )
                        binding.categoryDropdown.setAdapter(mAdapter)
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
                        val mSubList = ArrayList<String>()
                        mSubCatListName.clear()
                        it.forEach { data ->
                            data?.value?.let { it1 -> mSubList.add(it1) }
                            data?.name?.let { it1 -> mSubCatListName.add(it1) }
                        }

                        val mAdapter1 =
                            ArrayAdapter(
                                requireActivity(),
                                R.layout.item_spinner,
                                mSubList
                            )
                        binding.subCategoryDropdown.setAdapter(mAdapter1)
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
                AdobeAnalytics.setActionTrack(
                    "next",
                    "home:contact dart charge:case and enquiry:do u have a dart charge account:details entry page:check case and enquiries:raise a new enquiry category",
                    "contact dart charge",
                    "english",
                    "case and enquiry",
                    "home",
                    sessionManager.getLoggedInUser()
                )

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