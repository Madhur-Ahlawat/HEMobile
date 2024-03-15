package com.conduent.nationalhighways.ui.landing

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentFailedRetryBinding
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity.Companion.showToolBar
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class FailedRetryFragment : BaseFragment<FragmentFailedRetryBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentFailedRetryBinding.inflate(inflater, container, false)

    override fun init() {
        backButton = false
        checkBackIcon()
        showToolBar(true)
//        binding.desc3.movementMethod = LinkMovementMethod.getInstance()
//        makeLinksInLicenseAgreementDescription()
        if (sessionManager.getLoggedInUser()) {
            binding.decs1Tv.text = resources.getString(R.string.try_again_later_signin_account)
            binding.btnNext.text = resources.getString(R.string.sign_in)
        } else {
            binding.decs1Tv.text = resources.getString(R.string.try_again_later)
            binding.btnNext.text = resources.getString(R.string.back_to_main_menu)
        }

        binding.btnNext.setOnClickListener {
            if (sessionManager.getLoggedInUser()) {
                requireActivity().startNewActivityByClearingStack(LoginActivity::class.java)
            } else {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
            }
        }

        binding.detailsCl.contentDescription =
            binding.decs1Tv.text.toString() + "\n" +
                    binding.decs2Tv.text.toString() + "\n" +
                    binding.decs3Tv.text.toString() + "\n" +
                    binding.decs4Tv.text.toString() + "\n" +
                    binding.decs5Tv.text.toString()

    }

    override fun initCtrl() {
    }

    override fun observer() {}

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
//                        binding.desc3.text = it
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun linkClickListener(linkToOpen: String): ClickableSpan {
        return object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = requireContext().getColor(R.color.high_lighted_color)
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