package com.heandroid.ui.landing

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.heandroid.R
import com.heandroid.data.model.landing.LandingModel
import com.heandroid.databinding.FragmentLandingBinding
import com.heandroid.ui.checkpaidcrossings.CheckPaidCrossingActivity
import com.heandroid.ui.account.creation.controller.CreateAccountActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.payment.MakeOffPaymentActivity
import com.heandroid.ui.viewcharges.ViewChargesActivity
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.CHECK_FOR_PAID
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT
import com.heandroid.utils.common.Constants.ONE_OFF_PAYMENT
import com.heandroid.utils.common.Constants.RESOLVE_PENALTY
import com.heandroid.utils.common.Constants.VIEW_CHARGES
import com.heandroid.utils.common.VehicleHelper
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLandingBinding  = FragmentLandingBinding.inflate(inflater, container, false)

    override fun onResume() {
        super.onResume()
        if (requireActivity() is LandingActivity) {
            val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
            toolbar.findViewById<TextView>(R.id.btn_login).visible()
            requireActivity().setRightButtonText(getString(R.string.login))
        }
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
            R.id.rbCreateAccount -> {
                VehicleHelper.list?.clear()
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = CREATE_ACCOUNT

            }

            R.id.rbMakeOffPayment -> {
                VehicleHelper.list?.clear()
                val spannableString = SpannableString(getString(R.string.str_make_one_of_payment_continue))
                val boldSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(boldSpan, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.rbMakeOffPayment.text = spannableString
                binding.model?.selectType = ONE_OFF_PAYMENT
            }

            R.id.rbResolvePenalty -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = RESOLVE_PENALTY
            }

            R.id.rbCheckForPaid -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = CHECK_FOR_PAID
            }

            R.id.rbViewCharges -> {
                binding.rbMakeOffPayment.text = getString(R.string.str_make_one_of_payment)
                binding.model?.selectType = VIEW_CHARGES
            }
        }
        enableBtn()
    }

    private fun enableBtn() {
        binding.model = binding.model?.apply {
            enable = true
        }
    }


    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btnContinue -> {
                when (binding.model?.selectType) {
                   VIEW_CHARGES -> { requireActivity().startNewActivity(ViewChargesActivity::class.java) }
                   ONE_OFF_PAYMENT -> { requireActivity().startNewActivity(MakeOffPaymentActivity::class.java) }
                   CHECK_FOR_PAID -> { requireActivity().startNormalActivity(
                       CheckPaidCrossingActivity::class.java) }
                   RESOLVE_PENALTY -> { openUrlInWebBrowser() }
                   CREATE_ACCOUNT-> { requireActivity().startNormalActivity(CreateAccountActivity::class.java) }
                   else -> { requireActivity().startNormalActivity(InProgressActivity::class.java) }
              }
            }
        }}}

    private fun openUrlInWebBrowser() {
        val url = Constants.PCN_RESOLVE_URL
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            startActivity(Intent.createChooser(this, "Browse with"));
        }
    }
}