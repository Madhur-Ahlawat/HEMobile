package com.heandroid.ui.account.profile.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.profile.UpdateAccountPassword
import com.heandroid.data.model.profile.UpdatePasswordResponseModel
import com.heandroid.databinding.FragmentProfilePasswordUpdateBinding
import com.heandroid.ui.account.profile.ProfileViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class ProfilePasswordUpdateFragment : BaseFragment<FragmentProfilePasswordUpdateBinding>(), View.OnClickListener {
    private val viewModel : ProfileViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentProfilePasswordUpdateBinding.inflate(inflater,container,false)
    override fun init() {
        requireActivity().findViewById<AppCompatTextView>(R.id.tvYourDetailLabel).gone()

        binding.enable=false
        binding.data= UpdateAccountPassword(currentPassword = "", newPassword = "", confirmPassword = "")


        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }
    override fun initCtrl() {
        binding.apply {
            tieCurrentPassword.onTextChanged { checkButton() }
            tiePassword.onTextChanged { checkButton() }
            tieConfirmPassword.onTextChanged { checkButton() }
            btnChange.setOnClickListener(this@ProfilePasswordUpdateFragment)
        }
    }
    override fun observer() {
        observe(viewModel.updatePassword,::handleUpdatePasswordResponse)
    }

    private fun checkButton() {
        binding.enable = (binding.tiePassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()  && binding.tieConfirmPassword.text.toString().trim().isNotEmpty()
                && binding.tieConfirmPassword.text.toString()
            .trim() == binding.tiePassword.text.toString().trim())

    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id){
            R.id.btnChange ->{
                val validation=viewModel.checkPassword(binding.data)
                if(validation.first) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    viewModel.updatePassword(binding.data)
                }else {
                    showError(binding.root, validation.second)
                }
            }
        }
    }

    private fun handleUpdatePasswordResponse(status: Resource<UpdatePasswordResponseModel?>?){
        try {
            loader?.dismiss()
            when(status){
                is Resource.Success ->{
                    if(status.data?.statusCode?.equals("500")==true){
                        showError(binding.root, status.data.message)
                    }else{
                        val bundle= Bundle()
                        bundle.putParcelable(Constants.DATA,arguments?.getParcelable(Constants.DATA))
                        findNavController().navigate(R.id.action_updatePasswordFragment_to_updatePasswordSuccessfulFragment,bundle)
                    }
                }
                is Resource.DataError ->{
                    showError(binding.root, status.errorMsg)
                }
            }
        }catch (e: Exception){}
    }

}