package com.conduent.nationalhighways.ui.landing

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentEnquiryStatusBinding
import com.conduent.nationalhighways.databinding.FragmentPaymentSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Constants.PAY_FOR_CROSSINGS
import com.conduent.nationalhighways.utils.common.Constants.PLATE_NUMBER
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible

class EnquiryStatusDetailsFragment : BaseFragment<FragmentEnquiryStatusBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {
    private var additionalCrossingsCount: Int? = 0

    private var crossingsList: MutableList<String>? = mutableListOf()
    private var data: CrossingDetailsModelsResponse? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryStatusBinding =
        FragmentEnquiryStatusBinding.inflate(inflater, container, false)

    override fun init() {
        navData?.let {
            data = it as CrossingDetailsModelsResponse
        }
        setData()
        setClickListeners()
    }

    private fun setData() {
        binding?.apply {

        }
    }

    private fun setClickListeners() {
        binding?.apply {
            btnNext.setOnClickListener(this@EnquiryStatusDetailsFragment)
        }
    }

    fun getRequiredText(text: String) = text.substringAfter(' ')

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

            R.id.editPaymentAmount -> {
                if (data?.unSettledTrips!! > 0) {
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_PayForCrossingsFragment,
                        enableEditMode()
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_crossingCheckAnswersFragment_to_additionalCrossingsFragment,
                        enableEditMode()
                    )
                }

            }
        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, PAY_FOR_CROSSINGS)
        bundle.putString(
            PLATE_NUMBER,
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

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {

    }

    override fun onRetryClick(apiUrl: String){
    }

}