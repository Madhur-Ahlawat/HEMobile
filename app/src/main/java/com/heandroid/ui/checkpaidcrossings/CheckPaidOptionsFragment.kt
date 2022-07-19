package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.databinding.FragmentUsedUnusedOptionsBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckPaidOptionsFragment : BaseFragment<FragmentUsedUnusedOptionsBinding>(),
    View.OnClickListener {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentUsedUnusedOptionsBinding.inflate(inflater, container, false)

    override fun init() {}

    override fun initCtrl() {
        binding.rlUnUsedCrossings.setOnClickListener(this)
        binding.rlUsedCrossings.setOnClickListener(this)
    }

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_un_used_crossings -> {
                findNavController().navigate(
                    R.id.action_checkChargesOption_to_unUsedCharges,
                    arguments
                )
            }
            R.id.rl_used_crossings -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CHECK_PAID_CHARGE_DATA_KEY, arguments)
                findNavController().navigate(
                    R.id.action_checkChargesOption_to_usedCharges,
                    bundle
                )
            }
        }
    }
}