package com.heandroid.ui.checkpaidcrossings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.VehicleInfoDetails
import com.heandroid.data.model.checkpaidcrossings.*
import com.heandroid.databinding.FragmentCheckPaidCrossingChangeVrmBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.checkpaidcrossings.dialog.ConfirmChangeDialog
import com.heandroid.ui.checkpaidcrossings.dialog.ConfirmChangeListener
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.vehicle.vehiclegroup.dialog.DeleteVehicleGroupDialog
import com.heandroid.utils.VehicleClassTypeConverter
import com.heandroid.utils.common.*
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class CheckPaidCrossFragmentChangeVrm : BaseFragment<FragmentCheckPaidCrossingChangeVrmBinding>(),
    View.OnClickListener, ConfirmChangeListener {

    private var exists: Boolean = false
    private var vrm: String = ""
    private var loader: LoaderDialog? = null
    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCheckPaidCrossingChangeVrmBinding.inflate(inflater, container, false)

    override fun init() {

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

            }
            R.id.changeVehicle -> {
                if (!exists) {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_addVehicleDetailsFragment,
                        arguments
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_checkPaidCrossingChangeVrm_to_checkPaidCrossingChangeVrmConform,
                        arguments
                    )
//                    ConfirmChangeDialog.newInstance(
//                        getString(
//                            R.string.str_confirm_if_u_wish_to_associate_vehicle_no,
//                            arguments?.getParcelable<VehicleInfoDetails?>
//                                (Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)?.retrievePlateInfoDetails?.plateNumber,
//                            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)?.ref
//                        ),
//                        this
//                    ).show(childFragmentManager, Constants.DELETE_VEHICLE_GROUP_DIALOG)
                }
            }
        }
    }

    override fun onConfirmClick() {
        val mData =
            arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)!!
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!
        val country =
            arguments?.getString(Constants.COUNTRY_TYPE)
        val mVrmDetailsDvla =
            arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)!!

        val mTransferInfo = TransferInfo(
            "1",
            mVrmDetailsDvla.retrievePlateInfoDetails?.plateNumber!!,
            "HE",
            country!!,
            mVrmDetailsDvla.retrievePlateInfoDetails?.vehicleClass!!,
            mVrmDetailsDvla.retrievePlateInfoDetails?.vehicleMake!!,
            mVrmDetailsDvla.retrievePlateInfoDetails?.vehicleModel!!,
            ""
        )
        val mBalRequest =
            BalanceTransferRequest(mDataVrmRef.vrm!!, mData.plateCountry!!, mTransferInfo)
        loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

        viewModel.balanceTransfer(mBalRequest)
    }

    private fun balanceTransfer(status: Resource<BalanceTransferResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
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
    }

}