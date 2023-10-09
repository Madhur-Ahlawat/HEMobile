package com.conduent.nationalhighways.ui.payment.newpaymentmethod

import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.makeoneofpayment.CrossingDetailsModelsResponse
import com.conduent.nationalhighways.databinding.FragmentAdditionalCrossingsBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.ErrorDialog
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AdditionalCrossingsFragment : BaseFragment<FragmentAdditionalCrossingsBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var loader: LoaderDialog? = null
    private var data: CrossingDetailsModelsResponse? = null
    var requiredNoAdditionalCrossings: Boolean = false


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

        if (data?.unsettledTripChange == 0) {
            binding.recentCrossingHelper.gone()
            binding.recentCrossing.gone()
        } else {
            binding.recentCrossing.visible()
            binding.recentCrossing.visible()
        }
        binding.apply {
            if ((data?.additionalCrossingCount ?: 0) > 0) {
                numberAdditionalCrossings.setText("" + data?.additionalCrossingCount.toString())
                requiredNoAdditionalCrossings = true
            } else {
                numberAdditionalCrossings.setText("1")
                data?.additionalCrossingCount = 1
                requiredNoAdditionalCrossings = true
            }
            val charge = data?.chargingRate?.replace("£", "")?.replace("$", "")?.toDouble()
            Log.e("TAG", "init: charge " + charge)
            Log.e("TAG", "init: unsettledTripChange " + data?.unsettledTripChange)
            if (charge != null) {
                val mUnSettledTrips = data?.unsettledTripChange
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
                recentCrossing.text = getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    recentCrossingsAmount
                )
                paymentCrossing.text = getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    additionalCrossingsAmount
                )
                totalAmount.text = getString(R.string.currency_symbol) + String.format(
                    "%.2f",
                    total
                )
                data?.totalAmount = total
            }


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
            Log.e("TAG", "onTextChanged: 11122 ")
            requiredNoAdditionalCrossings =
                if (charSequence.toString().isNotEmpty() && charSequence.toString()
                        .toInt() > 0 && charSequence.toString().toInt() <= 50
                ) {
                    val charge = data?.chargingRate?.toDouble()
                    if (charge != null) {
                        Log.e("TAG", "onTextChanged: --> " + charge)
                        data?.additionalCrossingCount =
                            binding.numberAdditionalCrossings.editText.text.toString().toInt()

                        val mUnSettledTrips = data?.unsettledTripChange
                        val additionalCrossingsCount = data?.additionalCrossingCount
                        var recentCrossingsAmount = 0.0
                        var additionalCrossingsAmount = 0.0
                        if (mUnSettledTrips != null && mUnSettledTrips > 0) {
                            recentCrossingsAmount = charge * mUnSettledTrips
                        }
                        if (additionalCrossingsCount != null && additionalCrossingsCount > 0) {
                            additionalCrossingsAmount = charge * additionalCrossingsCount
                        }
                        Log.e(
                            "TAG",
                            "onTextChanged: --> recentCrossingsAmount -> " + recentCrossingsAmount
                        )
                        Log.e(
                            "TAG",
                            "onTextChanged: --> additionalCrossingsAmount -> " + additionalCrossingsAmount
                        )

                        val total = recentCrossingsAmount + additionalCrossingsAmount
                        Log.e("TAG", "onTextChanged: --> total -> " + total)

                        binding.recentCrossing.text = getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            recentCrossingsAmount
                        )
                        data?.totalAmount = total
                        data?.additionalCharge = additionalCrossingsAmount

                        binding.paymentCrossing.text = getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            data?.additionalCharge
                        )

                        binding.totalAmount.text = getString(R.string.currency_symbol) + String.format(
                            "%.2f",
                            data?.totalAmount
                        )
                        Log.e("TAG", "onTextChanged: --> totalAmount -> " + data?.totalAmount)

                    }
                    binding.numberAdditionalCrossings.removeError()
                    true
                } else {
                    if (charSequence.toString().trim().isEmpty()) {
                        binding.numberAdditionalCrossings.removeError()
                    } else if (charSequence.toString().toInt() == 0 || charSequence.toString()
                            .toInt() > 50
                    ) {
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
            (binding.totalAmount.text.toString().trim().replace("£", "")
                .replace("$", "").replace(" ", "").toDouble() > 0 && requiredNoAdditionalCrossings)
    }

    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
        binding.backToMainMenu.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {

            R.id.btnNext -> {
                Log.e("TAG", "init: navFlowFrom "+navFlowFrom )
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
                bundle.putDouble(
                    Constants.DATA,
                    binding.totalAmount.text.toString().trim()
                        .replace(getString(R.string.currency_symbol), "").replace("$", "")
                        .replace(getString(R.string.currency_symbol), "").replace(" ", "")
                        .replace(".", "").toDouble()
                )
                bundle.putParcelable(Constants.NAV_DATA_KEY, data as Parcelable?)
                if(edit_summary){
                    findNavController().navigate(R.id.action_additionalCrossingsFragment_to_crossingCheckAnswersFragment,bundle)
                }else {

                    findNavController().navigate(
                        R.id.action_additionalCrossingsFragment_to_crossingRecieptFragment,
                        bundle
                    )
                }
            }

            R.id.backToMainMenu -> {
                requireActivity().startNewActivityByClearingStack(LandingActivity::class.java)
                requireActivity().finish()
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
            bundle.putString(Constants.NAV_FLOW_FROM, navFlowFrom)
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