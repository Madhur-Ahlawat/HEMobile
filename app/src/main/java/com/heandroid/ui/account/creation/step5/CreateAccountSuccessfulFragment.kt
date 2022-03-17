package com.heandroid.ui.account.creation.step5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heandroid.R
import com.heandroid.databinding.FragmentCreateAccountSuccessfulBinding
import com.heandroid.ui.auth.controller.AuthActivity
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.extn.startNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CreateAccountSuccessfulFragment : BaseFragment<FragmentCreateAccountSuccessfulBinding>(),View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateAccountSuccessfulBinding = FragmentCreateAccountSuccessfulBinding.inflate(inflater,container,false)

    override fun init() {
        binding.model=arguments?.getParcelable("response")
        binding.tvDate.text= SimpleDateFormat("dd MMM yyyy",Locale.getDefault()).format(Calendar.getInstance().time)
    }
    override fun initCtrl() {
        binding.btnClose.setOnClickListener(this)
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnClose ->{
                requireActivity().finish()
                requireActivity().startNormalActivity(AuthActivity::class.java)
            }
        }
    }
}
