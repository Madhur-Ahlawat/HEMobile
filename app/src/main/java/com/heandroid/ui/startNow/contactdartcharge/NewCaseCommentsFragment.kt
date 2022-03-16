package com.heandroid.ui.startNow.contactdartcharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.account.ServiceRequest
import com.heandroid.databinding.*
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.*
import java.util.ArrayList

class NewCaseCommentsFragment : BaseFragment<FragmentNewCaseCommentBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentNewCaseCommentBinding.inflate(inflater, container, false)

    override fun init() {
        requireActivity().customToolbar(getString(R.string.str_raise_new_enquiry))
    }

    override fun initCtrl() {
        binding.apply {
            btnNext.setOnClickListener(this@NewCaseCommentsFragment)
        }
    }


    override fun observer() { }

    override fun onClick(it: View?) {

        when (it?.id) {

            R.id.btnNext -> {
                findNavController().navigate(R.id.action_NewCaseCommentsFragment_to_NewCaseSummeryFragment)
            }
            else -> {
            }
        }


    }

}