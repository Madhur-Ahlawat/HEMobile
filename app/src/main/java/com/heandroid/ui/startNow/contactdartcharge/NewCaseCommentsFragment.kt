package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.contactdartcharge.CaseProvideDetailsModel
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCaseCommentsFragment : BaseFragment<FragmentNewCaseCommentBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCommentBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
        Logg.logging(
            "NewCaseComments",
            "bundle data CaseProvideDetailsModel ${
                arguments?.getParcelable<CaseProvideDetailsModel>(Constants.CASES_PROVIDE_DETAILS_KEY)
            }"
        )
        Logg.logging(
            "NewCaseComments",
            "bundle data cat  ${arguments?.getString(Constants.CASES_CATEGORY)}"
        )
        Logg.logging(
            "NewCaseComments",
            "bundle data  sub Cat ${arguments?.getString(Constants.CASES_SUB_CATEGORY)}"
        )
    }

    override fun initCtrl() {
        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCommentsFragment)
            tvSelectedCategory.text = arguments?.getString(Constants.CASES_CATEGORY)
            tvSubSelectCategory.text = arguments?.getString(Constants.CASES_SUB_CATEGORY)
        }
    }

    override fun observer() {}

    override fun onClick(it: View?) {

        when (it?.id) {
            R.id.btnNext -> {
                if (binding.tfDescriptionInput.text.toString().isNotEmpty()) {
                    findNavController().navigate(
                        R.id.action_NewCaseCommentsFragment_to_NewCaseSummeryFragment,
                        arguments?.apply {
                            putString(Constants.CASE_COMMENTS_KEY,binding.tfDescriptionInput.text.toString())
                        }
                    )
                } else {
                    requireActivity().showToast("Please add case comment")
                }
            }
            else -> {
            }
        }


    }

}