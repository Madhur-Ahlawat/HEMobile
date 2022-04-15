package com.heandroid.ui.account.profile.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.EmptyApiResponse
import com.heandroid.data.model.profile.AccountPinChangeModel
import com.heandroid.databinding.FragmentProfilePinBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfilePinFragment : BaseFragment<FragmentProfilePinBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePinBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    override fun initCtrl() {
        binding.apply {
            btnSave.setOnClickListener(this@ProfilePinFragment)
            btnChangePin.setOnClickListener(this@ProfilePinFragment)
            tvPinOne.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinTwo.requestFocus()
                checkButton()
            }
            tvPinTwo.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinThree.requestFocus()
                else binding.tvPinOne.requestFocus()
                checkButton()
            }
            tvPinThree.doAfterTextChanged {
                if (it?.isNotEmpty() == true) binding.tvPinFour.requestFocus()
                else binding.tvPinTwo.requestFocus()

                checkButton()
            }
            tvPinFour.doAfterTextChanged {
                if (it?.isNotEmpty() == true) hideKeyboard()
                else binding.tvPinThree.requestFocus()
                checkButton()
            }
        }
    }

    override fun observer() {
        observe(viewModel.updateAccountPinApiVal, ::handlePinChange)
    }

    private fun handlePinChange(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                val bundle = Bundle().apply {
                    putBoolean(Constants.UPDATE_PIN_FLOW, true)
                    putParcelable(Constants.DATA, arguments?.getParcelable(Constants.DATA))
                }
                findNavController().navigate(R.id.action_pinFragment_to_updatePasswordSuccessfulFragment, bundle)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {

            }
        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnSave -> {
                val digitPin =
                    binding.tvPinOne.text.toString() + "" + binding.tvPinTwo.text.toString() + "" + binding.tvPinThree.text.toString() + "" + binding.tvPinFour.text.toString()
            }

            R.id.btnChangePin -> {
                changeAccountPin()
            }

        }
    }

    private fun changeAccountPin() {
        loader?.show(requireActivity().supportFragmentManager, "")
        val request = AccountPinChangeModel(
            binding.tvPinOne.text.toString() +
                    binding.tvPinTwo.text.toString() +
                    binding.tvPinThree.text +
                    binding.tvPinFour.text
        )
        viewModel.updateAccountPin(request)
    }

    private fun checkButton() {
        binding.enable = binding.tvPinOne.text.toString().isNotEmpty() &&
                binding.tvPinTwo.text.toString().isNotEmpty() &&
                binding.tvPinThree.text.toString().isNotEmpty() &&
                binding.tvPinFour.text.toString().isNotEmpty()
    }

}