package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Selection
import android.text.Spanned
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import java.util.regex.Pattern


class TopUpFragment : BaseFragment<FragmentTopUpBinding>(), View.OnClickListener {

    private var lowBalance: Boolean = false
    private var topUpBalance: Boolean = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopUpBinding = FragmentTopUpBinding.inflate(inflater, container, false)

    override fun init() {
//        binding.lowBalance.editText.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter()))
//        binding.top.editText.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter()))
    }

    override fun initCtrl() {
        binding.topUpBtn.setOnClickListener(this)
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.top.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.topUpBtn -> {
                val amount = binding.top.getText().toString().trim()
                val bundle = Bundle()
                bundle.putString(Constants.DATA,amount)
                findNavController().navigate(R.id.action_topUpFragment_to_paymentFragment,bundle)
            }
        }

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {

        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {

            if (index == 0) {

                val text = binding.lowBalance.getText().toString().trim()
                val updatedText = text.replace("£","")

                if (updatedText.isNotEmpty()) {
                    lowBalance = if (updatedText.length < 11) {
                        if (updatedText.toDouble() < 5) {
                            binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_more))
                            false

                        } else {
                            binding.lowBalance.removeError()
                            true
                        }
                    } else {
                        binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_8_characters))
                        false
                    }

                }else{
                    binding.lowBalance.removeError()
                }
                binding.lowBalance.editText.removeTextChangedListener(this)
                if(updatedText.isNotEmpty())
                    binding.lowBalance.setText("£" + String.format("%.2f", updatedText.toDouble()))
                Selection.setSelection( binding.lowBalance.getText(),binding.lowBalance.getText().toString().length)
                binding.lowBalance.editText.addTextChangedListener(this)
            } else if (index == 1) {
                val text = binding.top.getText().toString().trim()
                val updatedText = text.replace("£","")
                if (updatedText.isNotEmpty()) {
                    topUpBalance = if (updatedText.length < 11) {
                        if (updatedText.toDouble() < 10) {
                            binding.top.setErrorText(getString(R.string.str_top_up_amount_must_be_more))
                            false

                        } else {
                            binding.top.removeError()
                            true
                        }
                    } else {
                        binding.top.setErrorText(getString(R.string.str_top_up_amount_must_be_8_characters))
                        false
                    }
                }else{
                    binding.top.removeError()
                }
                binding.top.editText.removeTextChangedListener(this)
                if(updatedText.isNotEmpty())
                    binding.top.setText("£" + String.format("%.2f", updatedText.toDouble()))
                Selection.setSelection( binding.top.getText(),binding.top.getText().toString().length)
                binding.top.editText.addTextChangedListener(this)
            }

            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        binding.topUpBtn.isEnabled = lowBalance && topUpBalance
    }

    class DecimalDigitsInputFilter : InputFilter {
        var mPattern: Pattern

        init {
            mPattern = Pattern.compile("[0-9]*+((\\.[0-9]?)?)||(\\.)?")
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            val matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else ""
        }
    }

}