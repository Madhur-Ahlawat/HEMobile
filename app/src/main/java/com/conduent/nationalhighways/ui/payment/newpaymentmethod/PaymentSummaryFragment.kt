package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY

class PaymentSummaryFragment : BaseFragment<FragmentPaymentSummaryBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {

    private lateinit var vehicleAdapter: VehicleListAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPaymentSummaryBinding =
        FragmentPaymentSummaryBinding.inflate(inflater, container, false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun init() {
        navData = arguments?.getParcelable(
            Constants.NAV_DATA_KEY,
            CrossingDetailsModelsResponse::class.java
        )
        setData()
        setClickListeners()
        /*  val i = Intent(Intent.ACTION_VIEW)
          i.data = Uri.parse(url)
          startActivity(i)*/


    }

    private fun setData() {
        binding?.apply {
            vehicleRegisration.text = (navData as CrossingDetailsModelsResponse).plateNumber
            recentCrossings.text = (navData as CrossingDetailsModelsResponse).crossingCount.toString()
            creditAdditionalCrossings.text = (navData as CrossingDetailsModelsResponse).additionalCrossingCount.toString()
            paymentAmount.text = (navData as CrossingDetailsModelsResponse).totalAmount.toString()
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            btnNext.setOnClickListener(this@PaymentSummaryFragment)
            editFullName.setOnClickListener(this@PaymentSummaryFragment)
            editAddress.setOnClickListener(this@PaymentSummaryFragment)
            editEmailAddress.setOnClickListener(this@PaymentSummaryFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnNext -> {
                if (!NewCreateAccountRequestModel.prePay) {
                    val bundle = Bundle()

                    bundle.putDouble(Constants.DATA, 0.00)
                    bundle.putDouble(Constants.THRESHOLD_AMOUNT, 0.00)
                    bundle.putString(NAV_FLOW_KEY, Constants.NOTSUSPENDED)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, 0)
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_nmiPaymentFragment,
                        bundle
                    )

                } else {
                    findNavController().navigate(R.id.action_accountSummaryFragment_to_TopUpFragment)

                }
            }

            R.id.editFullName -> {
                findNavController().navigate(
                    R.id.action_crossingCheckAnswersFragment_to_personalInfoFragment,
                    enableEditMode()
                )
            }

            R.id.editAddress -> {
                if (NewCreateAccountRequestModel.isManualAddress) {
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_manualAddressFragment,
                        enableEditMode()
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_postCodeFragment,
                        enableEditMode()
                    )
                }
            }

            R.id.editEmailAddress -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_emailAddressFragment,
                    enableEditMode()
                )
            }

            R.id.editMobileNumber -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_mobileNumberFragment,
                    enableEditMode()
                )
            }

            R.id.editAccountType -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, EDIT_SUMMARY)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_typeAccountFragment,
                    bundle
                )
            }

            R.id.editSubAccountType -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_createAccountTypesFragment,
                    enableEditMode()
                )
            }

            R.id.editCommunications -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_communicationFragment,
                    enableEditMode()
                )
            }

            R.id.editTwoStepVerification -> {

                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_twoStepCommunicationFragment,
                    enableEditMode()
                )
            }
        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, EDIT_SUMMARY)
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