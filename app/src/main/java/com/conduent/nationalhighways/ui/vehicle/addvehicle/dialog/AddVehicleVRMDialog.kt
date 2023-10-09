package com.conduent.nationalhighways.ui.vehicle.addvehicle.dialog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.RetrievePlateInfoDetails
import com.conduent.nationalhighways.data.model.account.ValidVehicleCheckRequest
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.vehicle.PlateInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleInfoResponse
import com.conduent.nationalhighways.data.model.vehicle.VehicleResponse
import com.conduent.nationalhighways.databinding.DialogAddVehicleBinding
import com.conduent.nationalhighways.ui.base.BaseDialog
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.vehicle.VehicleMgmtViewModel
import com.conduent.nationalhighways.utils.DateUtils
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.openKeyboardForced
import com.conduent.nationalhighways.utils.extn.showToast
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleVRMDialog : BaseDialog<DialogAddVehicleBinding>() {

    private val viewModel: VehicleMgmtViewModel by viewModels()
    var time = (1 * 1000).toLong()
    private var loader: LoaderDialog? = null
    private var retrieveVehicle: RetrievePlateInfoDetails? = null
    var country = "UK"
    private var isObserverBack = false
    private var plateInfoResponse: PlateInfoResponse? = null
    private var vehicleResponse: VehicleResponse? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        DialogAddVehicleBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isObserverBack = true
    }

    override fun init() {
        dialog?.setCanceledOnTouchOutside(false)
        setBtnDisabled()
        binding.addVrmInput.onTextChanged {
            if (binding.addVrmInput.getText().toString().isNotEmpty()) {
                setBtnActivated()
            } else {
                setBtnDisabled()
            }
        }

        loader = LoaderDialog()
        loader?.setStyle(STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        AdobeAnalytics.setScreenTrack(
            "one of  payment:add vehicle:enter vrm dialog",
            "vehicle",
            "english",
            "one of payment",
            "home",
            "one of  payment:add vehicle:enter vrm dialog",
            sessionManager.getLoggedInUser()
        )

    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        binding.addVrmInput.post {
            binding.addVrmInput.openKeyboardForced()
        }
    }

    override fun initCtrl() {
        binding.ivClose.setOnClickListener {
            binding.addVrmInput.hideKeyboard()
            dismiss()
        }
        binding.addVehicleBtn.setOnClickListener {
            AdobeAnalytics.setActionTrack(
                "add vehicle",
                "one of  payment:add vehicle:enter vrm dialog",
                "vehicle",
                "english",
                "one of payment",
                "home",
                sessionManager.getLoggedInUser()
            )

            binding.addVrmInput.hideKeyboard()
            if (binding.addVrmInput.getText().toString().isNotEmpty()) {
                country = if (!binding.switchView.isChecked) {
                    "Non-UK"
                } else {
                    "UK"
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.addVehicleBtn.isEnabled = true
                }, time)

                if (country == "UK") {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    isObserverBack = true
                    getVehicleDataFromDVRM()

                } else {
                    setNavigation()
                }

            } else {
                requireContext().showToast("Please enter your vehicle number")
            }
        }

        binding.cancelBtn.setOnClickListener {
            AdobeAnalytics.setActionTrack(
                "cancel",
                "one of  payment:add vehicle:enter vrm dialog",
                "vehicle",
                "english",
                "one of payment",
                "home",
                sessionManager.getLoggedInUser()
            )
            binding.addVrmInput.hideKeyboard()
            dismiss()
        }
    }

    override fun observer() {
        observe(viewModel.findVehicleLiveData, ::apiResponseDVRM)
        observe(viewModel.validVehicleLiveData, ::apiResponseValidVehicle)
    }

    companion object {
        const val TAG = "AddVehicle"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_SUBTITLE = "KEY_SUBTITLE"
        private var mListener: AddVehicleListener? = null
        var isMakePayment = true

        fun newInstance(
            title: String,
            subTitle: String?,
            isMakePaymentScreen: Boolean,
            listener: AddVehicleListener
        ): AddVehicleVRMDialog {
            val args = Bundle()
            mListener = listener
            isMakePayment = isMakePaymentScreen
            args.putString(KEY_TITLE, title)
            args.putString(KEY_SUBTITLE, subTitle)
            val fragment = AddVehicleVRMDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private fun setBtnActivated() {
        binding.model = true
    }

    private fun setBtnDisabled() {
        binding.model = false
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.dialog_background
            )
        )
    }

    private fun getVehicleDataFromDVRM() {
        viewModel.getVehicleData(binding.addVrmInput.getText().toString().trim(), Constants.AGENCY_ID.toInt())
    }

    private fun apiResponseDVRM(resource: Resource<VehicleInfoDetails?>?) {

        if (loader?.isVisible == true)
            loader?.dismiss()

        if (isObserverBack) {
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        retrieveVehicle = resource.data.retrievePlateInfoDetails

                        if (isMakePayment)
                            setVrmDetails()
                        else
                            resource.data.retrievePlateInfoDetails?.let { it1 ->
                                checkForDuplicateVehicle(
                                    it1
                                )
                            }
                    }
                }

                is Resource.DataError -> {
                    isObserverBack = false

                    ErrorUtil.showError(binding.root, resource.errorMsg)
                    setNavigation()
                }
                else -> {}
            }
        }
    }

    private fun setNavigation() {
        plateInfoResponse = PlateInfoResponse()
        plateInfoResponse?.country = country
        plateInfoResponse?.number = binding.addVrmInput.getText().toString()

        vehicleResponse =
            VehicleResponse(plateInfoResponse, plateInfoResponse, VehicleInfoResponse())

        if (isMakePayment) {

            val bundle = Bundle().apply {
                putParcelable(Constants.DATA, vehicleResponse)
                putInt(Constants.VEHICLE_SCREEN_KEY, 3)
            }
            findNavController().navigate(
                R.id.action_makePaymentAddVehicleFragment_to_addVehicleDetailsFragment,
                bundle
            )
        } else {
            val bundle = Bundle().apply {
                putParcelable(Constants.DATA, vehicleResponse)
                putInt(Constants.VEHICLE_SCREEN_KEY, 2)
            }
            findNavController().navigate(
                R.id.action_addVehicleFragment_to_addVehicleDetailsFragment,
                bundle
            )
        }

    }

    private fun checkForDuplicateVehicle(plateInfo: RetrievePlateInfoDetails) {
        plateInfo.apply {
            val vehicleValidReqModel = ValidVehicleCheckRequest(
                plateNumber, country, "STANDARD",
                "2022", vehicleModel, vehicleMake, vehicleColor, "2", "HE"
            )
//            val vehicleValidReqModel = ValidVehicleCheckRequest(
//                plateNumber, country, "",
//                "", vehicleModel, vehicleMake, vehicleColor, "", ""
//            )
            viewModel.validVehicleCheck(vehicleValidReqModel, Constants.AGENCY_ID.toInt())
        }
    }

    private fun apiResponseValidVehicle(resource: Resource<String?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {

                // UK vehicle Valid from DVLA and Valid from duplicate vehicle check,move to next screen

                setVrmDetails()
            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
                findNavController().popBackStack()
            }
            else -> {}
        }
    }

    private fun setVrmDetails() {

        val plateInfoResp = PlateInfoResponse(
            binding.addVrmInput.getText().toString().trim(),
            country, "", "",
            "", "", ""
        )
        val vehicleInfoResp = VehicleInfoResponse(
            retrieveVehicle?.vehicleMake, retrieveVehicle?.vehicleModel, "", "",
            "", "", retrieveVehicle?.vehicleColor,
            retrieveVehicle?.vehicleClass?.let { VehicleClassTypeConverter.toClassName(it) },
            DateUtils.convertDateFormat(DateUtils.currentDate(), 0)
        )

        val mVehicleResponse =
            VehicleResponse(plateInfoResp, plateInfoResp, vehicleInfoResp)

        mListener?.onAddClick(mVehicleResponse)
    }

}