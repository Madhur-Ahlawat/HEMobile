package com.heandroid.ui.account.creation.step4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.databinding.FragmentCreateAccountFindVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class CreateAccountFindVehicleFragment : BaseFragment<FragmentCreateAccountFindVehicleBinding>(), View.OnClickListener {

    private var isEnable: Boolean? = false
    private var isAccountVehicle = false
  //  private val viewModel: CreateAccountVechileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 4, 5)
    }

    override fun initCtrl() {
        binding.apply {
            addVrmInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    enableField()
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 4, 5)
            continueBtn.setOnClickListener(this@CreateAccountFindVehicleFragment)
        }
    }


    private fun enableField() {
        binding.apply {
            isEnable = addVrmInput.length() > 1

            if(isEnable == true){
                continueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bottomActiveColor))
                continueBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                continueBtn.isClickable = true

            } else {
                isEnable = false
                continueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.btn_disable))
                continueBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.txt_disable))
                continueBtn.isClickable = false

            }
        }
    }

    override fun observer() {
      //  observe(viewModel.findVehicleLiveData, ::handleVehicleResponse)
    }

    override fun onClick(v: View?) {
    when (v?.id) {
        R.id.continue_btn -> {
            isAccountVehicle = true
            val bundle = Bundle()
            bundle.putParcelable(Constants.DATA,arguments?.getParcelable(Constants.DATA))
            bundle.putBoolean("IsAccountVehicle", isAccountVehicle)
            bundle.putString("VehicleNo", binding.addVrmInput.text.toString())
            findNavController().navigate(R.id.action_findYourVehicleFragment_to_makePaymentAddVehicleFragment2, bundle)

        }
    }
    }


}