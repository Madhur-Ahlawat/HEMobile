package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentEnquiryCommentsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnquiryCommentsFragment : BaseFragment<FragmentEnquiryCommentsBinding>() {

    lateinit var viewModel: RaiseNewEnquiryViewModel
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryCommentsBinding =
        FragmentEnquiryCommentsBinding.inflate(inflater, container, false)

    override fun init() {

        binding.charactersRemTv.text = resources.getString(
            R.string.str_you_have_chars_remain,
            "500"
        )

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_enquiryCommentsFragment_to_enquiryContactDetailsFragment)
        }

        binding.commentsEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p0.toString().isEmpty()) {
                    binding.btnNext.isEnabled = false
                } else {
                    binding.btnNext.isEnabled = true
                }
                binding.charactersRemTv.text = resources.getString(
                    R.string.str_you_have_chars_remain,
                    "" + (500 - p0.toString().length)
                )
                viewModel.enquiryModel.value?.comments = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    override fun initCtrl() {

    }

    override fun observer() {
        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )

    }

}