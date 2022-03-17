package com.heandroid.ui.account.creation.findyourvehicle

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountVehicleModel
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.databinding.FragmentFindYourVehicleBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.toolbar
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response

@AndroidEntryPoint
class FindYourVehicleFragment : BaseFragment<FragmentFindYourVehicleBinding>(),
    View.OnClickListener {

    private val viewModel: FindYourVehicleFragmentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isEnable: Boolean? = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFindYourVehicleBinding {
        return FragmentFindYourVehicleBinding.inflate(inflater, container, false)
    }

    override fun init() {
        requireActivity().toolbar(getString(R.string.str_create_an_account))
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
            findYourVehicle.setOnClickListener(this@FindYourVehicleFragment)
        }
    }

    private fun enableField() {
        binding.apply {
            isEnable = addVrmInput.length() > 1

            if(isEnable == true){
                findYourVehicle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bottomActiveColor))
                findYourVehicle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                findYourVehicle.isClickable = true

            } else {
                isEnable = false
                findYourVehicle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.btn_disable))
                findYourVehicle.setTextColor(ContextCompat.getColor(requireContext(), R.color.txt_disable))
                findYourVehicle.isClickable = false

            }
        }
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        v.let {
            when (v?.id) {
                R.id.findYourVehicle -> {
                    if (isEnable == true) {
                        callLoader()
                        viewModel.getVehicleData(
                            binding.addVrmInput.text.toString(),
                            Constants.AGENCY_ID
                        )
                        observe(viewModel.findVehicleLiveData, ::handleVehicleResponse)
                    }
                }
            }
        }
    }

    private fun callLoader() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager, "")
    }

    private fun handleVehicleResponse(resource: Resource<VehicleInfoDetails?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.retrievePlateInfoDetails != null) {
                    val bundle =  Bundle()
                    bundle.putParcelable(Constants.FIND_VEHICLE_DATA,resource.data)

                    findNavController().navigate(
                        R.id.action_findYourVehicleFragment_to_showVehicleDetailsFragment,
                        bundle
                    )
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
                // do nothing
            }
        }

    }
}