package com.heandroid.ui.bottomnav.dashboard.topup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.heandroid.R
import com.heandroid.databinding.FragmentManualTopUpBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.extn.gone
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualTopUpFragment : BaseFragment<FragmentManualTopUpBinding>(), View.OnClickListener {

    private val viewModel : ManualTopUpViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentManualTopUpBinding.inflate(inflater,container,false)


    override fun init() {

    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext -> {
                val validation = viewModel.validation(binding.tieAmount.text.toString())
                if(validation.first) {
                    val bundle= Bundle()
                    bundle.putString("amount",binding.tieAmount.text.toString())
                    findNavController().navigate(R.id.action_manualTopUpFragment_to_manualTopUpCardFragment,bundle)
                }
                else {
                    ErrorUtil.showError(binding.root, validation.second)
                }
            }
        }

}


}