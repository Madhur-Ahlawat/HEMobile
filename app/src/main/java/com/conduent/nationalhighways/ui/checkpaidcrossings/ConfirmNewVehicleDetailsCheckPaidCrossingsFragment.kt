package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferRequest
import com.conduent.nationalhighways.data.model.checkpaidcrossings.BalanceTransferResponse
import com.conduent.nationalhighways.data.model.checkpaidcrossings.TransferInfo
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.Utils.convertDateForTransferCrossingsScreen
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConfirmNewVehicleDetailsCheckPaidCrossingsFragment : BaseFragment<FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var isClicked: Boolean = false
    private var loader: LoaderDialog? = null
    private var additionalCrossings: Int? = 0
    private var additionalCrossingsCharge: Double? = 0.0
    private val checkPaidCrossingViewModel: CheckPaidCrossingViewModel by viewModels()
    private var data: CrossingDetailsModelsResponse? = null
    @Inject
    lateinit var sm:SessionManager
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding =
        FragmentConfirmNewVehicleDetailsCheckPaidCrossingsFragmentBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        additionalCrossings = data?.additionalCrossingCount
        additionalCrossingsCharge = data?.additionalCharge
        setData()
        setClickListeners()
        initLoader()
    }

    private fun initLoader() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        Log.d("date",data?.expirationDate.toString())
        binding.apply {
            vehicleRegisration.text = data?.plateNo?.uppercase()
            vehicleRegisration.contentDescription = Utils.accessibilityForNumbers(data?.plateNo?.uppercase()?:"")

            if (data?.unusedTrip?.toInt()==1){
                creditRemaining.text =
                    data?.unusedTrip?.trim()+" "+getString(R.string.crossing)

            }else{
                creditRemaining.text =
                    data?.unusedTrip?.trim()+" "+getString(R.string.crossings)
            }

            creditAdditionalCrossings.text = convertDateForTransferCrossingsScreen(data?.expirationDate)
        }
    }


    private fun setClickListeners() {
        binding.apply {
            btnContinue.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)
            btnCancel.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)
            editVehicleRegistrationNumber.setOnClickListener(this@ConfirmNewVehicleDetailsCheckPaidCrossingsFragment)

        }
    }


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
                    val bundle=Bundle()
                    bundle.putString(NAV_FLOW_KEY,navFlowCall)
                    bundle.putParcelable(NAV_DATA_KEY,data)
                    findNavController().clearBackStack(R.id.landingFragment)
                    findNavController().navigate(
                        R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_ChangeVehicleConfirmSuccessCheckPaidCrossingsFragment,
                        bundle
                    )
                }
                is Resource.DataError -> {
                    if (checkSessionExpiredOrServerError(status.errorModel)) {
                        displaySessionExpireDialog(status.errorModel)
                    } else {
                        ErrorUtil.showError(binding.root, status.errorModel?.message)
                    }
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
                bundle.putDouble(Constants.DATA, data?.totalAmount?:0.0)
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, data)
                if(data?.vehicleClass?.lowercase().equals("a",true)){
                    findNavController().navigate(
                        R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_vehicleIsExemptFromDartChargesFragment,
                        bundle
                    )
                }
                else if(data?.vehicleClass?.equals(data?.vehicleClassBalanceTransfer) == false){
                    findNavController().navigate(
                        R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_vehicleDoesNotMatchCurrentVehicleFragment,
                        bundle
                    )
                }
                else{
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    isClicked = true
                    checkPaidCrossingViewModel.balanceTransfer(BalanceTransferRequest(accountNumber = data?.accountNo,
                        plateCountry = data?.plateCountryToTransfer,
                        plateNumber = data?.plateNumberToTransfer,
                        transferInfo = TransferInfo(tripCount = data?.unusedTrip, plateNumber = data?.plateNo?.uppercase()
                            ,plateState = "HE", plateCountry = data?.plateCountry, vehicleClass = data?.vehicleClassBalanceTransfer
                            , vehicleMake = data?.vehicleMake, vehicleModel = data?.vehicleModel, vehicleYear = "2023")))
                }

            }
            R.id.btnCancel -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, data)
                findNavController().popBackStack(R.id.addNewVehicleDetailsFragment,false)
//                findNavController().navigate(
//                    R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_addNewVehicleDetailsFragment,
//                    bundle
//                )
            }

            R.id.editVehicleRegistrationNumber->{
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, navFlowCall)
                bundle.putParcelable(NAV_DATA_KEY, data)
                bundle.putString(PLATE_NUMBER,data?.plateNo?:"")
                findNavController().navigate(R.id.action_confirmNewVehicleDetailsCheckPaidCrossingsFragment_to_findYourVehicleFragment,bundle)
            }

        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, navFlowCall)
        bundle.putString(PLATE_NUMBER, data?.plateNo?.uppercase()?.trim())
        bundle.putParcelable(NAV_DATA_KEY, data)
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