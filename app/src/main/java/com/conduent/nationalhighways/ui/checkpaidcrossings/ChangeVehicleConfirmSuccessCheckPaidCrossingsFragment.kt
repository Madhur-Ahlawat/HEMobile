package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.method.LinkMovementMethod
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
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible

class ChangeVehicleConfirmSuccessCheckPaidCrossingsFragment : BaseFragment<FragmentChangeVehicleSuccessConfirmCheckPaidCrossingsFragmentBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private var totalAmountOfUnsettledTrips: Double?=0.0
    private var crossingsList: MutableList<String>? = mutableListOf()
    private var data: CrossingDetailsModelsResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChangeVehicleSuccessConfirmCheckPaidCrossingsFragmentBinding =
        FragmentChangeVehicleSuccessConfirmCheckPaidCrossingsFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnFeedback.movementMethod = LinkMovementMethod.getInstance()

        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        additionalCrossings = data?.additionalCrossingCount
        additionalCrossingsCharge = data?.additionalCharge
        setData()
        setContentDescriptionForBullets()
        setClickListeners()
        /*  val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse(url)
          startActivity(i)*/
        HomeActivityMain.dataBinding?.backButton?.gone()

    }

    private fun setData() {
        binding?.apply {
            vehicleRegisration.text = data?.plateNo?.toUpperCase()
            creditRemaining.text =
                data?.unusedTrip.toString()
            creditWillExpireOn.text =
                Utils.convertDateForTransferCrossingsScreen(data?.expirationDate.toString())
        }
    }

    private fun setContentDescriptionForBullets() {
        binding.layoutVehicleRegistrationCv.contentDescription=binding.txtVehicleRegistration.text.toString()+"\n"+binding.vehicleRegisration.text.toString()
        binding.creditRemainingCv.contentDescription=binding.txtCreditRemaining.text.toString()+"\n"+binding.creditRemaining.text.toString()
        binding.emailCard.contentDescription=binding.txtCreditWillExpireOn.text.toString()+"\n"+binding.creditWillExpireOn.text.toString()

    }
    private fun setClickListeners() {
        binding?.apply {
            btnOk.setOnClickListener(this@ChangeVehicleConfirmSuccessCheckPaidCrossingsFragment)
            btnFeedback.setOnClickListener(this@ChangeVehicleConfirmSuccessCheckPaidCrossingsFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnOk -> {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
            }
            R.id.btnFeedback -> {
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, data?.totalAmount?:0.0)
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                    bundle
                )
            }
        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, navFlowCall)
        bundle.putString(PLATE_NUMBER, data?.plateNo?.trim())
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