package com.heandroid.ui.payment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentMakeOffPaymentReceiptBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


@AndroidEntryPoint
class MakeOffPaymentReceiptFragment : BaseFragment<FragmentMakeOffPaymentReceiptBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOffPaymentReceiptBinding =
        FragmentMakeOffPaymentReceiptBinding.inflate(inflater, container, false)

    private var mScreeType = 0
    override fun init() {
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        val mSubList = ArrayList<String>()
        binding.mailSmsDropDown.setText("Email")

        mSubList.add("Email")
        mSubList.add("SMS")
        val mAdapter1 =
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_dropdown_item_1line,
                mSubList
            )
        binding.mailSmsDropDown.setAdapter(mAdapter1)

        binding.mailSmsDropDown.setOnItemClickListener { parent, view, position, id ->
            mOption = parent.getItemAtPosition(position) as String
            if (mOption == "SMS") {
                binding.tilEmail.hint = getString(R.string.str_mobile_number)
                binding.tilConfirmEmail.hint = getString(R.string.str_mobile_number)
                binding.tieEmail.inputType = InputType.TYPE_CLASS_PHONE
                binding.tieConfirmEmail.inputType = InputType.TYPE_CLASS_PHONE
                type =1
                mOption= "SMS"
            } else {
                type =0
                mOption="Email"
                binding.tilEmail.hint = getString(R.string.str_email)
                binding.tilConfirmEmail.hint = getString(R.string.str_email)
                binding.tieEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                binding.tieConfirmEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

            }
        }


    }

    private var mOption = "Email"
    private var type =0

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)
    }

    private val viewModel: MakeOneOfPaymentViewModel by viewModels()

    override fun observer() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                if (viewModel.validationEmail(
                        binding.tieEmail.text.toString(),
                        binding.tieConfirmEmail.text.toString(),
                    type).first
                ) {
                    val bundle = Bundle()
                    bundle.putString(Constants.EMAIL, binding.tieEmail.text.toString())
                    Logg.logging("testing", "  binding.tieEmail.text.toString()  ${binding.tieEmail.text.toString()}")

                    bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                    bundle.putString(Constants.OPTIONS_TYPE, mOption)
                    bundle.putParcelableArrayList(
                        Constants.DATA,
                        arguments?.getParcelableArrayList(Constants.DATA)
                    )
                    findNavController().navigate(R.id.action_makeOffPaymentReceiptFragment_to_makeOffPaymentCardFragment,bundle)
                } else {
                    requireActivity().showToast("Please check all the details")
                }
            }
        }
    }
}