package com.conduent.nationalhighways.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.VehicleInfoDetails
import com.conduent.nationalhighways.data.model.checkpaidcrossings.*
import com.conduent.nationalhighways.databinding.FragmentCheckPaidCrossingChangeVrmBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.checkpaidcrossings.dialog.ConfirmChangeDialog
import com.conduent.nationalhighways.ui.checkpaidcrossings.dialog.ConfirmChangeListener
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.VehicleClassTypeConverter
import com.conduent.nationalhighways.utils.common.*
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CheckPaidCrossFragmentChangeVrm : BaseFragment<FragmentCheckPaidCrossingChangeVrmBinding>(),
    View.OnClickListener, ConfirmChangeListener {

    private var isClicked: Boolean = false
    private var exists: Boolean = false
    private var vrm: String = ""
    private var loader: LoaderDialog? = null
    private val viewModel: CheckPaidCrossingViewModel by activityViewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCheckPaidCrossingChangeVrmBinding.inflate(inflater, container, false)

    override fun init() {
        AdobeAnalytics.setScreenTrack(
            "check crossings:change vrm",
            "vehicle",
            "english",
            "check crossings",
            "home",
            "check crossings:change vrm",
            sessionManager.getLoggedInUser()
        )

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        val country = arguments?.getString(Constants.COUNTRY_TYPE)
        vrm = arguments?.getString(Constants.CHECK_PAID_CROSSING_VRM_ENTERED).toString()
        exists = arguments?.getBoolean(Constants.CHECK_PAID_CROSSING_VRM_EXISTS) == true

        binding.apply {
            if (exists) {
                val mVrmDetailsDvla =
                    arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)

                regNum.text = vrm
                countryMarker.text = country
                vehicleClass.text =
                    VehicleClassTypeConverter.toClassCode(mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleClass)
                make.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleMake
                model.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleModel
                color.text = mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleColor

                changeVehicle.text = getString(R.string.str_continue)
                removeVehicle.gone()
            } else {
                regNum.text = vrm
                countryMarker.text = country
                vehicleClass.text = "-"
                make.text = "-"
                model.text = "-"
                color.text = "-"
                changeVehicle.text = getString(R.string.str_change)
                removeVehicle.visible()
            }
        }
    }

    override fun initCtrl() {
        binding.removeVehicle.setOnClickListener(this)
        binding.changeVehicle.setOnClickListener(this)
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.balanceTransfer, ::balanceTransfer)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.removeVehicle -> {
                findNavController().navigate(R.id.action_checkPaidCrossingChangeVrm_to_enterVrmFragment)
            }
            R.id.changeVehicle -> {
                if (!exists) {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_addVehicleDetailsFragment,
                        arguments
                    )
                } else {
                    ConfirmChangeDialog.newInstance(
                        getString(
                            R.string.str_confirm_if_u_wish_to_associate_vehicle_no,
                            arguments?.getParcelable<VehicleInfoDetails?>
                                (Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)?.retrievePlateInfoDetails?.plateNumber,
                            viewModel.paidCrossingOption.value?.vrm
                        ),
                        this
                    ).show(childFragmentManager, Constants.DELETE_VEHICLE_GROUP_DIALOG)
                }
            }
        }
    }

    override fun onConfirmClick() {
        val country =
            arguments?.getString(Constants.COUNTRY_TYPE)
        val mVrmDetailsDvla =
            arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)

        val mTransferInfo = TransferInfo(
            "1",
            mVrmDetailsDvla?.retrievePlateInfoDetails?.plateNumber,
            "HE",
            country,
            mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleClass,
            mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleMake,
            mVrmDetailsDvla?.retrievePlateInfoDetails?.vehicleModel,
            ""
        )
        val mBalRequest =
            BalanceTransferRequest(
                viewModel.paidCrossingOption.value?.vrm,
                viewModel.paidCrossingResponse.value?.plateCountry,"",
                mTransferInfo
            )
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

        isClicked = true
        viewModel.balanceTransfer(mBalRequest)
    }

    private fun balanceTransfer(status: Resource<BalanceTransferResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        if (isClicked) {
            when (status) {
                is Resource.Success -> {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_checkPaidCrossingChangeVrmConformSuccess,
                        arguments
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

}