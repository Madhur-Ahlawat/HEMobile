package com.heandroid.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.heandroid.R
import com.heandroid.databinding.FragmentForgotPasswordSecondBinding
import com.heandroid.model.ConfirmationOptionsResponseModel
import com.heandroid.model.GetSecurityCodeRequestModel
import com.heandroid.model.GetSecurityCodeResponseModel
import com.heandroid.network.ApiHelperImpl
import com.heandroid.network.RetrofitInstance
import com.heandroid.repo.Status
import com.heandroid.utils.Common
import com.heandroid.utils.Constants
import com.heandroid.utils.Logg
import com.heandroid.utils.SessionManager
import com.heandroid.viewmodel.RecoveryUsernamePasswordViewModel
import com.heandroid.viewmodel.ViewModelFactory

class ForgotPasswordSecondFragment : BaseFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var binding: FragmentForgotPasswordSecondBinding
    private lateinit var viewModel: RecoveryUsernamePasswordViewModel

    private var data: ConfirmationOptionsResponseModel? = null
    private var selectedOpt: String = ""
    private var optionVal: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password_second, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        initCtrl()
    }

    private fun init() {
        data=arguments?.getParcelable(Constants.OPTIONS)

        val factory = ViewModelFactory(ApiHelperImpl(RetrofitInstance.apiService))
        viewModel = ViewModelProvider(this, factory)[RecoveryUsernamePasswordViewModel::class.java]

        binding.emailRadioBtn.text = "Email - ${data?.email}"
        binding.textMessageRadioBtn.text = "Text message - ${data?.phone}"
        binding.postMailRadioBtn.text = "Post mail - 3113********,Ap***NC,***02"

        setBtnNormal()
    }

    private fun initCtrl(){
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.continueBtn.setOnClickListener {
            if (!TextUtils.isEmpty(selectedOpt)) {
                val bundle = Bundle()
                bundle.putString("optionType", selectedOpt)
                bundle.putString("optionValue", optionVal)
                when(selectedOpt){
                    Constants.POST_MAIL -> { Navigation.findNavController(binding.root).navigate(R.id.action_forgotPasswordSecondFragment_to_forgotPasswordPostalFragment, bundle) }
                    else -> { Navigation.findNavController(binding.root).navigate(R.id.action_forgotPasswordSecondFragment_to_forgotPasswordThirdFragment, bundle) }
                }
            } else { Toast.makeText(requireActivity(), "Please select one mode for password recovery", Toast.LENGTH_SHORT).show() }
        }
    }



    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId){

            R.id.email_radio_btn -> {
                selectedOpt = Constants.EMAIL
                optionVal = data?.email?:""
                setBtnActivated()
            }

            R.id.text_message_radio_btn -> {
                selectedOpt = Constants.MESSAGE
                optionVal = data?.phone?:""
                setBtnActivated()
            }

            R.id.post_mail_radio_btn ->{
                selectedOpt = Constants.POST_MAIL
                optionVal = ""
                setBtnActivated()
            }

        }
    }

    private fun setBtnActivated() {
        binding.continueBtn.isEnabled = true
        binding.continueBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
    }

    private fun setBtnNormal() {
        binding.continueBtn.isEnabled = false
        binding.continueBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.color_7D7D7D))
    }
}