package com.conduent.nationalhighways.ui.account.creation.step5.businessaccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.*
import com.conduent.nationalhighways.databinding.FragmentBusinessVehicleDetailChangesBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Logg
import com.conduent.nationalhighways.utils.common.Utils
import com.google.gson.Gson

class BusinessVehicleDetailFragment : BaseFragment<FragmentBusinessVehicleDetailChangesBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentBusinessVehicleDetailChangesBinding.inflate(inflater, container, false)

    private var requestModel: CreateAccountRequestModel? = null
    private var nonUKVehicleModel: NewVehicleInfoDetails? = null

    override fun init() {
        requestModel = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        nonUKVehicleModel = arguments?.getParcelable(Constants.VEHICLE_DETAIL)

        Log.d("vehicleData", Gson().toJson(nonUKVehicleModel))

        binding.apply {
            regNum.text = nonUKVehicleModel?.plateNumber
            typeOfVehicle.text = Utils.getVehicleType(nonUKVehicleModel?.vehicleClass.toString())
            vehicleModel.text = nonUKVehicleModel?.vehicleModel
            vehicleMake.text = nonUKVehicleModel?.vehicleMake
            vehicleColor.text = nonUKVehicleModel?.vehicleColor
        }


    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.notVehicle.setOnClickListener(this@BusinessVehicleDetailFragment)
        binding.inCorrectVehicleNumber.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.confirmBtn -> {

                val accountData = NewCreateAccountRequestModel
                val vehicleList = accountData.vehicleList

                nonUKVehicleModel?.let {

                    vehicleList.add(it)
                    findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_vehicleListFragment)


                }


            }

            R.id.notVehicle -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.VEHICLE_DETAIL, nonUKVehicleModel)
                findNavController().navigate(
                    R.id.action_businessVehicleDetailFragment_to_yourVehicleFragment,
                    bundle
                )
            }

            R.id.inCorrectVehicleNumber -> {
                findNavController().navigate(R.id.action_businessVehicleDetailFragment_to_findYourVehicleFragment)
            }
        }
    }
}