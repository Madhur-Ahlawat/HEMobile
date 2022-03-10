package com.heandroid.ui.landing

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.data.model.auth.forgot.email.LoginModel
import com.heandroid.data.model.landing.LandingModel
import com.heandroid.databinding.FragmentLandingBinding
import com.heandroid.ui.account.creation.CreateAccountActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.vehicle.payment.MakeOffPaymentActivity
import com.heandroid.ui.vehicle.payment.MakeOneOffPaymentFragment
import com.heandroid.ui.viewcharges.ViewChargesActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.CHECK_FOR_PAID
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT
import com.heandroid.utils.common.Constants.ONE_OFF_PAYMENT
import com.heandroid.utils.common.Constants.RESOLVE_PENALTY
import com.heandroid.utils.common.Constants.VIEW_CHARGES
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLandingBinding {
        return FragmentLandingBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
        toolbar.findViewById<TextView>(R.id.btn_login).visible()

        requireActivity().setRightButtonText(getString(R.string.login))
    }

    override fun init() {
      binding.model= LandingModel(enable = false)
    }

    override fun initCtrl() {
        binding.rgOptions.setOnCheckedChangeListener(this)
        binding.btnContinue.setOnClickListener(this@LandingFragment)
    }

    override fun observer() { }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rb_create_account -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = Constants.CREATE_ACCOUNT
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
            }
            R.id.rbMakeOffPayment -> {
                val spannableString = SpannableString(getString(R.string.str_make_one_of_payment_continue))
                val boldSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(boldSpan, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(RelativeSizeSpan(0.95f), 24,spannableString.length, 0); // set size

                spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(),R.color.color_757575)), 24, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.rbMakeOffPayment.text = spannableString
                binding.model?.selectType = Constants.ONE_OFF_PAYMENT
            }
            R.id.rbResolvePenalty -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = Constants.RESOLVE_PENALTY
            }
            R.id.rbCheckForPaid -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = Constants.CHECK_FOR_PAID
            }
            R.id.rbViewCharges -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = Constants.VIEW_CHARGES
            }
        }
        binding.model= LandingModel(true,binding.model?.selectType?:"")
    }

    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btnContinue -> {
                when (binding.model?.selectType) {
                   VIEW_CHARGES -> { requireActivity().startNewActivity(ViewChargesActivity::class.java) }
                   ONE_OFF_PAYMENT -> { requireActivity().startNewActivity(MakeOffPaymentActivity::class.java) }
                   CHECK_FOR_PAID -> { requireActivity().startNormalActivity(InProgressActivity::class.java) }
                   RESOLVE_PENALTY -> { openUrlInWebBrowser() }
                   CREATE_ACCOUNT-> { requireActivity().startNormalActivity(CreateAccountActivity::class.java) }
                   else -> { requireActivity().startNormalActivity(InProgressActivity::class.java) }
              }
            }
        }}}

    private fun openUrlInWebBrowser() {
        val url = "http://www.google.com";
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"));
        }
    }
}