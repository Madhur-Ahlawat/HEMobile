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
import com.conduent.nationalhighways.databinding.FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class ConfirmNewVehicleDetailsCheckPaidCrossingsFragment : BaseFragment<FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private var totalAmountOfUnsettledTrips: Double?=0.0
    private var crossingsList: MutableList<String>? = mutableListOf()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding =
        FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(arguments?.getParcelable(Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java)!=null){
                navData = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if(arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY)!=null){
                navData = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        additionalCrossings = (navData as CrossingDetailsModelsResponse)?.additionalCrossingCount
        additionalCrossingsCharge = (navData as CrossingDetailsModelsResponse)?.additionalCharge
        setData()
        setClickListeners()

    }

    private fun setData() {
        binding?.apply {
            vehicleRegisration.text = (navData as CrossingDetailsModelsResponse).plateNumber
            creditRemaining.text =
                (navData as CrossingDetailsModelsResponse).unusedTrip.toString()
            creditAdditionalCrossings.text =
                (navData as CrossingDetailsModelsResponse).expirationDate
//            val charge = (navData as CrossingDetailsModelsResponse).chargingRate?.toDouble()
//            val unSettledTrips = (navData as CrossingDetailsModelsResponse).unSettledTrips
//            crossingsList = emptyList<String>().toMutableList()
//            if(unSettledTrips != null && charge != null){
//                totalAmountOfUnsettledTrips = charge*unSettledTrips
//            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            btnContinue.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)
            btnCancel.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)
            editCreditRemaining.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)
            editCreditWillExpireOn.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)

        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnContinue -> {
                    val bundle = Bundle()
                    bundle.putDouble(Constants.DATA, (navData as CrossingDetailsModelsResponse).totalAmount?:0.0)
                    bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                    bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                        bundle
                    )
            }
            R.id.btnCancel -> {
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, (navData as CrossingDetailsModelsResponse).totalAmount?:0.0)
                bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                    bundle
                )
            }

            R.id.editCreditRemaining -> {
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_findYourVehicleFragment,
                    enableEditMode()
                )
            }

            R.id.editCreditWillExpireOn -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_PayForCrossingsFragment,
                    enableEditMode()
                )

            }

        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
        bundle.putString(PLATE_NUMBER, (navData as CrossingDetailsModelsResponse).plateNumber?.trim())
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