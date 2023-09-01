package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferResponse
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.TransferInfo
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.payment.MakeOneOfPaymentViewModel
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils.convertDateForTransferCrossingsScreen
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class ConfirmNewVehicleDetailsCheckPaidCrossingsFragment : BaseFragment<FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var isClicked: Boolean = false
    private var loader: LoaderDialog? = null
    private var mData: CrossingDetailsModelsResponse?=null
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private val checkPaidCrossingViewModel: CheckPaidCrossingViewModel by viewModels()

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
        mData=navData as CrossingDetailsModelsResponse
        additionalCrossings = mData?.additionalCrossingCount
        additionalCrossingsCharge = mData?.additionalCharge
        setData()
        setClickListeners()
        initLoader()
    }

    private fun initLoader() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun setData() {
        binding?.apply {
            vehicleRegisration.text = mData?.plateNumber
            creditRemaining.text =
                mData?.unusedTrip
            creditAdditionalCrossings.text = convertDateForTransferCrossingsScreen(mData?.expirationDate)

//            val charge = mData?.chargingRate?.toDouble()
//            val unSettledTrips = mData?.unSettledTrips
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
        lifecycleScope.launch {
            observe(checkPaidCrossingViewModel.balanceTransfer, ::balanceTransfer)
        }
    }
    private fun balanceTransfer(status: Resource<BalanceTransferResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isClicked) {
            when (status) {
                is Resource.Success -> {
                    var bundle=Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                    bundle.putParcelable(Constants.NAV_DATA_KEY,navData as CrossingDetailsModelsResponse)
                    findNavController().clearBackStack(R.id.landingFragment)
                    findNavController().navigate(
                        R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_ChangeVehicleConfirmSuccessCheckPaidCrossingsFragment,
                        bundle
                    )
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }
            isClicked = false
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnContinue -> {
                    val bundle = Bundle()
                    bundle.putDouble(Constants.DATA, mData?.totalAmount?:0.0)
                    bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
                    bundle.putParcelable(NAV_DATA_KEY, navData as CrossingDetailsModelsResponse)
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                isClicked = true
                checkPaidCrossingViewModel.balanceTransfer(BalanceTransferRequest(accountNumber = mData?.accountNumber, plateCountry = mData?.plateCountry, plateNumber = mData?.plateNumber, transferInfo = TransferInfo(mData?.unusedTrip, plateNumber = mData?.plateNumber,plateState = "HE", plateCountry = mData?.plateCountry, vehicleClass = mData?.vehicleClass, vehicleMake = mData?.vehicleMake, vehicleModel = mData?.vehicleModel, vehicleYear = mData?.vehicleYear)))
            }
            R.id.btnCancel -> {
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, mData?.totalAmount?:0.0)
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
        bundle.putString(PLATE_NUMBER, mData?.plateNumber?.trim())
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