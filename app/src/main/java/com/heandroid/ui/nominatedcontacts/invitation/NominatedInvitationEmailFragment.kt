package com.heandroid.ui.nominatedcontacts.invitation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentNominatedInvitationEmailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.ErrorUtil.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedInvitationEmailFragment : BaseFragment<FragmentNominatedInvitationEmailBinding>(), View.OnClickListener {

    private val viewModel : NominatedInvitationViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentNominatedInvitationEmailBinding = FragmentNominatedInvitationEmailBinding.inflate(inflater,container,false)

    override fun init() {
        binding.model= arguments?.getParcelable("data")
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext ->{
                val validation=viewModel.validationEmail(binding.model)
                if(validation.first){
                    val bundle= Bundle()
                    bundle.putBoolean("edit",arguments?.getBoolean("edit")?:false)
                    bundle.putParcelable("data",binding.model)
                    findNavController().navigate(R.id.action_ncEmailFragment_to_ncAcceessRightFragment,bundle)
                }
                else { showError(binding.root, validation.second) }
            }
        }
    }
}