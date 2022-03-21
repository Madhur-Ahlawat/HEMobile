package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentCreateAccountInfoBinding
import com.heandroid.databinding.FragmentCreateAccountInfoConfirmationBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountInfoConfirmationFragment : BaseFragment<FragmentCreateAccountInfoConfirmationBinding>(), View.OnClickListener {

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountInfoConfirmationBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(Constants.DATA)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 5)
    }

    override fun initCtrl() {
        binding.apply {
            btnAction.setOnClickListener(this@CreateAccountInfoConfirmationFragment)
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA,arguments?.getParcelable(Constants.DATA))
                    putInt(Constants.PERSONAL_TYPE, arguments?.getInt(Constants.PERSONAL_TYPE)?:0)

                }
                findNavController().navigate(R.id.action_createAccoutInfoConfirmationFragment_to_findYourVehicleFragment, bundle)

            }

        }
    }

}