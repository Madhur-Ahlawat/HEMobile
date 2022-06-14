package com.heandroid.ui.account.creation.step4.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountVehicleListModel
import com.heandroid.data.model.account.CreateAccountVehicleModel
import com.heandroid.data.model.account.NonUKVehicleModel
import com.heandroid.databinding.FragmentBusinessVehicleDetailBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.Constants
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible

class BusinessVehicleDetailFragment: BaseFragment<FragmentBusinessVehicleDetailBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleDetailBinding.inflate(inflater, container, false)

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        nonUKVehicleModel = arguments?.getParcelable(Constants.NON_UK_VEHICLE_DATA)

        binding.apply {
            regNum.text = requestModel?.vehicleNo
            vehicleClass.text = nonUKVehicleModel?.vehicleClassDesc
            vehicleModel.text = nonUKVehicleModel?.vehicleModel
            vehicleMake.text = nonUKVehicleModel?.vehicleMake
            vehicleColor.text = nonUKVehicleModel?.vehicleColor
            countryRegistration.text = requestModel?.countryType
            groupName.text = nonUKVehicleModel?.vehicleGroup
        }

        if (requestModel?.accountType == Constants.BUSINESS_ACCOUNT) {
            binding.groupNameLayout.visible()
            binding.groupNameDesc.visible()
        } else {
            binding.groupNameLayout.gone()
            binding.groupNameDesc.gone()
        }
    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.notVehicle.setOnClickListener(this@BusinessVehicleDetailFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.confirmBtn -> {

                val vehicleList: MutableList<CreateAccountVehicleModel?> = ArrayList()
                nonUKVehicleModel?.apply {
                    val accountVehicleModel = CreateAccountVehicleModel(
                        requestModel?.countryType,
                        "STANDARD", vehicleColor, "",
                        vehicleMake, vehicleModel,
                        requestModel?.vehicleNo, "2022", "HE",
                        VehicleClassTypeConverter.toClassCode(vehicleClassDesc),
                        vehicleGroup)

                    vehicleList.add(accountVehicleModel)
                    requestModel?.ftvehicleList = CreateAccountVehicleListModel(vehicleList)
                }

                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_paymentSummaryScreen, bundle)
            }

            R.id.notVehicle -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_findYourVehicleFragment, bundle)
            }
        }
    }
}