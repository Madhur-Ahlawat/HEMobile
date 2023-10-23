package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentVehicleDoesNotMatchBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.common.Utils

class VehicleDoesNotMatchCurrentVehicleFragment :
    BaseFragment<FragmentVehicleDoesNotMatchBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private var totalAmountOfUnsettledTrips: Double? = 0.0
    private var crossingsList: MutableList<String>? = mutableListOf()
    var crossingDetailModel: CrossingDetailsModelsResponse? = null
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVehicleDoesNotMatchBinding =
        FragmentVehicleDoesNotMatchBinding.inflate(inflater, container, false)

    override fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arguments?.getParcelable(
                    NAV_DATA_KEY,
                    CrossingDetailsModelsResponse::class.java
                ) != null
            ) {
                navData = arguments?.getParcelable(
                    NAV_DATA_KEY, CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if (arguments?.getParcelable<CrossingDetailsModelsResponse>(NAV_DATA_KEY) != null) {
                navData = arguments?.getParcelable(
                    NAV_DATA_KEY,
                )
            }
        }
        crossingDetailModel = (navData as CrossingDetailsModelsResponse)
        additionalCrossings = crossingDetailModel?.additionalCrossingCount
        additionalCrossingsCharge = crossingDetailModel?.additionalCharge

        val correctVehicleType = Utils.getVehicleType(
            requireActivity(),
            crossingDetailModel?.customerClass ?: ""
        )
        val selectedVehicleType = Utils.getVehicleType(
            requireActivity(),
            crossingDetailModel?.dvlaclass ?: ""
        )
        val chargingRate = crossingDetailModel?.chargingRate

        if (navFlowCall == PAY_FOR_CROSSINGS) {
            binding.descTv.text = resources.getString(
                R.string.our_records_show_the_numberplate, crossingDetailModel?.plateNo,
                selectedVehicleType, correctVehicleType,
               (String.format("%.2f", chargingRate?.toDouble()))
            )
            binding.btnOk.text = resources.getString(R.string.pay_new_amount)
        } else {
            val chargingRate = "£" + crossingDetailModel?.chargingRate
            binding.descTv.text = resources.getString(R.string.our_records_show_the_numberplate,
                crossingDetailModel?.plateNo, crossingDetailModel?.dvlaclass?.let { Utils.getVehicleType(
                    requireActivity(),
                    it
                ) },
                crossingDetailModel?.customerClass?.let { Utils.getVehicleType(requireActivity(), it) },
                crossingDetailModel?.customerClassRate)

            binding.btnOk.text = resources.getString(R.string.str_buy_crossings_for_vehicle)

        }
        setData()
        setClickListeners()

    }

    private fun setData() {
        binding.apply {
            val charge = crossingDetailModel?.chargingRate?.toDouble()
            val unSettledTrips = crossingDetailModel?.unSettledTrips
            crossingsList = emptyList<String>().toMutableList()
            if (unSettledTrips != null && charge != null) {
                totalAmountOfUnsettledTrips = charge * unSettledTrips
            }
        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnOk.setOnClickListener(this@VehicleDoesNotMatchCurrentVehicleFragment)
            btnFeedback.setOnClickListener(this@VehicleDoesNotMatchCurrentVehicleFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, navFlowCall)
        when (v?.id) {

            R.id.btnOk -> {
                if (navFlowCall == PAY_FOR_CROSSINGS) {
                    bundle.putParcelable(NAV_DATA_KEY, returnModel(true))

                    if (crossingDetailModel?.unSettledTrips!! > 0) {
                        findNavController().navigate(
                            R.id.action_vehicleDoesNotMatchCurrentVehicleFragment_to_payCrossingsFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_vehicleDoesNotMatchCurrentVehicleFragment_to_additionalCrossingsFragment,
                            bundle
                        )
                    }
                } else {
                    bundle.putDouble(
                        Constants.DATA,
                        (navData as CrossingDetailsModelsResponse).totalAmount
                    )
                    bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                    bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                        bundle
                    )
                }
            }

            R.id.btnFeedback -> {

                if (navFlowCall == PAY_FOR_CROSSINGS) {

                    bundle.putParcelable(NAV_DATA_KEY, returnModel(false))

                    if (crossingDetailModel?.unSettledTrips!! > 0) {
                        findNavController().navigate(
                            R.id.action_vehicleDoesNotMatchCurrentVehicleFragment_to_payCrossingsFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.action_vehicleDoesNotMatchCurrentVehicleFragment_to_additionalCrossingsFragment,
                            bundle
                        )
                    }
                } else {
                    bundle.putDouble(
                        Constants.DATA,
                        (navData as CrossingDetailsModelsResponse).totalAmount
                    )
                    bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                    bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun returnModel(status: Boolean): CrossingDetailsModelsResponse {
        var chargingRate = crossingDetailModel?.chargingRate
        if (!status) {
            chargingRate = crossingDetailModel?.customerClassRate
        }
        return CrossingDetailsModelsResponse(
            accountNo = crossingDetailModel?.accountNo ?: "",
            chargingRate = chargingRate,
            customerClass = crossingDetailModel?.customerClass,
            customerClassRate = crossingDetailModel?.customerClassRate,
            dvlaclass = crossingDetailModel?.dvlaclass,
            plateCountry = crossingDetailModel?.plateCountry,
            plateNo = crossingDetailModel?.plateNo ?: "",
            unPaidAmt = crossingDetailModel?.unPaidAmt,
            unSettledTrips = crossingDetailModel?.unSettledTrips ?: 0,
            vehicleMake = crossingDetailModel?.vehicleMake,
            vehicleModel = crossingDetailModel?.vehicleModel,
        )


    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
        bundle.putString(PLATE_NUMBER, (navData as CrossingDetailsModelsResponse).plateNo.trim())
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
                bundle.putString(PLATE_NUMBER, plateNumber)
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