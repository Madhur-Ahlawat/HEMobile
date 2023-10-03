package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentAdditionalCrossingsBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AdditionalCrossingsFragment : BaseFragment<FragmentAdditionalCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var loader: LoaderDialog? = null
    private var data: CrossingDetailsModelsResponse? = null
    var requiredNoAdditionalCrossings:Boolean=false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentAdditionalCrossingsBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                    CrossingDetailsModelsResponse::class.java
                ) != null
            ) {
                data = arguments?.getParcelable(

                    Constants.NAV_DATA_KEY, CrossingDetailsModelsResponse::class.java
                )
            }
        } else {
            if (arguments?.getParcelable<CrossingDetailsModelsResponse>(Constants.NAV_DATA_KEY) != null) {
                data = arguments?.getParcelable(
                    Constants.NAV_DATA_KEY,
                )
            }
        }
        navData = data
        binding.apply {
            if (data?.additionalCrossingCount!! > 0) {
                numberAdditionalCrossings.text = "" + data?.additionalCrossingCount.toString()
                requiredNoAdditionalCrossings=true
            } else {
                numberAdditionalCrossings.text = "0"
                data?.additionalCrossingCount = 0
                requiredNoAdditionalCrossings=false
            }
            val charge = data?.chargingRate?.replace("£", "")?.replace("$", "")?.toDouble()
            if (charge != null) {
                val mUnSettledTrips = data?.unSettledTrips
                val additionalCrossingsCount = data?.additionalCrossingCount
                var recentCrossingsAmount = 0.0
                var additionalCrossingsAmount = 0.0
                if (mUnSettledTrips != null && mUnSettledTrips > 0) {
                    recentCrossingsAmount = charge * mUnSettledTrips
                }
                if (additionalCrossingsCount != null && additionalCrossingsCount > 0) {
                    additionalCrossingsAmount = charge * additionalCrossingsCount
                }
                val total = recentCrossingsAmount + additionalCrossingsAmount
                recentCrossing.setText(
                    getString(R.string.currency_symbol) + String.format(
                        "%.2f",
                        recentCrossingsAmount
                    )
                )
                paymentCrossing.setText(
                    getString(R.string.currency_symbol) + String.format(
                        "%.2f",
                        additionalCrossingsAmount
                    )
                )
                totalAmount.setText(
                    getString(R.string.currency_symbol) + String.format(
                        "%.2f",
                        total
                    )
                )
                data?.totalAmount = total
            }
            recentCrossing.isEnabled = false
            paymentCrossing.isEnabled = false
            totalAmount.isEnabled = false
            titleText2.text = Html.fromHtml(
                getString(
                    R.string.recent_crossings_txt,
                    String.format("%.2f", charge),
                    data?.vehicleType
                ), Html.FROM_HTML_MODE_COMPACT
            )

        }
        displayCustomMessage(
            getString(R.string.additional_crossings_txt),
            getString(R.string.only_use_this_option_for_crossings_planned),
            getString(R.string.str_continue), "", null, null, View.GONE
        )

        checkButton()

        binding.numberAdditionalCrossings.editText.addTextChangedListener(GenericTextWatcher(1))
    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            requiredNoAdditionalCrossings=  if(charSequence.toString().isNotEmpty() && charSequence.toString().toInt()>0 && charSequence.toString().toInt()<=50){
                val charge = data?.chargingRate?.toDouble()
                if (charge != null) {
                    data?.additionalCrossingCount = binding.numberAdditionalCrossings.text.toString().toInt()
//            val total = charge*selectedItem.toInt()
//            binding.paymentCrossing.setText(getString(R.string.currency_symbol)+total)
                    val mUnSettledTrips = data?.unSettledTrips
                    val additionalCrossingsCount = data?.additionalCrossingCount
                    var recentCrossingsAmount = 0.0
                    var additionalCrossingsAmount = 0.0
                    if (mUnSettledTrips != null && mUnSettledTrips > 0) {
                        recentCrossingsAmount = charge * mUnSettledTrips
                    }
                    if (additionalCrossingsCount != null && additionalCrossingsCount > 0) {
                        additionalCrossingsAmount = charge * additionalCrossingsCount
                    }
                    val total = recentCrossingsAmount + additionalCrossingsAmount
                    binding.recentCrossing?.setText(
                        getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            recentCrossingsAmount
                        )
                    )
                    binding.paymentCrossing?.setText(additionalCrossingsCount.toString())
                    data?.totalAmount = total
                    data?.additionalCharge = additionalCrossingsAmount
                    binding.totalAmount.setText(
                        getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            data?.totalAmount
                        )
                    )
                }
                binding.numberAdditionalCrossings.removeError()
                true
            }else{
                if(charSequence.toString().trim().isEmpty()){
                    binding.numberAdditionalCrossings.removeError()
                }else if(charSequence.toString().toInt()==0 || charSequence.toString().toInt()>50){
                    binding.numberAdditionalCrossings.setErrorText(resources.getString(R.string.str_number_additional_crossings_btw_1_to_50))
                }
                false
            }
            checkButton()


        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }
    private fun checkButton() {
        binding.btnNext.isEnabled =
           ( binding.totalAmount.editText.text.toString().trim().replace("£", "")
                .replace("$", "").replace(" ", "").toDouble() > 0 && requiredNoAdditionalCrossings)
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {

            R.id.btnNext -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putDouble(
                    Constants.DATA,
                    binding.totalAmount.getText().toString().trim()
                        .replace(getString(R.string.currency_symbol), "").replace("$", "")
                        .replace(getString(R.string.currency_symbol), "").replace(" ", "")
                        .replace(".", "").toDouble()
                )
                bundle.putParcelable(Constants.NAV_DATA_KEY, data as Parcelable?)
                findNavController().navigate(
                    R.id.action_additionalCrossingsFragment_to_crossingRecieptFragment,
                    bundle
                )
            }
        }
    }


    override fun onRetryClick(apiUrl: String) {

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