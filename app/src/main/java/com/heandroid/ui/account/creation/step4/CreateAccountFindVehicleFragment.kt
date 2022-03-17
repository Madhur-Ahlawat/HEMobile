package com.heandroid.ui.account.creation.step4

import android.os.Bundle
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

    private var loader: LoaderDialog? = null
    private val viewModel: CreateAccountVechileViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountFindVehicleBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 4, 5)
    }

    override fun initCtrl() {
        binding.apply {
            addVrmInput.doAfterTextChanged{ binding.enable= (it?.length?:0)  >1 }
            findYourVehicle.setOnClickListener(this@CreateAccountFindVehicleFragment)
        }
    }

    override fun observer() {
        observe(viewModel.findVehicleLiveData, ::handleVehicleResponse)
    }

    override fun onClick(v: View?) {
    when (v?.id) {
        R.id.findYourVehicle -> {
            loader?.show(requireActivity().supportFragmentManager, "")
            viewModel.getVehicleData(binding.addVrmInput.text.toString(), Constants.AGENCY_ID)
        }
    }
    }

    private fun handleVehicleResponse(resource: Resource<VehicleInfoDetails?>?) {
        try {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.retrievePlateInfoDetails != null) {
                    val bundle =  Bundle()
                    bundle.putParcelable(Constants.FIND_VEHICLE_DATA,resource.data)
                    findNavController().navigate(R.id.action_findYourVehicleFragment_to_showVehicleDetailsFragment, bundle)
                }
            }
            is Resource.DataError -> { ErrorUtil.showError(binding.root, resource.errorMsg) }
        }
    }catch (e: Exception){}}
}