package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.LoginWithPlateAndReferenceNumberResponseModel
import com.conduent.nationalhighways.data.model.checkpaidcrossings.CheckPaidCrossingsRequest
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPaidPreviousCrossingsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.AdobeAnalytics
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingsFragment : BaseFragment<FragmentPaidPreviousCrossingsBinding>(),
    View.OnClickListener {

    private var mBuilder: StringBuilder? = null
    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    private var isCalled = false

    @Inject
    lateinit var sessionManager: SessionManager
    var paymentRefereceNumberRegex = "^[a-zA-Z0-9-]+$"
    var plateNumberREgex = "[0-9a-zA-Z -]{1,10}$"
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPaidPreviousCrossingsBinding.inflate(inflater, container, false)

    override fun init() {

        AdobeAnalytics.setScreenTrack(
            "check crossings:login",
            "login",
            "english",
            "check crossings",
            "home",
            "check crossings:login",
            sessionManager.getLoggedInUser()
        )

//        binding.model = CheckPaidCrossingsOptionsModel(ref = "", vrm = "", enable = false)
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
//        binding.editReferenceNumber.setText("1-99352459")
//        binding.editNumberPlate.setText("ERR")
        binding.editNumberPlate.editText.doAfterTextChanged {
            isEnable(it)
        }
        binding.editReferenceNumber.doAfterTextChanged {
            val builder = StringBuilder()
            for (i in 0 until
                    it.toString().length) {
                builder.append(it.toString()[i].toString())
                builder.append("\u00A0")
            }
            binding.editReferenceNumber.contentDescription = builder.toString()
            binding.editReferenceNumber.setHint(builder.toString())
            isEnable(it)
        }
        binding.point1Ll.contentDescription =
            resources.getString(R.string.accessibility_bullet) + "." + resources.getString(R.string.paid_crossing_point1)
        binding.point2Ll.contentDescription =
            resources.getString(R.string.accessibility_bullet) + "." + resources.getString(R.string.paid_crossing_point2)
    }

    override fun initCtrl() {
        binding.editNumberPlate.editText.doAfterTextChanged { isEnable(it) }
        binding.findVehicle.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.loginWithRefAndPlateNumber, ::loginWithRefHeader)
        }
    }

    private fun isEnable(editable: Editable?) {
        var isReferenceNumberValid = true
        var isPlateNumberValid = true
        if (binding.editReferenceNumber.getText().toString().trim().isEmpty()) {
            binding.errorMobileNumber.gone()
            isReferenceNumberValid = false
        } else {
            if (!Regex(paymentRefereceNumberRegex).matches(
                    binding.editReferenceNumber.getText().toString()
                )
            ) {
                isReferenceNumberValid = false
                binding.errorMobileNumber.visible()
                binding.errorMobileNumber.setText(getString(R.string.payment_reference_number_must_only_include_letters_a_to_z_and_numbers_0_to_9))
            } else {
                binding.errorMobileNumber.gone()
            }
        }

        if (binding.editNumberPlate.getText().toString().trim().isEmpty()) {
            isPlateNumberValid = false
            binding.editNumberPlate.removeError()
        } else {
            if (!Regex(plateNumberREgex).matches(binding.editNumberPlate.getText().toString())) {
                isPlateNumberValid = false
                binding.editNumberPlate.setErrorText(getString(R.string.str_vehicle_registration))
            } else {
                binding.editNumberPlate.removeError()
            }
        }
        if (isPlateNumberValid && isReferenceNumberValid) {
            binding.findVehicle.isEnabled = true
        } else {
            binding.findVehicle.isEnabled = false
        }

//        else if(Regex(paymentRefereceNumberRegex).matches(binding.editReferenceNumber.getText().toString()) && Regex(plateNumberREgex).matches(binding.editNumberPlate.getText().toString())){
//            binding.findVehicle.isEnabled = true
//            binding.editReferenceNumber.removeError()
//            binding.editNumberPlate.removeError()
//        }
        binding.editNumberPlate.binding.inputFirstName.contentDescription = editable?.toString()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.findVehicle -> {

                AdobeAnalytics.setActionTrack(
                    "continue",
                    "check crossings:login",
                    "login",
                    "english",
                    "check crossings",
                    "home",
                    sessionManager.getLoggedInUser()
                )

                hideKeyboard()
                isCalled = true
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                val checkPaidCrossingReq = CheckPaidCrossingsRequest(
                    referenceNumber =
                    binding.editReferenceNumber.getText().toString(),
                    plateNumber = binding.editNumberPlate.getText().toString().uppercase()
                )
                viewModel.checkPaidCrossings(checkPaidCrossingReq)
            }
        }

    }

    private fun loginWithRefHeader(status: Resource<LoginWithPlateAndReferenceNumberResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isCalled) {
            when (status) {
                is Resource.Success -> {
                    val dataObj = status.data
                    val bundle = Bundle().apply {
                        putString(Constants.NAV_FLOW_KEY, navFlowCall)
                        val crossingDetailsModelsResponse = CrossingDetailsModelsResponse().apply {
                            referenceNumber = binding.editReferenceNumber.getText().toString()
                            accountActStatus = dataObj?.get(0)?.accountActStatus ?: ""
                            accountBalance = dataObj?.get(0)?.accountBalance ?: ""
                            accountNo = dataObj?.get(0)?.accountNo ?: ""
                            accountTypeCd = dataObj?.get(0)?.accountStatusCd ?: ""
                            expirationDate = dataObj?.get(0)?.expirationDate ?: ""
                            plateCountry = dataObj?.get(0)?.plateCountry
                            plateCountryToTransfer = dataObj?.get(0)?.plateCountry
                            plateNumberToTransfer = dataObj?.get(0)?.plateNo ?: ""
                            unusedTrip = dataObj?.get(0)?.unusedTrip ?: ""
                            vehicleClassBalanceTransfer = dataObj?.get(0)?.vehicleClass
                        }
                        putParcelable(Constants.NAV_DATA_KEY, crossingDetailsModelsResponse)
                    }
                    findNavController().navigate(
                        R.id.action_crossingCheck_to_crossing_details,
                        bundle
                    )
                }

                is Resource.DataError -> {
                    if (status.errorMsg.contains("401")) {
                        binding.errorMobileNumber.visible()
                        binding.errorMobileNumber.setText(getString(R.string.error_check_paid_crossings))
                    } else {
                        binding.errorMobileNumber.gone()
                        ErrorUtil.showError(binding.root, status.errorMsg)
                    }
                }

                else -> {
                }
            }
            isCalled = false
        }

    }


}