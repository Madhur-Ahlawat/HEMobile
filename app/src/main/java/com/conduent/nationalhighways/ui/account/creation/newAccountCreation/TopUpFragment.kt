package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment


class TopUpFragment : BaseFragment<FragmentTopUpBinding>(), View.OnClickListener {

    private var lowBalance: Boolean = false
    private var topUpBalance: Boolean = false
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTopUpBinding = FragmentTopUpBinding.inflate(inflater, container, false)

    override fun init() {
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
                findNavController().navigate(R.id.action_topUpFragment_to_paymentFragment)
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

               /* if (binding.lowBalance.getText().toString().trim().isNotEmpty()) {
                    lowBalance = if (binding.lowBalance.getText().toString().trim().length < 8) {
                        if (binding.lowBalance.getText().toString().trim() > "£5") {
                            binding.lowBalance.removeError()
                            true

                        } else {
                            binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_more))
                            false

                        }
                    } else {
                        binding.lowBalance.setErrorText(getString(R.string.str_low_balance_must_be_8_characters))
                        false
                    }

                }else{
                    binding.lowBalance.removeError()
                }*/
            } else if (index == 1) {
                /* if (binding.top.getText().toString().trim().isNotEmpty()) {


                    topUpBalance = if (binding.top.getText().toString().trim().length < 8) {
                        if (binding.top.getText().toString().trim().toInt() < 10) {
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
            }
*/
                checkButton()
            }
        }

            override fun afterTextChanged(s: Editable?) {
                if (index == 0) {
                    if (s.toString().isNotEmpty()){
                        if (!s.toString().startsWith("£")) {
                            binding.lowBalance.setText("£");
                            Selection.setSelection(
                                binding.lowBalance.getText(),
                                binding.lowBalance.getText().toString().length
                            )

                        }
                    }

                }else{
                    if (s.toString().isNotEmpty()){
                        if (!s.toString().startsWith("£")) {
                            binding.top.setText("£");
                            Selection.setSelection(
                                binding.top.getText(),
                                binding.top.getText().toString().length
                            )

                        }
                    }
                }

            }
        }

        private fun checkButton() {
            binding.topUpBtn.isEnabled = lowBalance && topUpBalance
        }


    }
