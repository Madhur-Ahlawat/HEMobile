package com.conduent.nationalhighways.ui.payment.newpaymentmethod

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

class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {

    private var totalAmount: Double?=0.00
    private lateinit var vehicleAdapter: VehicleListAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentSummaryBinding =
        FragmentPaymentSummaryBinding.inflate(inflater, container, false)

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
        setData()
        setClickListeners()
        /*  val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse(url)
          startActivity(i)*/


    }

    private fun setData() {
        binding?.apply {
            vehicleRegisration.text = (navData as CrossingDetailsModelsResponse).plateNumber
            recentCrossings.text =
                (navData as CrossingDetailsModelsResponse).unSettledTrips.toString()
            creditAdditionalCrossings.text =
                (navData as CrossingDetailsModelsResponse).additionalCrossingCount.toString()
            val charge = (navData as CrossingDetailsModelsResponse)?.chargingRate?.toDouble()
            val unSettledTrips = (navData as CrossingDetailsModelsResponse)?.unSettledTrips
            val additionalCrossings = (navData as CrossingDetailsModelsResponse)?.additionalCrossingCount
            val additionalCrossingsCharge = (navData as CrossingDetailsModelsResponse)?.additionalCharge
            if(unSettledTrips != null && unSettledTrips != 0 && charge != null){
                val index = emptyList<String>().toMutableList()
                for (i in 0..unSettledTrips!!){
                    index.add(i.toString())
                }
                totalAmount = charge*unSettledTrips
            }
            if(additionalCrossings != null && additionalCrossings != 0 && additionalCrossingsCharge != null){

                totalAmount = totalAmount?.plus(additionalCrossings!! * additionalCrossingsCharge!!)
            }

            paymentAmount.text =  getString(R.string.currency_symbol)+ String.format("%.2f", totalAmount)
            if((navData as CrossingDetailsModelsResponse).unSettledTrips>0){
                binding.cardRecentCrossings.visible()
            }
            else{
                binding.cardRecentCrossings.gone()
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            btnNext.setOnClickListener(this@PaymentSummaryFragment)
            editRegistrationNumber.setOnClickListener(this@PaymentSummaryFragment)
            editRecentCrossings.setOnClickListener(this@PaymentSummaryFragment)
            editCreditForAdditionalCrossings.setOnClickListener(this@PaymentSummaryFragment)
            editPaymentAmount.setOnClickListener(this@PaymentSummaryFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnNext -> {
                    val bundle = Bundle()
                    bundle.putDouble(Constants.DATA, (navData as CrossingDetailsModelsResponse).totalAmount)
                    bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                    bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                        bundle
                    )
            }

            R.id.editRegistrationNumber -> {
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_findYourVehicleFragment,
                    enableEditMode()
                )
            }

            R.id.editRecentCrossings -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_PayForCrossingsFragment,
                    enableEditMode()
                )

            }

            R.id.editCreditForAdditionalCrossings -> {
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_additionalCrossingsFragment,
                    enableEditMode()
                )
            }

            R.id.editPaymentAmount -> {
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