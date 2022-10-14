package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.databinding.FragmentCreateAccountTopUpBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountTopUpFragment : BaseFragment<FragmentCreateAccountTopUpBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountTopUpBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)
        model?.thresholdAmount="10.00"
        binding.enable=false
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountTopUpFragment )
            rbOptions.setOnCheckedChangeListener(this@CreateAccountTopUpFragment)
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.CREATE_ACCOUNT_DATA,model)
                }
                findNavController().navigate(R.id.action_createAccoutInfoConfirmationFragment_to_findYourVehicleFragment, bundle)

            }

        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        binding.enable=true
        when(group?.checkedRadioButtonId){
            R.id.mrbYes ->{
                binding.clNoDes.gone()
                binding.clYesDes.visible()
                model?.replenishmentAmount="05.00"
            }
            R.id.mrbNo ->{
                binding.clNoDes.visible()
                binding.clYesDes.gone()
                model?.replenishmentAmount="10.00"
            }
        }
    }

}