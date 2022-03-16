package com.heandroid.ui.startNow.contactdartcharge

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.AccountTypeSelectionModel
import com.heandroid.databinding.FragmentAccountTypeSelectionBinding
import com.heandroid.databinding.FragmentDartChargeAccountTypeSelectionBinding
import com.heandroid.databinding.FragmentProvideDetailsDartChargeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.*
import com.heandroid.utils.onTextChanged

class ProvideDetailsDartChargeFragment : BaseFragment<FragmentProvideDetailsDartChargeBinding>(),
    View.OnClickListener {
    private lateinit var accountModel: AccountTypeSelectionModel


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProvideDetailsDartChargeBinding.inflate(inflater, container, false)


    override fun init() {
        requireActivity().customToolbar(getString(R.string.cases_and_enquiry))
        accountModel = AccountTypeSelectionModel(false)
    }

    override fun initCtrl() {
        binding.apply {
            model = accountModel
            btnContinue.setOnClickListener(this@ProvideDetailsDartChargeFragment)
            etFistName.onTextChanged {
                checkButton()
            }
            etLastName.onTextChanged {
                checkButton()
            }
        }
    }

    private fun checkButton(){
        binding.apply {
            model = AccountTypeSelectionModel( binding.etFistName.text.toString().trim().isNotEmpty() &&
                    binding.etLastName.text.toString().trim().isNotEmpty())
        }
    }

    override fun observer() { }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.btnContinue -> {
                    findNavController().navigate(R.id.action_provideDetailsDartChargeFragment_to_caseDetailsDartChargeFragment)
                }
                else -> { }
            }
        }
    }

}