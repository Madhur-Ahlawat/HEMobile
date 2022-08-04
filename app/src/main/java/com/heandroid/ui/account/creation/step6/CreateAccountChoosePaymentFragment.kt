package com.heandroid.ui.account.creation.step6

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountVehicleListModel
import com.heandroid.data.model.account.CreateAccountVehicleModel
import com.heandroid.databinding.FragmentCreateAccountChoosePaymentBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.CREATE_ACCOUNT_DATA
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.Logg
import com.heandroid.utils.common.VehicleHelper
import com.heandroid.utils.extn.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountChoosePaymentFragment :
    BaseFragment<FragmentCreateAccountChoosePaymentBinding>(), View.OnClickListener,
    RadioGroup.OnCheckedChangeListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountChoosePaymentBinding.inflate(inflater, container, false)

    override fun init() {
        Logg.logging(
            "testing",
            " CreateAccountChoosePaymentFragment init  called"
        )

        binding.tvStep.text = getString(R.string.str_step_f_of_l, 6, 6)
        binding.enable = false
        binding.btnContine.setOnClickListener(this)
        binding.rgPaymentOptions.setOnCheckedChangeListener(this)

    }

    override fun initCtrl() {}

    override fun observer() {}

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContine -> {
                Logg.logging(
                    "testing",
                    " CreateAccountChoosePaymentFragment btnContinue click called"
                )

                if (binding.rbDebitCard.isChecked) {
                    val bundle = Bundle()
                    Logg.logging(
                        "testing",
                        " CreateAccountChoosePaymentFragment btnContinue click inside 1 called"
                    )

                    bundle.putParcelable(
                        CREATE_ACCOUNT_DATA,
                        arguments?.getParcelable(CREATE_ACCOUNT_DATA)
                    )
                    Logg.logging(
                        "testing",
                        " CreateAccountChoosePaymentFragment btnContinue click inside 2 called arguments?.getParcelable(CREATE_ACCOUNT_DATA) ${
                            arguments?.getParcelable<CreateAccountRequestModel>(CREATE_ACCOUNT_DATA) as
                                    CreateAccountRequestModel
                        }"
                    )

                    findNavController().navigate(
                        R.id.action_choosePaymentFragment_to_cardFragment,
                        bundle
                    )
                } else {
                    requireActivity().showToast(getString(R.string.please_select_option))
                }
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (group?.checkedRadioButtonId) {
            R.id.rbDebitCard -> {
                binding.enable = true
            }
        }
    }
}