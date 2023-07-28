package com.conduent.nationalhighways.ui.bottomnav.dashboard.topup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentManualTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManualTopUpFragment : BaseFragment<FragmentManualTopUpBinding>(), View.OnClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        checkButton()
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.tieAmount.onTextChanged {
            checkButton()
        }
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnNext -> {
                val bundle = Bundle()
                bundle.putString("amount", binding.tieAmount.getText().toString())
                findNavController().navigate(
                    R.id.action_manualTopUpFragment_to_manualTopUpCardFragment,
                    bundle
                )
            }
        }
    }

    private fun checkButton() {
        binding.model = binding.tieAmount.getText().toString().trim().isNotEmpty()
    }
}