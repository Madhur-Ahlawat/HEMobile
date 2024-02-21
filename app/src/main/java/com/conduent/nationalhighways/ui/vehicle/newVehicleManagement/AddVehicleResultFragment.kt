package com.conduent.nationalhighways.ui.vehicle.newVehicleManagement

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.VehicleSuccessFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleResultFragment : BaseFragment<VehicleSuccessFragmentBinding>(),
    View.OnClickListener {

    private lateinit var vehicleList: ArrayList<NewVehicleInfoDetails>

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VehicleSuccessFragmentBinding =
        VehicleSuccessFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        Utils.validationsToShowRatingDialog(requireActivity(), sessionManager)
        binding.recyclerViewSuccess.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFaied.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun initCtrl() {
        binding.btnContinue.setOnClickListener(this)

    }

    override fun observer() {
    }

    override fun onResume() {
        super.onResume()
        invalidateList()
    }


    private fun invalidateList() {
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()
        val accountData = NewCreateAccountRequestModel
        vehicleList = accountData.vehicleList as ArrayList<NewVehicleInfoDetails>
        val tempSuccessList: ArrayList<NewVehicleInfoDetails> = arrayListOf()
        val tempFailedList: ArrayList<NewVehicleInfoDetails> = arrayListOf()
        for (obj in vehicleList) {
            if (obj.status == true) {
                tempSuccessList.add(obj)
            } else {
                tempFailedList.add(obj)
            }
        }
        if (tempSuccessList.isEmpty()) {
            binding.vehicleAddedNote.visibility = View.GONE
            binding.recyclerViewSuccess.visibility = View.GONE
            binding.maximumVehicleAdded.visibility = View.GONE
            binding.warningIcon.visibility = View.GONE
        } else {
            val vehicleAdapterSuccess = VehiclesResultAdapter(requireContext(), tempSuccessList)
            binding.recyclerViewSuccess.adapter = vehicleAdapterSuccess
            if (tempSuccessList.size > 1) {
                binding.vehicleAddedNote.text = getString(R.string.vehicles_added)
                binding.maximumVehicleAdded.text =
                    getString(R.string.vehicles_added_to_your_account)
            } else {
                binding.vehicleAddedNote.text = getString(R.string.vehicle_added)

            }
        }
        if (tempFailedList.isEmpty()) {
            binding.vehicleNotAdded.visibility = View.GONE
            binding.recyclerViewFaied.visibility = View.GONE
            binding.warningIcon2.visibility = View.GONE
        } else {
            val vehicleAdapterFailed = VehiclesResultAdapter(requireContext(), tempFailedList)
            binding.recyclerViewFaied.adapter = vehicleAdapterFailed
            if (tempFailedList.size > 1) {
                binding.vehicleNotAdded.text = getString(R.string.vehicles_not_added)
            } else {
                binding.vehicleNotAdded.text = getString(R.string.vehicle_not_added)
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {
                NewCreateAccountRequestModel.vehicleList.clear()
                findNavController().navigate(R.id.action_vehicleResultFragment_to_vehicleHistoryListFragment)
            }
        }
    }
}