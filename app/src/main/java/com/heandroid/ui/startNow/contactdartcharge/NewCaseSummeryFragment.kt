package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.data.model.contactdartcharge.CreateNewCaseReq
import com.heandroid.data.model.contactdartcharge.CreateNewCaseResp
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewCaseSummeryFragment : BaseFragment<FragmentNewCaseSummaryBinding>(),
    View.OnClickListener {

    private val viewModel: ContactDartChargeViewModel by viewModels()
    private val mList = mutableListOf<String>()
    private var mModel: CaseProvideDetailsModel? = null
    private var loader: LoaderDialog? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseSummaryBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        val mTempList = arguments?.getStringArrayList(Constants.FILE_NAMES_KEY) as ArrayList<String>
        mList.addAll(mTempList)

    }

    override fun initCtrl() {
        binding.apply {
            btnNext.setOnClickListener(this@NewCaseSummeryFragment)
            rlCategoryVal.text = arguments?.getString(Constants.CASES_CATEGORY)
            rlSubCategoryVal.text = arguments?.getString(Constants.CASES_SUB_CATEGORY)
            rlCommentsVal.text = arguments?.getString(Constants.CASE_COMMENTS_KEY)
            rlTransactionVal.text = Utils.currentDateAndTime()
            file1.gone()
            file2.gone()
            file3.gone()
            file4.gone()
        }
        mList.forEachIndexed { index, s ->
            if (index == 0) {
                binding.file1.visible()
                binding.fileName1.text = s
            }
            if (index == 1) {
                binding.file2.visible()
                binding.fileName2.text = s
            }
            if (index == 2) {
                binding.file3.visible()
                binding.fileName3.text = s
            }
            if (index == 3) {
                binding.file4.visible()
                binding.fileName4.text = s
            }
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
                        ErrorUtil.showError(binding.root, "Something went wrong. Try again later")
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
                mModel = arguments?.getParcelable(Constants.CASES_PROVIDE_DETAILS_KEY)
                val mCat = arguments?.getString(Constants.CASES_CATEGORY)
                val mSubCat = arguments?.getString(Constants.CASES_SUB_CATEGORY)
                val mComment = arguments?.getString(Constants.CASE_COMMENTS_KEY)

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