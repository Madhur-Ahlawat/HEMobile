package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.account.CreateAccountVehicleListModel
import com.conduent.nationalhighways.data.model.account.CreateAccountVehicleModel
import com.conduent.nationalhighways.data.model.account.NonUKVehicleModel
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleDetailBinding
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleDetailChangesBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.extn.visible

class BusinessVehicleDetailFragment : BaseFragment<FragmentBusinessVehicleDetailChangesBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleDetailChangesBinding.inflate(inflater, container, false)

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NonUKVehicleModel? = null

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        nonUKVehicleModel = arguments?.getParcelable(Constants.NON_UK_VEHICLE_DATA)

        binding.apply {
            regNum.text = requestModel?.vehicleNo
            //vehicleClass.text = nonUKVehicleModel?.vehicleClassDesc
            vehicleModel.text = nonUKVehicleModel?.vehicleModel
            vehicleMake.text = nonUKVehicleModel?.vehicleMake
            vehicleColor.text = nonUKVehicleModel?.vehicleColor
           // countryRegistration.text = requestModel?.plateCountryType
          //  groupName.text = nonUKVehicleModel?.vehicleGroup
        }

/*
        binding.groupNameLayout.visible()
        binding.groupNameDesc.visible()
*/

        if (requestModel?.accountType == Constants.BUSINESS_ACCOUNT) {
/*
            binding.groupTxtOptional.text = getString(R.string.group_name_optional)
            binding.groupName.text = nonUKVehicleModel?.vehicleGroup
            binding.groupNameDesc.text = getString(R.string.group_name_field)
*/
        } else {
           /* binding.groupTxtOptional.text = getString(R.string.free_txt_name_optional)
            binding.groupName.text = nonUKVehicleModel?.vehicleComments
            binding.groupNameDesc.text =
                getString(R.string.str_free_text_field_can_be_used_to_distingush)
*/        }
    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.notVehicle.setOnClickListener(this@BusinessVehicleDetailFragment)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.confirmBtn -> {

                val vehicleList: MutableList<CreateAccountVehicleModel?> = ArrayList()
                nonUKVehicleModel?.apply {
                    val accountVehicleModel = CreateAccountVehicleModel(
                        requestModel?.plateCountryType,
                        "STANDARD", vehicleColor, "",
                        vehicleMake, vehicleModel,
                        requestModel?.vehicleNo, "2022", "HE",
                        VehicleClassTypeConverter.toClassCode(vehicleClassDesc),
                        vehicleGroup
                    )

                    vehicleList.add(accountVehicleModel)
                    requestModel?.ftvehicleList = CreateAccountVehicleListModel(vehicleList)
                }

                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                findNavController().navigate(
                    R.id.action_businessVehicleDetailFragment_to_paymentSummaryScreen,
                    bundle
                )
            }

            R.id.notVehicle -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.CREATE_ACCOUNT_DATA, requestModel)
                bundle.putParcelable(Constants.NON_UK_VEHICLE_DATA, nonUKVehicleModel)
                bundle.putInt(Constants.FROM_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE,Constants.FROM_CREATE_ACCOUNT_DETAILS_FRAG_TO_CREATE_ACCOUNT_FIND_VEHICLE)
                Logg.logging("NotVehicle", "bundle requestModel data $requestModel")
                Logg.logging("NotVehicle", "bundle nonUKVehicleModel data $nonUKVehicleModel")
                findNavController().navigate(
                    R.id.action_businessVehicleDetailFragment_to_findYourVehicleFragment,
                    bundle
                )
            }
        }
    }
}