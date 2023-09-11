package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(arguments?.getParcelable(Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java)!=null){
                data = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY,CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if(arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY)!=null){
                data = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        navData = data
        binding.apply {
            numberAdditionalCrossings.dataSet.clear()
            numberAdditionalCrossings.dataSet.addAll(resources.getStringArray(R.array.crossings))
            if(data?.additionalCrossingCount!!>0){
                numberAdditionalCrossings.setSelectedValue(data?.additionalCrossingCount.toString())
            }
            else{
                numberAdditionalCrossings.setSelectedValue("0")
                data?.additionalCrossingCount=0
            }
            val charge = data?.chargingRate?.replace("£","")?.replace("$","")?.toDouble()
            if(charge != null) {
                val mUnSettledTrips = data?.unSettledTrips
                val additionalCrossingsCount = data?.additionalCrossingCount
                var recentCrossingsAmount = 0.0
                var additionalCrossingsAmount = 0.0
                if (mUnSettledTrips != null && mUnSettledTrips > 0) {
                    recentCrossingsAmount = charge * mUnSettledTrips
                }
                if(additionalCrossingsCount !=null && additionalCrossingsCount >0){
                    additionalCrossingsAmount = charge * additionalCrossingsCount
                }
                val total = recentCrossingsAmount + additionalCrossingsAmount
                recentCrossing.setText(getString(R.string.currency_symbol) +  String.format("%.2f", recentCrossingsAmount))
                paymentCrossing.setText(getString(R.string.currency_symbol) +  String.format("%.2f", additionalCrossingsAmount))
                totalAmount.setText(getString(R.string.currency_symbol) +  String.format("%.2f", total))
                data?.totalAmount=total
            }
            recentCrossing.isEnabled = false
            paymentCrossing.isEnabled = false
            totalAmount.isEnabled = false
            titleText2?.text =  Html.fromHtml(getString(R.string.recent_crossings_txt, String.format("%.2f", charge),data?.vehicleType), Html.FROM_HTML_MODE_COMPACT)

        }
        displayCustomMessage(getString(R.string.additional_crossings_txt),
            getString(R.string.only_use_this_option_for_crossings_planned),
        getString(R.string.str_continue),"",null,null,View.GONE)

    checkButton()
    }

    private fun checkButton() {
        if(binding.totalAmount.editText.getText().toString().trim().replace("£","").replace("$","").replace(" ","").toDouble()>0){
            binding.btnNext.isEnabled=true
        }
        else{
            binding.btnNext.isEnabled=false
        }
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
                bundle.putDouble(Constants.DATA,binding.totalAmount.getText().toString().trim().replace(getString(R.string.currency_symbol),"").replace("$","").replace(getString(R.string.currency_symbol),"").replace(" ","").replace(".","").toDouble())
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
            data?.additionalCrossingCount = selectedItem.toInt()
//            val total = charge*selectedItem.toInt()
//            binding.paymentCrossing.setText(getString(R.string.currency_symbol)+total)
            val mUnSettledTrips = data?.unSettledTrips
            val additionalCrossingsCount = data?.additionalCrossingCount
            var recentCrossingsAmount = 0.0
            var additionalCrossingsAmount = 0.0
            if (mUnSettledTrips != null && mUnSettledTrips > 0) {
                recentCrossingsAmount = charge * mUnSettledTrips
            }
            if(additionalCrossingsCount !=null && additionalCrossingsCount >0){
                additionalCrossingsAmount = charge * additionalCrossingsCount
            }
            val total = recentCrossingsAmount + additionalCrossingsAmount
            binding?.recentCrossing?.setText(getString(R.string.currency_symbol) +  String.format("%.2f", recentCrossingsAmount))
            binding?.paymentCrossing?.setText(additionalCrossingsCount.toString())
            data?.totalAmount=total
            data?.additionalCharge=additionalCrossingsAmount
            binding.totalAmount.setText(getString(R.string.currency_symbol)+ String.format("%.2f", data?.totalAmount))
        }
        checkButton()
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