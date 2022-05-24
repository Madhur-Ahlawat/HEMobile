package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.contactdartcharge.CaseCategoriesModel
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.data.model.contactdartcharge.CreateNewCaseReq
import com.heandroid.data.model.contactdartcharge.CreateNewCaseResp
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class NewCaseSummeryFragment : BaseFragment<FragmentNewCaseSummaryBinding>(),
    View.OnClickListener {
    private val viewModel: ContactDartChargeViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseSummaryBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        Logg.logging(
            "NewCaseSummeryFragment",
            "bundle data CaseProvideDetailsModel ${
                arguments?.getParcelable<CaseProvideDetailsModel>(Constants.CASES_PROVIDE_DETAILS_KEY)
            }"
        )
        Logg.logging(
            "NewCaseSummeryFragment",
            "bundle data cat  ${arguments?.getString(Constants.CASES_CATEGORY)}"
        )
        Logg.logging(
            "NewCaseSummeryFragment",
            "bundle data  sub Cat ${arguments?.getString(Constants.CASES_SUB_CATEGORY)}"
        )
        Logg.logging(
            "NewCaseSummeryFragment",
            "bundle data  sub comments ${arguments?.getString(Constants.CASE_COMMENTS_KEY)}"
        )
        Logg.logging(
            "NewCaseSummeryFragment",
            "string arraylist ${arguments?.getStringArrayList(Constants.FILE_NAMES_KEY)}"
        )

        val mTempList = arguments?.getStringArrayList(Constants.FILE_NAMES_KEY) as ArrayList<String>
        mList.addAll(mTempList)

        Logg.logging(
            "NewCaseSummeryFragment",
            "string mList $mList"
        )

    }

    override fun initCtrl() {
        binding.apply {
            btnNext.setOnClickListener(this@NewCaseSummeryFragment)
            rlCategoryVal.text = arguments?.getString(Constants.CASES_CATEGORY)
            rlSubCategoryVal.text = arguments?.getString(Constants.CASES_SUB_CATEGORY)
            rlCommentsVal.text = arguments?.getString(Constants.CASE_COMMENTS_KEY)
            rlTransactionVal.text = Utils.currentDateAndTime()

        }
    }

    override fun observer() {
        observe(viewModel.createNewCaseVal, ::createNewCase)
    }

    private fun createNewCase(resource: Resource<CreateNewCaseResp?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    Logg.logging("NewCaseSummeryFragment", "list data $it ")
                    if (it.statusCode == "0") {
                        findNavController().navigate(
                            R.id.action_NewCaseSummeryFragment_to_CaseCreatedSuccessfullyFragment,
                            arguments?.apply {
                                putString(Constants.CASE_NUMBER, it.srNumber)
                                if (mModel == null) {
                                    putString(
                                        Constants.LAST_NAME,
                                        requireActivity().intent.getStringExtra(Constants.LAST_NAME)
                                    )
                                } else {
                                    putString(
                                        Constants.LAST_NAME,
                                        arguments?.getParcelable<CaseProvideDetailsModel>(Constants.CASES_PROVIDE_DETAILS_KEY)!!.emailId
                                    )

                                }
                            }
                        )
                    } else {
                        requireActivity().showToast("Please Check some thing wrong , case not created")
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

    private val mList = mutableListOf<String>()
    private var mModel: CaseProvideDetailsModel? = null
    override fun onClick(it: View?) {

        when (it?.id) {

            R.id.btnNext -> {

                mModel =
                    arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY)
                val mCat = arguments?.getString(Constants.CASES_CATEGORY)
                val mSubCat = arguments?.getString(Constants.CASES_SUB_CATEGORY)
                val mComment = arguments?.getString(Constants.CASE_COMMENTS_KEY)

/*
                val newCaseReq = CreateNewCaseReq(
                    mModel!!.fName,
                    mModel.lName,
                    mModel.emailId,
                    mModel.telephoneNo,
                    "",
                    mComment,
                    "OTHER",//SUB
                    "WEB",//CAT
                    mList,
                    "ENU"
                )
*/
                val newCaseReq: CreateNewCaseReq?
                if (mModel == null) {
                    newCaseReq = CreateNewCaseReq(
                        "",
                        "",
                        "",
                        "",
                        "",
                        mComment,
                        mSubCat,
                        mCat,
                        mList,
                        "ENU"
                    )

                } else {
                    newCaseReq = CreateNewCaseReq(
                        mModel?.fName,
                        mModel?.lName,
                        mModel?.emailId,
                        mModel?.telephoneNo,
                        "",
                        mComment,
                        mSubCat,
                        mCat,
                        mList,
                        "ENU"
                    )

                }
                loader?.show(requireActivity().supportFragmentManager, "Loader")
                viewModel.createNewCase(newCaseReq)
            }
            else -> {
            }
        }


    }


}