package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentChangeVehicleSuccessConfirmCheckPaidCrossingsFragmentBinding
import com.conduent.nationalhighways.databinding.FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.databinding.FragmentVehicleDoesNotMatchBinding
import com.conduent.nationalhighways.databinding.FragmentVehicleIsExemptFromDartChargesBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class VehicleIsExemptFromDartChargesFragment :
    BaseFragment<FragmentVehicleIsExemptFromDartChargesBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private var totalAmountOfUnsettledTrips: Double? = 0.0
    private var crossingsList: MutableList<String>? = mutableListOf()
    private var data: CrossingDetailsModelsResponse? = null
    val bundle = Bundle()
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleIsExemptFromDartChargesBinding =
        FragmentVehicleIsExemptFromDartChargesBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        additionalCrossings = data?.additionalCrossingCount
        additionalCrossingsCharge = data?.additionalCharge
        setData()
        setClickListeners()
        /*  val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse(url)
          startActivity(i)*/


    }

    private fun setData() {
        binding?.apply {

            val charge = (navData as CrossingDetailsModelsResponse).chargingRate?.toDouble()
            val unSettledTrips = (navData as CrossingDetailsModelsResponse).unSettledTrips
            crossingsList = emptyList<String>().toMutableList()
            if (unSettledTrips != null && charge != null) {
                totalAmountOfUnsettledTrips = charge * unSettledTrips
            }

//            if(additionalCrossings != null && additionalCrossings != 0 && additionalCrossingsCharge != null){
//                totalAmountOfAdditionalCrossings = totalAmountOfAdditionalCrossings?.plus(additionalCrossings!! * additionalCrossingsCharge!!)
//
//            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            btnTransfer.setOnClickListener(this@VehicleIsExemptFromDartChargesFragment)
            btnCancel.setOnClickListener(this@VehicleIsExemptFromDartChargesFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnTransfer -> {
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, data)
                findNavController().navigate(
                    R.id.action_vehicleIsExemptFromDartChargesFragment_to_findYourVehicleFragment,
                    bundle
                )
            }

            R.id.btnCancel -> {
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, data)
                findNavController().popBackStack(R.id.businessVehicleDetailFragment, false)
            }
        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
        bundle.putString(
            PLATE_NUMBER,
            (navData as CrossingDetailsModelsResponse).plateNumber?.trim()
        )
        bundle.putParcelable(NAV_DATA_KEY, navData as Parcelable?)
        return bundle
    }


    override fun vehicleListCallBack(
        position: Int,
        value: String,
        plateNumber: String?,
        isDblaAvailable: Boolean?
    ) {
        enableEditMode()
        if (value == Constants.REMOVE_VEHICLE) {
            val bundle = Bundle()
            bundle.putInt(Constants.VEHICLE_INDEX, position)
            findNavController().navigate(R.id.action_accountSummaryFragment_to_removeVehicleFragment)
        } else {
            val bundle = Bundle()

            if (isDblaAvailable == true) {
                bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_createAccountFindVehicleFragment,
                    bundle
                )
            } else {
                bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                if (isDblaAvailable != null) {
                    bundle.putBoolean(Constants.IS_DBLA_AVAILABLE, isDblaAvailable)
                }
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_addNewVehicleDetailsFragment,
                    bundle
                )
            }
        }

    }

}