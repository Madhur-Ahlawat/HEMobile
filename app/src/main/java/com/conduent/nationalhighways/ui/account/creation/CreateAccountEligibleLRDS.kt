package com.conduent.nationalhighways.ui.account.creation

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountEligibleLrdsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountEligibleLRDS : BaseFragment<FragmentCreateAccountEligibleLrdsBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountEligibleLrdsBinding.inflate(inflater, container, false)

    override fun init() {
        val semiBold: Typeface = Typeface.create(
            ResourcesCompat.getFont(requireContext(), com.conduent.nationalhighways.R.font.sfsemibold),
            Typeface.NORMAL
        )
        val clickableLink = object : ClickableSpan() {
            override fun onClick(view: View) {
                Toast.makeText(view.context, "Link Clicked!", Toast.LENGTH_SHORT).show()
            }
        }
        val string = SpannableString("Your post code has been recognised as a valid Local Resident Discount Scheme (LRDS) post code. \n\nTo apply for a LRDS plan, click on this link which will navigate to the account creation journey on the web.")
        string.setSpan(UnderlineSpan(), 137, 142, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//        string.setSpan(clickableLink,  0, 100, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        string.setSpan(StyleSpan(Typeface.BOLD), 137, 142, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.txtDescription.setText(string, TextView.BufferType.SPANNABLE)
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
        binding.continueWithoutApplying.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.continueWithoutApplying.id -> {
                val bundle = Bundle()
//                bundle.putParcelable("response", status.data)
                findNavController().navigate(
                    R.id.action_createAccountEligibleLRDS_to_createAccountTypes,
                    bundle
                )
            }
            binding.btnContinue.id->{
                val bundle = Bundle()
//                bundle.putParcelable("response", status.data)
                findNavController().navigate(
                    R.id.action_createAccountEligibleLRDS_to_discount_type_fragment,
                    bundle
                )
            }
        }
    }

    override fun onRetryClick() {

    }
}