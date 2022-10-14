package com.conduent.nationalhighways.ui.account.creation.step6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountSuccessfulBinding
import com.conduent.nationalhighways.ui.auth.controller.AuthActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.extn.startNormalActivity
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
