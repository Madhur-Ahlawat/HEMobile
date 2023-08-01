package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.OnKeyPressListener
import com.conduent.apollo.models.KeysModel
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAmountKeyPadBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AmountKeyPadFragment : BaseFragment<FragmentAmountKeyPadBinding>(), View.OnClickListener {

    private var replenishedAmount: String? = "£0.00"
    private var lowBalanceClick: String = ""
    private var lowBalanceAmount:String=""
    private var topUpAmount:String=""



    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAmountKeyPadBinding = FragmentAmountKeyPadBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        binding.btnContinueReload.setOnClickListener(this)
        lowBalanceClick = arguments?.getString(Constants.LOW_BALANCE) ?: ""

        lowBalanceAmount=arguments?.getString(Constants.LOW_BALANCE_AMOUNT)?:""
        topUpAmount=arguments?.getString(Constants.TOP_UP_AMOUNT)?:""

        val value = Utils.convertToPoundFormat(replenishedAmount?.replace("£", "")!!).toString()

        binding.txtPaymentAmount.text = value

        binding.cmKeypad.currentValue = value.replace("£", "").replace(".00", "")
        binding.cmKeypad.bindView(binding.txtPaymentAmount)

        binding.txtPaymentAmount.text=binding.txtPaymentAmount.text.toString().replace("$","£")


        var isPreviousValueCleared = false

        binding.cmKeypad.setKeyPressListener(object : OnKeyPressListener {
            override fun onKeyPressed(data: KeysModel) {
                if (!isPreviousValueCleared && !data.text.equals(".")) {
                    binding.txtPaymentAmount.text = ""
                    binding.cmKeypad.currentValue = ""
                    isPreviousValueCleared = true

                }
                binding.txtPaymentAmount.text=binding.txtPaymentAmount.text.toString().replace("$","£")

            }

            override fun onMaxValueReached(maxValue: Float) {
            }
        })


    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btn_continue_reload -> {
                if (lowBalanceClick == Constants.LOW_BALANCE) {
                    setFragmentResult(
                        Constants.LOW_BALANCE,
                        bundleOf(
                            Constants.LOW_BALANCE to binding.txtPaymentAmount.text.toString().trim(),
                            Constants.TOP_UP_BALANCE to topUpAmount
                        )

                    )

                } else {
                    setFragmentResult(
                        Constants.TOP_UP_BALANCE,
                        bundleOf(
                            Constants.TOP_UP_BALANCE to binding.txtPaymentAmount.text.toString()
                                .trim(),
                            Constants.LOW_BALANCE_AMOUNT to lowBalanceAmount
                        )
                    )

                }


                findNavController().popBackStack()

            }
        }
    }

}