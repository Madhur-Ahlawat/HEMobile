package com.heandroid.ui.account.profile.pin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.heandroid.R
import com.heandroid.databinding.FragmentProfilePinBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfilePinFragment : BaseFragment<FragmentProfilePinBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentProfilePinBinding.inflate(inflater,container,false)
    override fun init() {
        binding.enable = false
    }
    override fun initCtrl() {
        binding.apply {
            btnSave.setOnClickListener(this@ProfilePinFragment)
            btnChangePin.setOnClickListener(this@ProfilePinFragment)
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
                else binding.tvPinThree.requestFocus()
                checkButton()
            }
        }
    }
    override fun observer() {}

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnSave -> {
                val digitPin = binding.tvPinOne.text.toString()+""+ binding.tvPinTwo.text.toString()+""+ binding.tvPinThree.text.toString()+""+ binding.tvPinFour.text.toString()
            }

            R.id.btnChangePin -> {
            }

        }
    }

    private fun checkButton() {
        binding.enable = binding.tvPinOne.text.toString().isNotEmpty() &&
                binding.tvPinTwo.text.toString().isNotEmpty() &&
                binding.tvPinThree.text.toString().isNotEmpty() &&
                binding.tvPinFour.text.toString().isNotEmpty()

    }

}