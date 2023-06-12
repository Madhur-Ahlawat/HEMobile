package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentMaximumVehicleNumberBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants


class MaximumVehicleNumberFragment : BaseFragment<FragmentMaximumVehicleNumberBinding>(),View.OnClickListener {


    private var plateNumber = ""
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMaximumVehicleNumberBinding =
        FragmentMaximumVehicleNumberBinding.inflate(inflater, container, false)

    override fun init() {
        plateNumber = arguments?.getString(Constants.PLATE_NUMBER).toString()
        if (NewCreateAccountRequestModel.isExempted){
            binding.textMaximumVehicle.text=getString(R.string.str_vehicle_exempt_detail_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_exempt_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.cancelBtn.visibility=View.GONE
            binding.btnContinue.text = getString(R.string.str_continue)
        }

        if (NewCreateAccountRequestModel.isRucEligible){
            binding.textMaximumVehicle.text=getString(R.string.str_no_ruc_desc)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_exempt_message,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.cancelBtn.visibility=View.VISIBLE
            binding.btnContinue.text = getString(R.string.str_add_to_account)
        }
        if (NewCreateAccountRequestModel.isVehicleAlreadyAdded){
            binding.textMaximumVehicle.text=getString(R.string.str_vehicle_already_exist_desc,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAdded.text=getString(R.string.str_vehicle_already_added_system,NewCreateAccountRequestModel.plateNumber)
            binding.maximumVehicleAddedNote.visibility=View.VISIBLE
            binding.cancelBtn.visibility=View.VISIBLE
            binding.btnContinue.text = getString(R.string.str_add_another)
        }


        if (NewCreateAccountRequestModel.isVehicleAlreadyAddedLocal){
            binding.maximumVehicleAdded.text=getString(R.string.vehicle_s_mha_has_already_been_assigned_to_this_account,plateNumber)
            binding.textMaximumVehicle.text=getString(R.string.you_have_already_added_this_vehicle_to_this_account)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.btnContinue.text = getString(R.string.add_another_vehicle)
            binding.textMaximumVehicle.gravity = Gravity.CENTER
        }

        if (NewCreateAccountRequestModel.isMaxVehicleAdded){
            binding.maximumVehicleAdded.text=getString(R.string.maximum_number_of_vehicles_have_been_registered_against_the_account)
            binding.textMaximumVehicle.text=getString(R.string.you_ve_reached_the_maximum_amount_of_vehicles_that_you_can_add_to_your_account_you_ll_need_to_remove_a_vehicle_before_adding_another)
            binding.maximumVehicleAddedNote.visibility=View.GONE
            binding.cancelBtn.visibility=View.GONE
            binding.btnContinue.text = getString(R.string.str_continue)
            binding.textMaximumVehicle.gravity = Gravity.CENTER
        }
    }

    override fun initCtrl() {

        binding.cancelBtn.setOnClickListener(this)
        binding.btnContinue.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnContinue -> {

                when (binding.btnContinue.text) {
                    getString(R.string.add_another_vehicle) -> findNavController().navigate(R.id.action_maximumFragment_to_findYourVehicleFragment)

                }
                when (binding.btnContinue.text) {
                    getString(R.string.str_continue) -> findNavController().navigate(R.id.action_maximumFragment_to_createAccountSummaryFragment)

                }


            }
            R.id.cancel_btn -> {

                findNavController().popBackStack()


            }
        }
    }

}