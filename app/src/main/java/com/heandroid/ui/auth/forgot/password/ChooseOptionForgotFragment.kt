package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.request.auth.forgot.password.RequestOTPModel
import com.heandroid.data.model.response.auth.forgot.password.ConfirmOptionResponseModel
import com.heandroid.databinding.FragmentForgotChooseOptionBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.SessionManager
import javax.inject.Inject

class ChooseOptionForgotFragment: BaseFragment<FragmentForgotChooseOptionBinding>(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @Inject
    lateinit var sessionManager : SessionManager
    private var type : String?=""
    private var model : RequestOTPModel?=null
    private var data: ConfirmOptionResponseModel?=null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotChooseOptionBinding = FragmentForgotChooseOptionBinding.inflate(inflater,container,false)

    override fun init() {
        model= RequestOTPModel(optionType = "",optionValue = "")
        data=arguments?.getParcelable(Constants.OPTIONS)
    }

    override fun initCtrl() {
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.continueBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId){

            R.id.email_radio_btn ->{
                model?.optionType = Constants.EMAIL
                model?.optionValue=data?.email
            }
            R.id.text_message_radio_btn ->{
                model?.optionType = Constants.SMS
                model?.optionValue= data?.phone
            }
            R.id.post_mail_radio_btn ->{
                model?.optionType= Constants.POST_MAIL
                model?.optionValue= ""
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.continue_btn -> {
                if (!TextUtils.isEmpty(model?.optionType?:"")) {
                    val bundle =  Bundle()
                    bundle.putParcelable("data",model)
                    when(model?.optionType){
                        Constants.POST_MAIL ->{ findNavController().navigate(R.id.action_chooseOptionFragment_to_postalEmailFragment, bundle) }
                        else ->{ findNavController().navigate(R.id.action_chooseOptionFragment_to_otpFragment,bundle) }
                    }
                } else {
                    showError(binding.root,getString(R.string.please_select_one_mode_for_password))
                }
            }
        }
    }
}