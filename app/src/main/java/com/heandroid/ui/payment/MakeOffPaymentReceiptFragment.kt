package com.heandroid.ui.payment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentReceiptBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.setSpinnerAdapterData
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MakeOffPaymentReceiptFragment : BaseFragment<FragmentMakeOffPaymentReceiptBinding>(),
    View.OnClickListener {

    private var mScreeType = 0
    private var mOption = Constants.EMAIL_ADDRESS

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentMakeOffPaymentReceiptBinding.inflate(inflater, container, false)

    override fun init() {
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }
        checkButton()
        clearError()

        val mSubList = ArrayList<String>()
        mSubList.add(Constants.EMAIL_ADDRESS)
        mSubList.add(Constants.PHONE_NUMBER)

        binding.mailSmsDropDown.setSpinnerAdapterData(mSubList)

        binding.mailSmsDropDown.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mOption = parent.getItemAtPosition(position) as String
                    if (mOption == Constants.PHONE_NUMBER) {
                        clearFields()
                        binding.tilEmail.hint = getString(R.string.str_mobile_number)
                        binding.tilConfirmEmail.hint = getString(R.string.str_confirm_phone)
                        binding.tieEmail.inputType = InputType.TYPE_CLASS_PHONE
                        binding.tieConfirmEmail.inputType = InputType.TYPE_CLASS_PHONE
                        mOption = Constants.PHONE_NUMBER
                    } else {
                        clearFields()
                        mOption = Constants.EMAIL_ADDRESS
                        binding.tilEmail.hint = getString(R.string.str_email)
                        binding.tilConfirmEmail.hint = getString(R.string.str_confirm_email)
                        binding.tieEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        binding.tieConfirmEmail.inputType =
                            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
        binding.tieEmail.onTextChanged {
            clearError()
            checkButton()
        }
        binding.tieConfirmEmail.onTextChanged {
            clearError()
            checkButton()
        }
        binding.appCompatCheckBox.setOnCheckedChangeListener { _, _ ->
            checkButton()
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                if (mOption == Constants.EMAIL_ADDRESS && binding.tieEmail.text.toString()
                        .trim() != binding.tieConfirmEmail.text.toString().trim()
                ) {
                    binding.tilConfirmEmail.error =
                        "The confirmed email address entered do not match"
                } else if (mOption == Constants.PHONE_NUMBER && binding.tieEmail.text.toString()
                        .trim() != binding.tieConfirmEmail.text.toString().trim()
                ) {
                    binding.tilConfirmEmail.error = "The confirmed mobile entered do not match"
                } else {
                    navigate()
                }
            }
        }
    }

    private fun checkButton() {
        if (mOption == Constants.EMAIL_ADDRESS) {
            val first = binding.tieEmail.text.toString().trim()
            val second = binding.tieConfirmEmail.text.toString().trim()
            binding.model = Utils.isEmailValid(first) && Utils.isEmailValid(second)
                    && binding.appCompatCheckBox.isChecked
        } else {
            binding.model = (binding.tieEmail.text.toString().trim().length >= 10
                    && binding.tieConfirmEmail.text.toString().trim().length >= 10
                    && binding.appCompatCheckBox.isChecked)
        }
    }

    private fun navigate() {
        val bundle = Bundle()
        bundle.putString(Constants.EMAIL, binding.tieEmail.text.toString())
        bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
        if (mOption == Constants.EMAIL_ADDRESS) {
            bundle.putString(Constants.OPTIONS_TYPE, "Email")
        } else {
            bundle.putString(Constants.OPTIONS_TYPE, "SMS")
        }
        bundle.putParcelableArrayList(
            Constants.DATA,
            arguments?.getParcelableArrayList(Constants.DATA)
        )
        findNavController().navigate(
            R.id.action_makeOffPaymentReceiptFragment_to_makeOffPaymentCardFragment,
            bundle
        )
    }

    private fun clearFields() {
        binding.tieEmail.setText("")
        binding.tieConfirmEmail.setText("")
        clearError()
    }

    private fun clearError() {
        binding.tieEmail.error = null
        binding.tieConfirmEmail.error = null
        binding.tilEmail.error = null
        binding.tilConfirmEmail.error = null
    }
}