package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.core.widget.addTextChangedListener
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
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossingsFragment : BaseFragment<FragmentPaidPreviousCrossingsBinding>(),
    View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    private var isCalled = false
    @Inject
    lateinit var sessionManager: SessionManager

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
        binding.editReferenceNumber.setText("1-97832991")
        binding.editNumberPlate.setText("DF4654")
        isEnable()
    }

    override fun initCtrl() {
        binding.editReferenceNumber.editText.addTextChangedListener { isEnable() }
        binding.editNumberPlate.editText.addTextChangedListener { isEnable() }
        binding.findVehicle.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.loginWithRefAndPlateNumber, ::loginWithRefHeader)
        }
    }

    private fun isEnable() {
        binding.findVehicle.isEnabled = binding.editNumberPlate.isNotEmpty() &&
            binding.editNumberPlate.isNotEmpty()
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
                val checkPaidCrossingReq = CheckPaidCrossingsRequest(referenceNumber=
                    binding.editReferenceNumber.getText().toString(), plateNumber = binding.editNumberPlate.getText().toString())
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
                        var crossingDetailsModelsResponse=CrossingDetailsModelsResponse().apply {
                            referenceNumber = binding.editReferenceNumber.getText().toString()
                            plateNumber = binding.editNumberPlate.getText().toString()
                            accountActStatus= dataObj?.get(0)?.accountActStatus!!
                            accountBalance= dataObj?.get(0)?.accountBalance!!
                            accountNo= dataObj?.get(0)?.accountNo!!
                            accountTypeCd=dataObj?.get(0)?.accountStatusCd!!
                            expirationDate=dataObj?.get(0)?.expirationDate!!
                            plateCountry=dataObj?.get(0)?.plateCountry
                            plateNo=dataObj?.get(0)?.plateNo!!
                            unusedTrip=dataObj?.get(0)?.unusedTrip!!
                            vehicleClass=dataObj?.get(0)?.vehicleClass
                        }
                        putParcelable(Constants.NAV_DATA_KEY, crossingDetailsModelsResponse)
                    }
                    findNavController().navigate(
                        R.id.action_crossingCheck_to_crossing_details,
                        bundle
                    )
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
                else -> {
                }
            }
            isCalled = false
        }

    }


}