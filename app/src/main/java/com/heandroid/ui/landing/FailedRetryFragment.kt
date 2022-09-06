package com.heandroid.ui.landing

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.databinding.FragmentFailedRetryBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class FailedRetryFragment : BaseFragment<FragmentFailedRetryBinding>(), View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFailedRetryBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        if (requireActivity() is LandingActivity) {
            val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
            toolbar.findViewById<TextView>(R.id.btn_login).gone()
        }

    }

    override fun init() {
        binding.desc3.movementMethod = LinkMovementMethod.getInstance()
        makeLinksInLicenseAgreementDescription()
    }

    override fun initCtrl() {
        binding.apply {
            btnSignIn.setOnClickListener(this@FailedRetryFragment)
            btnClose.setOnClickListener(this@FailedRetryFragment)
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btnSignIn -> {
                    Intent(requireActivity(), AuthActivity::class.java).run {
                        startActivity(this)
                    }
                }

                R.id.btnClose -> {
                    requireActivity().finishAffinity()
                }
            }
        }
    }

    private fun makeLinksInLicenseAgreementDescription() {
        try {
            getString(R.string.failed_retry_desc3).lowercase(Locale.getDefault())
                .let { it ->
                    val phraseTermsService =
                        getString(R.string.failed_retry_num1).lowercase(Locale.getDefault())
                    val phrasePrivacyPolicy =
                        getString(R.string.failed_retry_num2).lowercase(Locale.getDefault())
                    val phraseTermsServiceStart = it.indexOf(phraseTermsService)
                    val phraseTermsServiceEnd = phraseTermsServiceStart + phraseTermsService.length
                    val phrasePrivacyPolicyStart = it.indexOf(phrasePrivacyPolicy)
                    val phrasePrivacyPolicyEnd =
                        phrasePrivacyPolicyStart + phrasePrivacyPolicy.length

                    SpannableString(getString(R.string.failed_retry_desc3)).let {
                        it.setSpan(
                            linkClickListener(getString(R.string.failed_retry_num1)),
                            phraseTermsServiceStart,
                            phraseTermsServiceEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        it.setSpan(
                            linkClickListener(getString(R.string.failed_retry_num2)),
                            phrasePrivacyPolicyStart,
                            phrasePrivacyPolicyEnd,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        binding.desc3.text = it
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun linkClickListener(linkToOpen: String): ClickableSpan {
        return object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = requireContext().getColor(R.color.white)
                ds.isUnderlineText = true
            }

            override fun onClick(view: View) {
                startActivity(Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$linkToOpen")
                })
            }
        }
    }

}