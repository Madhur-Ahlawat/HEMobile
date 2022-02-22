package com.heandroid.ui.auth.forgot.password

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.request.auth.forgot.password.ConfirmOptionModel
import com.heandroid.data.model.response.auth.forgot.password.SecurityCodeResponseModel
import com.heandroid.databinding.FragmentForgotChooseOptionBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.Common
import com.heandroid.utils.Constants
import com.heandroid.utils.ErrorUtil.showError
import com.heandroid.utils.SessionManager
import javax.inject.Inject

class ChooseOptionForgotFragment: BaseFragment<FragmentForgotChooseOptionBinding>(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @Inject
    lateinit var sessionManager : SessionManager
    private var type : String?=""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentForgotChooseOptionBinding = FragmentForgotChooseOptionBinding.inflate(inflater,container,false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.continueBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(group?.checkedRadioButtonId){
            R.id.email_radio_btn ->{ type= Constants.EMAIL }
            R.id.text_message_radio_btn ->{ type= Constants.MESSAGE }
            R.id.post_mail_radio_btn ->{ type=Constants.POST_MAIL }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.continue_btn -> {

                if (!TextUtils.isEmpty(type)) {
//                    val response = SecurityCodeResponseModel("345677", 30, "qweerty", true)
//                    sessionManager.saveCode(response.code?:"")
//                    sessionManager.saveSecurityCodeObject(response)
//                    updateView(arguments?.getParcelable(Constants.OPTIONS))
                    val bundle = Bundle()
                    bundle.putString(Constants.MODE, type)
                    when(type){
                        Constants.POST_MAIL ->{ findNavController().navigate(R.id.action_chooseOptionFragment_to_postalEmailFragment, bundle) }
                        else ->{ findNavController().navigate(R.id.action_chooseOptionFragment_to_otpFragment,bundle) }
                    }
                } else {
                    showError(binding.root,getString(R.string.please_select_one_mode_for_password))
                }
            }
        }
    }


    private fun updateView(data: ConfirmOptionModel?) {
        val maskMail = Common.maskString(data?.email, 2, 10, '*')
        val length = data?.phone?.length?:0
        val maskPhone = data?.phone?.substring(length - 5, length - 1)
        binding.emailRadioBtn.text = "Email - $maskMail"
        binding.textMessageRadioBtn.text = "Text message - (xxxx) xxxx -$maskPhone"
        binding.postMailRadioBtn.text = "Post mail - 3113********,Ap***NC,***02"
    }
}