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
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils


class PaymentFragment : BaseFragment<FragmentPaymentBinding>(),View.OnClickListener {


    private var topUpAmount = ""
    private var topUpBalance : Boolean = false
    private var requiredName = false
    private var requiredCard = false
    private var requiredCode = false
    private var requiredDate = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentBinding = FragmentPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        topUpAmount = arguments?.getString(Constants.DATA).toString()
        topUpBalance = true
        binding.paymentAmount.setText(topUpAmount)
        if(NewCreateAccountRequestModel.prePay){
            binding.youChooseToPay.visibility = View.GONE
        }else{
            binding.paymentAmount.visibility = View.GONE
        }
    }

    override fun initCtrl() {
        binding.proceddWithPayment.setOnClickListener(this)
        binding.paymentAmount.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.expiryDate.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.nameOnCard.editText.addTextChangedListener(GenericTextWatcher(2))
        binding.cardSecurityCode.editText.addTextChangedListener(GenericTextWatcher(3))
        binding.cardNumber.editText.addTextChangedListener(GenericTextWatcher(4))
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
            when(index){
                0->{

                    val text = binding.paymentAmount.getText().toString().trim()
                    val updatedText = text.replace("£","")
                    if (updatedText.isNotEmpty()) {
                        topUpBalance = if (updatedText.length < 11) {
                            if (updatedText.toDouble() < 10) {
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
                    binding.paymentAmount.editText.removeTextChangedListener(this)
                    if(updatedText.isNotEmpty())
                        binding.paymentAmount.setText("£" + String.format("%.2f", updatedText.toDouble()))
                    Selection.setSelection( binding.paymentAmount.getText(),binding.paymentAmount.getText().toString().length)
                    binding.paymentAmount.editText.addTextChangedListener(this)

                }
                1->{
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
                    if(current.length<5){
                        binding.expiryDate.setErrorText(getString(R.string.invalid_date_format))
                        requiredDate = false
                    }else{
                        val expDate: String = binding.expiryDate.getText().toString()
                        val split = expDate.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (split.isNotEmpty()) {
                            val expiryMonth = split[0]
                            val expiryYear = split[1]
                            if(expiryMonth.toInt()>12 || expiryMonth == "00"){
                                binding.expiryDate.setErrorText(getString(R.string.invalid_date_format))
                                requiredDate = false
                            }else if(expiryYear.toInt()<23){
                                binding.expiryDate.setErrorText(getString(R.string.expiry_date_cannot_be_in_the_past))
                                requiredDate = false
                            }else{
                                binding.expiryDate.removeError()
                                requiredDate = true
                            }

                        }
                    }
                }

                2->{
                    if(binding.nameOnCard.getText()?.isEmpty() == true){
                        requiredName = false
                    }else{
                        if (binding.nameOnCard.getText().toString().trim().length < 50) {

                            if (binding.nameOnCard.getText().toString().trim()
                                    .contains(Utils.specialCharacter)
                            ) {
                                binding.nameOnCard.setErrorText(getString(R.string.the_name_on_card_must_only_include_letters_a_to_z_and_special_characters_such_as_hyphens))
                                requiredName = false

                            } else {
                                binding.nameOnCard.removeError()
                                requiredName = true
                            }
                        } else {
                            if (binding.nameOnCard.getText().toString().trim().length > 50) {
                                binding.nameOnCard.setErrorText(getString(R.string.str_card_name_length_error_message))
                                requiredName = false
                            } else {
                                binding.nameOnCard.removeError()
                                requiredName = true
                            }


                        }
                    }

                }

                3->{
                    val length = binding.cardSecurityCode.getText()?.length
                    if (length != null) {
                        if(length < 3){
                            binding.cardSecurityCode.setErrorText(getString(R.string.card_security_code_must_be_3_digits_or_more))
                            requiredCode = false
                        }else{
                            binding.cardSecurityCode.removeError()
                            requiredCode = true
                        }
                    }

                }

                4->{
                    val length = binding.cardNumber.getText()?.length
                    if (length != null) {
                        if(length < 16){
                            binding.cardNumber.setErrorText(getString(R.string.card_number_must_be_16_digits_or_more))
                        }else{
                            binding.cardNumber.removeError()
                            requiredCard = true
                        }
                    }

                }
            }

            checkButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {



        binding.proceddWithPayment.isEnabled = topUpBalance && requiredDate &&
                binding.nameOnCard.getText()?.isNotEmpty() == true && requiredName && requiredCard && requiredCode
    }

}