package com.heandroid.ui.checkpaidcrossings

import android.os.Bundle
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
import com.heandroid.databinding.FragmentEnterVrmCheckChangeConformBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception

@AndroidEntryPoint
class CheckPaidCrossFragmentConformChangeVrm :
    BaseFragment<FragmentEnterVrmCheckChangeConformBinding>(), View.OnClickListener {

    private val viewModel: CheckPaidCrossingViewModel by viewModels()

    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnterVrmCheckChangeConformBinding =
        FragmentEnterVrmCheckChangeConformBinding.inflate(inflater, container, false)

    override fun init() {

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        val mDataVrmRef =
            arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!

        val mVrmDetailsDvla =
            arguments?.getParcelable<VehicleInfoDetails?>(Constants.CHECK_PAID_CROSSINGS_VRM_DETAILS)!!

        binding.topTxt.text = getString(
            R.string.str_confirm_if_u_wish_to_associate_vehicle_no,
            mVrmDetailsDvla.retrievePlateInfoDetails!!.plateNumber,
            mDataVrmRef.ref!!
        )
    }

    override fun initCtrl() {
        binding.confirmBtn.setOnClickListener(this)

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.balanceTransfer, ::balanceTransfer)
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.confirm_btn -> {
                val mData =
                    arguments?.getParcelable<CheckPaidCrossingsResponse?>(Constants.CHECK_PAID_CHARGE_DATA_KEY)!!
                val mDataVrmRef =
                    arguments?.getParcelable<CheckPaidCrossingsOptionsModel?>(Constants.CHECK_PAID_REF_VRM_DATA_KEY)!!
                val index =
                    arguments?.getInt("Index")
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
                    BalanceTransferRequest(mDataVrmRef.vrm!!,mData?.plateCountry!! , mTransferInfo)
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                viewModel.balanceTransfer(mBalRequest)
            }
        }

    }

    private fun balanceTransfer(status: Resource<BalanceTransferResponse?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    Logg.logging("CheckpaidCrossi", "response ${status.data}")

                    binding?.root?.post {
                        findNavController().navigate(
                            R.id.action_checkPaidCrossingChangeVrmConform_to_checkPaidCrossingChangeVrmConformSuccess,
                            arguments
                        )

                    }

                }
                is Resource.DataError -> {
                    Logg.logging("CheckpaidCrossi", "error response ${status.errorMsg}")

                    ErrorUtil.showError(binding.root, status.errorMsg)
                }
            }
        } catch (e: Exception) {
        }
    }

}