package com.heandroid.ui.landing

import android.content.Intent
import android.graphics.Color
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
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.futureModule.InProgressActivity
import com.heandroid.ui.startNow.StartNowBaseActivity
import com.heandroid.ui.viewcharges.ActivityViewCharges
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.setRightButtonText
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.startNewActivity
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : BaseFragment<FragmentLandingBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private var screenType: String = ""
    private lateinit var model: LandingModel
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLandingBinding {
        return FragmentLandingBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        val toolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar_lyt)
        toolbar.findViewById<TextView>(R.id.btn_login).visible()

        requireActivity().setRightButtonText(getString(R.string.login))
    }

    override fun init() {
        binding.apply {
            btnContinue.setOnClickListener(this@LandingFragment)
        }
        model = LandingModel()
    }

    override fun initCtrl() {
        binding.radioGroup.setOnCheckedChangeListener(this)
    }

    override fun observer() {
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rb_create_account -> {
                binding.rbOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                model.selectType = Constants.CREATE_ACCOUNT
            }
            R.id.rb_one_of_payment -> {
                val spannableString = SpannableString(getString(R.string.str_make_one_of_payment_continue))
                val boldSpan = StyleSpan(Typeface.BOLD)
                spannableString.setSpan(boldSpan, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.rbOneOfPayment.text = spannableString
                model.selectType = Constants.ONE_OFF_PAYMENT
            }
            R.id.rb_resolve_penalty -> {
                binding.rbOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                model.selectType = Constants.RESOLVE_PENALTY
            }
            R.id.rb_check_for_paid -> {
                binding.rbOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                model.selectType = Constants.CHECK_FOR_PAID
            }
            R.id.rb_view_charges -> {
                binding.rbOneOfPayment.text = getString(R.string.str_make_one_of_payment)
                model.selectType = Constants.VIEW_CHARGES
            }
        }
        enableBtn()

    }

    private fun enableBtn() {
        binding.model = model.apply {
            enable = true
        }
    }


    override fun onClick(v: View?) {
        v?.let {
            when (v.id) {
                R.id.btn_continue -> {

                    when (model.selectType) {

                        Constants.VIEW_CHARGES -> {
                            requireActivity().startNewActivity(ActivityViewCharges::class.java)

                        }

                        Constants.CHECK_FOR_PAID -> {
                            Intent(requireActivity(), InProgressActivity::class.java).run {
                                startActivity(this)
                            }

                        }

                        Constants.RESOLVE_PENALTY -> {
                            openUrlInWebBrowser()
                        }

                         else -> {
                        Intent(requireActivity(), InProgressActivity::class.java).run {
                            startActivity(this)
                        }

                    }

                }
            }
        }}}

    private fun openUrlInWebBrowser() {

        var url = "http://www.google.com";
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).run {
            // Note the Chooser below. If no applications match,
            // Android displays a system message.So here there is no need for try-catch.
            startActivity(Intent.createChooser(this, "Browse with"));
        }

    }
}