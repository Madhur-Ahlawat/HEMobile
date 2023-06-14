package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentPaymentBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class PaymentFragment : BaseFragment<FragmentPaymentBinding>(),View.OnClickListener {


    private var topUpAmount : Int? = 10
    private var topUpBalance: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentBinding = FragmentPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        topUpAmount = arguments?.getInt(Constants.DATA,10)
    }

    override fun initCtrl() {
        binding.proceddWithPayment.setOnClickListener(this)
        binding.paymentAmount.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.expiryDate.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.proceddWithPayment->{
                findNavController().navigate(R.id.action_paymentFragment_to_tryPaymentAgainFragment)

            }
        }

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {

        var current = ""
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
                val text = binding.paymentAmount.getText().toString().trim()
                val updatedText = text.replace("£","")
                if (updatedText.isNotEmpty()) {
                    topUpBalance = if (updatedText.length < 8) {
                        if (updatedText.toInt() < 10) {
                            binding.paymentAmount.setErrorText(getString(R.string.str_top_up_amount_must_be_more))
                            false

                        } else {
                            binding.paymentAmount.removeError()
                            true
                        }
                    } else {
                        binding.paymentAmount.setErrorText(getString(R.string.str_top_up_amount_must_be_8_characters))
                        false
                    }
                }else{
                    binding.paymentAmount.removeError()
                }
                binding.paymentAmount.editText.removeTextChangedListener(this);
                binding.paymentAmount.setText("£" + updatedText)
                Selection.setSelection( binding.paymentAmount.getText(),binding.paymentAmount.getText().toString().length)
                binding.paymentAmount.editText.addTextChangedListener(this)
            }else if(index == 1){
                if (charSequence.toString() != current) {
                    var clean: String = charSequence.toString().replace("[^\\d.]".toRegex(), "")
                    var formatted = ""
                    var length = clean.length
                    if (length > 2) {
                        formatted += clean.substring(0, 2) + "/"
                        clean = clean.substring(2)
                        length = clean.length
                    }
                    if (length > 0) {
                        formatted += clean.substring(0, Math.min(2, length))
                    }
                    current = formatted
                    binding.expiryDate.setText(formatted)
                    Selection.setSelection( binding.expiryDate.getText(),binding.expiryDate.getText().toString().length)
                }
            }

            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {

    }

}