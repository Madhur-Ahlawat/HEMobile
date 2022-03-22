package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentCreateAccountPinBinding
import com.heandroid.databinding.FragmentCreateAccountPosswordBinding
import com.heandroid.databinding.FragmentCreateAccountPostcodeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccoutPinFragment : BaseFragment<FragmentCreateAccountPinBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPinBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = false
        model = arguments?.getParcelable(Constants.DATA)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 5)


        when(model?.planType){
            PAYG ->{  binding.tvLabel.text=getString(R.string.pay_as_you_go)  }
            else ->{ binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
        }

    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccoutPinFragment)
            tvPinOne.doAfterTextChanged {
                if(it?.isNotEmpty()==true) binding.tvPinTwo.requestFocus()
                checkButton() }
            tvPinTwo.doAfterTextChanged {
                if(it?.isNotEmpty()==true) binding.tvPinThree.requestFocus()
                else binding.tvPinOne.requestFocus()
                checkButton() }
            tvPinThree.doAfterTextChanged {
                if(it?.isNotEmpty()==true) binding.tvPinFour.requestFocus()
                else binding.tvPinTwo.requestFocus()

                checkButton() }
            tvPinFour.doAfterTextChanged {
                if(it?.isNotEmpty()==true) hideKeyboard()
                checkButton()
            }
        }
    }

    private fun checkButton() {
        binding.enable = binding.tvPinOne.text.toString().length>1  && binding.tvPinTwo.text.toString().length>1 && binding.tvPinThree.text.toString().length>1 && binding.tvPinFour.text.toString().length>1

    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                model?.digitPin = binding.tvPinOne.text.toString()+""+ binding.tvPinTwo.text.toString()+""+ binding.tvPinThree.text.toString()+""+ binding.tvPinFour.text.toString()

                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA, model)
                }
                when(model?.planType){
                    PAYG ->{   findNavController().navigate(R.id.action_createAccoutPinFragment_to_findYourVehicleFragment, bundle)  }
                    else ->{   findNavController().navigate(R.id.action_createAccoutPinFragment_to_createAccoutInfoFragment, bundle) }
                }

            }

        }
    }

}