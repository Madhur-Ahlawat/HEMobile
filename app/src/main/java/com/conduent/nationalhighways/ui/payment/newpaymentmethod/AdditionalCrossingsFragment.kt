package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentAdditionalCrossingsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AdditionalCrossingsFragment : BaseFragment<FragmentAdditionalCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener, DropDownItemSelectListener {

    private var loader: LoaderDialog? = null
    private var data : CrossingDetailsModelsResponse? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAdditionalCrossingsBinding.inflate(inflater, container, false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.titleText2.text =  Html.fromHtml(getString(R.string.recent_crossings_txt), Html.FROM_HTML_MODE_COMPACT)
        data = arguments?.getParcelable(Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java)
        binding.apply {
            numberAdditionalCrossings.dataSet.addAll(resources.getStringArray(R.array.crossings))
            numberAdditionalCrossings.setSelectedValue("1")
            val charge = data?.chargingRate?.replace("£","")?.replace("$","")?.toDouble()
            if(charge != null) {
                val unSettledTrips = data?.unSettledTrips?.toInt()
                var recent = 0.0
                if (unSettledTrips != null && unSettledTrips != 0) {
                    recent = charge * unSettledTrips
                }
                val total = recent + charge
                recentCrossing.setText(getString(R.string.currency_symbol) + recent)
                paymentCrossing.setText(getString(R.string.currency_symbol) + charge)
                totalAmount.setText(getString(R.string.currency_symbol) + total)
                val additional = charge*numberAdditionalCrossings.selectedItemValue!!.toInt()
                data?.totalAmount=total
                data?.additionalCrossingCount = numberAdditionalCrossings.selectedItemValue!!.toInt()
                data?.additionalCharge=additional

            }
        }
        displayCustomMessage(getString(R.string.additional_crossings_txt),
            getString(R.string.only_use_this_option_for_crossings_planned),
        getString(R.string.str_continue),"",null,null,View.GONE)


    }

    override fun initCtrl() {
        binding.numberAdditionalCrossings.dropDownItemSelectListener = this
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {

            R.id.btnNext -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                bundle.putDouble(Constants.DATA,binding.totalAmount.getText().toString().replace(getString(R.string.currency_symbol),"").toDouble())
                bundle.putParcelable(Constants.NAV_DATA_KEY, data as Parcelable?)
                findNavController().navigate(R.id.action_additionalCrossingsFragment_to_crossingRecieptFragment,bundle)
            }
        }
    }


    override fun onRetryClick() {

    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {
    }

    override fun onItemSlected(position: Int, selectedItem: String) {
        val charge = data?.chargingRate?.toDouble()
        if(charge != null){
//            val total = charge*selectedItem.toInt()
//            binding.paymentCrossing.setText(getString(R.string.currency_symbol)+total)
            val unSettledTrips = data?.unSettledTrips?.toInt()
            var recent = 0.0
            if(unSettledTrips != null){
                 recent = charge*unSettledTrips
            }
            val additional = charge*selectedItem.toInt()
            val total = recent+additional
            data?.totalAmount=total
            data?.additionalCharge=additional
            data?.additionalCrossingCount = selectedItem.toInt()
            binding.recentCrossing.setText(getString(R.string.currency_symbol)+recent)
            binding.paymentCrossing.setText(getString(R.string.currency_symbol)+additional)
            binding.totalAmount.setText(getString(R.string.currency_symbol)+data?.totalAmount)
        }
    }

    fun showError(view: View?, message: String?) {
        try {
            val dialog = ErrorDialog()
            val bundle = Bundle()
            bundle.putString(Constants.DATA, message)
            dialog.arguments = bundle
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

            when (view?.context) {
                is AppCompatActivity -> dialog.show(
                    (view.context as AppCompatActivity).supportFragmentManager,
                    Constants.ERROR_DIALOG
                )
                is ContextWrapper -> dialog.show(
                    (((view.context as ContextWrapper).baseContext)
                            as AppCompatActivity).supportFragmentManager,
                    Constants.ERROR_DIALOG
                )
            }

        } catch (e: Exception) {

        }
    }

}