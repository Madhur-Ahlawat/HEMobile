package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentPayForCrossingsBinding
import com.conduent.nationalhighways.listener.DialogNegativeBtnListener
import com.conduent.nationalhighways.listener.DialogPositiveBtnListener
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList


@AndroidEntryPoint
class PayForCrossingsFragment : BaseFragment<FragmentPayForCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {

    private var loader: LoaderDialog? = null
    private var data : CrossingDetailsModelsResponse? = null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentPayForCrossingsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.titleText2.text =
            Html.fromHtml(getString(R.string.recent_crossings_txt), Html.FROM_HTML_MODE_COMPACT)
        data = navData as CrossingDetailsModelsResponse?
        binding.apply {
            inputTotalAmount.isEnabled = false
            val charge = data?.chargingRate?.toDouble()
            val unSettledTrips = data?.unSettledTrips?.toInt()
            if(unSettledTrips != null && charge != null){
                val index = emptyList<String>().toMutableList()
                for (i in 0..unSettledTrips){
                    index.add(i.toString())
                }
                inputCountry.dataSet.addAll(index)
                inputCountry.setSelectedValue(unSettledTrips.toString())
                val total = charge*unSettledTrips
                inputTotalAmount.setText(getString(R.string.currency_symbol)+total)
            }
            binding.titleText2.text =  Html.fromHtml(getString(R.string.recent_crossings_txt,data?.chargingRate,
                data?.dvlaclass?.let { Utils.getVehicleType(it) }), Html.FROM_HTML_MODE_COMPACT)
        }

        (navData as CrossingDetailsModelsResponse).recentCrossingCount =
            binding.inputCountry.getSelectedValue()!!.toInt()
    }

    override fun initCtrl() {
        binding.inputCountry.dropDownItemSelectListener = this
        binding.btnAdditionalCrossing.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAdditionalCrossing -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                bundle.putParcelable(Constants.NAV_DATA_KEY,data)
                findNavController().navigate(R.id.action_payCrossingsFragment_to_additionalCrossingsFragment,bundle)
            }
            R.id.btnNext -> {
                val crossings = binding.inputCountry.selectedItemDescription?.toInt()
                if(crossings == 0){
                    displayCustomMessage(getString(R.string.purchase_no_crossings),
                        getString(R.string.to_continue_you_must_pay_for_at_least_one_recent_additional_crossing),
                        getString(R.string.str_continue),
                        getString(R.string.str_cancel),
                        object : DialogPositiveBtnListener {
                            override fun positiveBtnClick(dialog: DialogInterface) {
                                findNavController().navigate(R.id.action_payCrossingsFragment_to_landingFragment)

                            }
                        },
                        object : DialogNegativeBtnListener {
                            override fun negativeBtnClick(dialog: DialogInterface) {
                            }
                        })
                } else {
                    val bundle = Bundle()
                    bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                    bundle.putDouble(
                        Constants.DATA,
                        binding.inputTotalAmount.getText().toString()
                            .replace(getString(R.string.currency_symbol), "").toDouble()
                    )
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                    findNavController().navigate(
                        R.id.action_payCrossingsFragment_to_crossingRecieptFragment,
                        bundle
                    )
                }

            }
        }
    }


    override fun onRetryClick() {

    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {

        val charge = data?.chargingRate?.toInt()
        if (charge != null) {
            val total = charge * selectedItem.toInt()
            data?.recentCrossingCount = selectedItem.toInt()
            binding.inputTotalAmount.setText(getString(R.string.currency_symbol) + total)
        }
    }
}
