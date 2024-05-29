package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.ui.payment.MakeOffPaymentActivity
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_FROM
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.OLD_PLATE_NUMBER
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER

import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {
    private var additionalCrossingsCount: Int = 0
    private var crossingsList: MutableList<String>? = mutableListOf()
    private var data: CrossingDetailsModelsResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentSummaryBinding =
        FragmentPaymentSummaryBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }

        setData()
        setClickListeners()

        if (requireActivity() is CreateAccountActivity) {
            (requireActivity() as CreateAccountActivity).focusToolBarCreateAccount()
        } else if (requireActivity() is MakeOffPaymentActivity) {
            (requireActivity() as MakeOffPaymentActivity).focusMakeOffToolBar()
        }

    }


    private fun setData() {
        binding.apply {
            additionalCrossingsCount = data?.additionalCrossingCount?:0
            val charge = data?.chargingRate?.toDouble() ?:0.0
            val unSettledTrips = data?.unsettledTripChange
            vehicleRegisration.text = data?.plateNo
            vehicleRegisration.contentDescription =
                Utils.accessibilityForNumbers(data?.plateNo ?: "")
            recentCrossings.text = unSettledTrips.toString()
            recentCrossings.contentDescription =
                Utils.accessibilityForNumbers(unSettledTrips.toString())
            creditAdditionalCrossings.text = additionalCrossingsCount.toString()
            creditAdditionalCrossings.contentDescription =
                Utils.accessibilityForNumbers(additionalCrossingsCount.toString())
            if (additionalCrossingsCount == 0) {
                creditForAdditionalCrossings.gone()
            } else {
                creditForAdditionalCrossings.visible()
            }
            if (Utils.isStringOnlyInt(NewCreateAccountRequestModel.mobileNumber ?: "")) {
                labelMobileNumber.visible()
                mobileNumber.text = resources.getString(
                    R.string.concatenate_two_strings_with_space,
                    "" + data?.countryCode,
                    NewCreateAccountRequestModel.mobileNumber
                )
            } else {
                labelMobileNumber.gone()
            }

            mobileNumber.contentDescription =
                Utils.accessibilityForNumbers(mobileNumber.text.toString())
            if (!NewCreateAccountRequestModel.emailAddress.isNullOrEmpty()) {
                labelEmail.visible()
                email.text = NewCreateAccountRequestModel.emailAddress
            } else {
                labelEmail.gone()
            }

            if (navFlowFrom != PAY_FOR_CROSSINGS) {
                if (unSettledTrips == 0) {
                    recentCrossingsCv.gone()
                } else {
                    recentCrossingsCv.visible()
                }
            } else {
                recentCrossingsCv.visible()
            }
            var recentCrossingsAmount = 0.0
            var additionalCrossingsAmount = 0.0
            if (unSettledTrips != null && unSettledTrips > 0) {
                recentCrossingsAmount = charge * unSettledTrips
            }
            if (additionalCrossingsCount > 0) {
                additionalCrossingsAmount = charge * additionalCrossingsCount
            }
            val total = recentCrossingsAmount + additionalCrossingsAmount
            data?.totalAmount = total
            crossingsList = emptyList<String>().toMutableList()

            crossingsList?.clear()
            val totalAdditional =
                (additionalCrossingsCount.plus(unSettledTrips ?: 0))
            for (i in 0..totalAdditional) {
                crossingsList?.add(i.toString())
            }
            paymentAmount.text = getString(
                R.string.price, "" + String.format(
                    "%.2f",
                    total
                )
            )


        }
    }

    private fun setClickListeners() {
        binding.apply {
            btnNext.setOnClickListener(this@PaymentSummaryFragment)
            editRegistrationNumber.setOnClickListener(this@PaymentSummaryFragment)
            editRecentCrossings.setOnClickListener(this@PaymentSummaryFragment)
            editCreditForAdditionalCrossings.setOnClickListener(this@PaymentSummaryFragment)
            editEmail.setOnClickListener(this@PaymentSummaryFragment)
            editMobileNumber.setOnClickListener(this@PaymentSummaryFragment)
        }
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnNext -> {
                val bundle = Bundle()
                bundle.putDouble(
                    Constants.DATA,
                    data?.totalAmount ?: 0.0
                )
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

            R.id.editEmail -> {
                findNavController().popBackStack()
            }

            R.id.editMobileNumber -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
        bundle.putString(NAV_FLOW_FROM, navFlowFrom)
        bundle.putBoolean(EDIT_SUMMARY, true)
        bundle.putString(
            PLATE_NUMBER,
            data?.plateNo?.trim()
        )
        bundle.putString(
            OLD_PLATE_NUMBER,
            data?.plateNo?.trim()
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
                bundle.putString(PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_createAccountFindVehicleFragment,
                    bundle
                )
            } else {
                bundle.putString(OLD_PLATE_NUMBER, plateNumber)
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

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {

    }

    override fun onRetryClick(apiUrl: String) {
    }

}