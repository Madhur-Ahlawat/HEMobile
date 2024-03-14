package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPayForCrossingsBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.announceDropDown
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.setupTextAccessibilityDelegate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PayForCrossingsFragment : BaseFragment<FragmentPayForCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {
    private var totalAmountOfUnsettledTrips: Double? = 0.0
    private var crossingsList: MutableList<String> = mutableListOf()
    private var totalAmountOfAdditionalCrossings: Double? = 0.00
    private var loader: LoaderDialog? = null
    private var unsettled_trip_api = 0
    private var data: CrossingDetailsModelsResponse? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPayForCrossingsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                    CrossingDetailsModelsResponse::class.java
                ) != null
            ) {
                navData = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY, CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if (arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY) != null) {
                navData = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        data = navData as CrossingDetailsModelsResponse?
        unsettled_trip_api = data?.unSettledTrips ?: 0
        if (!edit_summary) {
            data?.unsettledTripChange = data?.unSettledTrips ?: 0
        }
        val additionalCrossings =
            (navData as CrossingDetailsModelsResponse).additionalCrossingCount
        val additionalCrossingsCharge = (navData as CrossingDetailsModelsResponse).additionalCharge
        binding.apply {
            val charge = data?.chargingRate?.toDouble()
            val unSettledTrips = data?.unsettledTripChange
            crossingsList = emptyList<String>().toMutableList()
            if (unSettledTrips != null && charge != null) {
                totalAmountOfUnsettledTrips = charge * unSettledTrips
            }

            if (additionalCrossingsCharge != null) {
                totalAmountOfAdditionalCrossings = additionalCrossingsCharge

            }
            crossingsList.clear()
            for (i in 1..additionalCrossings.plus(unSettledTrips!!)) {
                crossingsList.add(i.toString())
            }
            inputCountry.dataSet.clear()
            inputCountry.dataSet.addAll(crossingsList)
            inputCountry.setSelectedValue(unSettledTrips.toString())
            inputTotalAmount.text = getString(R.string.currency_symbol) + String.format(
                "%.2f",
                totalAmountOfUnsettledTrips ?: 0
            )


            binding.titleText2.text = Html.fromHtml(
                getString(R.string.str_pay_for_crossing_point2,
                    String.format("%.2f", data?.chargingRate?.toDouble()),
                    data?.dvlaclass?.let { Utils.getVehicleType(requireActivity(), it) }),
                Html.FROM_HTML_MODE_COMPACT
            )
        }

        data?.unsettledTripChange =
            binding.inputCountry.getSelectedValue()?.toInt() ?: 0

    }


    override fun initCtrl() {
        binding.inputCountry.dropDownItemSelectListener = this
        binding.inputCountry.setupTextAccessibilityDelegate(binding.inputCountry,"dropdown")
        binding.btnAdditionalCrossing.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        binding.inputCountry.isLongClickable = false

       // announceDropDown(binding.inputCountry,"dropdown")

    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAdditionalCrossing -> {
                additionalCrossingClick()
            }

            R.id.btnNext -> {

                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putDouble(
                    Constants.DATA,
                    binding.inputTotalAmount.text.toString()
                        .replace(getString(R.string.currency_symbol), "").toDouble()
                )
                if (!edit_summary) {
                    data?.additionalCrossingCount = 0
                }
                bundle.putParcelable(Constants.NAV_DATA_KEY, data)

                if (edit_summary) {

                    findNavController().navigate(
                        R.id.action_payCrossingsFragment_to_crossingCheckAnswersFragment,
                        bundle
                    )
                } else {
                    val crossings = binding.inputCountry.selectedItemDescription?.toInt()
                    if (crossings == 0) {
                        displayCustomMessage(getString(R.string.purchase_no_crossings),
                            getString(R.string.to_continue_you_must_pay_for_at_least_one_recent_additional_crossing),
                            getString(R.string.str_continue),
                            getString(R.string.str_cancel),
                            object : DialogPositiveBtnListener {
                                override fun positiveBtnClick(dialog: DialogInterface) {
                                    additionalCrossingClick()
                                }
                            },
                            object : DialogNegativeBtnListener {
                                override fun negativeBtnClick(dialog: DialogInterface) {
                                }
                            })
                    } else {
                        findNavController().navigate(
                            R.id.action_payCrossingsFragment_to_crossingRecieptFragment,
                            bundle
                        )
                    }
                }
            }
        }
    }

    private fun additionalCrossingClick() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        bundle.putParcelable(Constants.NAV_DATA_KEY, data)
        bundle.putString(Constants.NAV_FLOW_FROM, Constants.PAY_FOR_CROSSINGS)
        bundle.putBoolean(Constants.EDIT_SUMMARY, edit_summary)
        bundle.putBoolean(Constants.HAVE_RECENT_CROSSINGS, true)
        findNavController().navigate(
            R.id.action_payCrossingsFragment_to_additionalCrossingsFragment,
            bundle
        )
    }


    override fun onRetryClick(apiUrl: String) {

    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        data?.unsettledTripChange = selectedItem.toInt()
        val charge = data?.chargingRate?.toDouble()
        if (charge != null) {
            val total = (data?.unsettledTripChange ?: 0) * charge
            binding.inputTotalAmount.text = getString(R.string.currency_symbol) + String.format(
                "%.2f",
                total.toDouble()
            )
        }
    }
}
