package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.*
import java.util.ArrayList

class NewCaseCategoryFragment : BaseFragment<FragmentNewCaseCategoryBinding>(),
    View.OnClickListener {


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCategoryBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
    }

    override fun initCtrl() {
        val mList = ArrayList<String>()
        /* val mCat1 = CaseCategoriesModel("ACCESSIBILITY REQUEST", "Accessibility Request")
         val mCat2 = CaseCategoriesModel("ACCOUNT MANAGEMENT", "Account Management")
         val mCat3 = CaseCategoriesModel("COMPLAINT", "Complaint")
         val mCat4 = CaseCategoriesModel("ENQUIRY", "Enquiry")
         val mCat5 = CaseCategoriesModel("REFUND", "Refund")*/
        mList.add("Accessibility Request")
        mList.add("Account Management")
        mList.add("Complaint")
        mList.add("Enquiry")
        mList.add("Refund")
        val mAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, mList)

        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCategoryFragment)
            categoryDropdown.setAdapter(mAdapter)
            categoryDropdown.setOnItemClickListener { _, _, position, _ ->
                val mSubList = ArrayList<String>()
                mSubList.add("Account")
                mSubList.add("Crossing")
                mSubList.add("Customer Service")
                mSubList.add("Payment")
                mSubList.add("Website")
                val mAdapter1 =
                    ArrayAdapter(
                        requireActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        mSubList
                    )

                subCategoryDropdown.setAdapter(mAdapter1)


            }
        }
    }


    override fun observer() {}

    override fun onClick(it: View?) {
        when (it?.id) {

            R.id.btnNext -> {
                findNavController().navigate(R.id.action_newCaseCategoryFragment_to_NewCaseCommentsFragment)
            }
            else -> {
            }
        }

    }

}