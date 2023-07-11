package com.conduent.nationalhighways.ui.auth.suspended

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltBinding
import com.conduent.nationalhighways.ui.auth.adapter.SuspendPaymentMethodAdapter
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentCardAdapter
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSuspendSelectPaymentFragment : BaseFragment<FragmentAccountSuspendHaltBinding>(),
    View.OnClickListener, SuspendPaymentMethodAdapter.paymentMethodSelectCallBack {
    private lateinit var suspendPaymentMethodAdapter: SuspendPaymentMethodAdapter
    private var paymentList: MutableList<CardListResponseModel?>? = ArrayList()
    private var lowBalance: Boolean = false
    private var cardSelection: Boolean = false
    private val viewModel: PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var position: Int = 0
    private var isViewCreated: Boolean = false
    private var personalInformation: PersonalInformation? = null
    private var currentBalance:String=""


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltBinding =
        FragmentAccountSuspendHaltBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.saveCardList()
        }

        isViewCreated = false


        binding.btnContinue.setOnClickListener(this)
        binding.btnAddNewPaymentMethod.setOnClickListener(this)
        binding.btnAddNewPayment.setOnClickListener(this)
        binding.lowBalance.editText.addTextChangedListener(GenericTextWatcher())

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""

    }
    override fun init() {


        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvPaymentMethods.layoutManager = linearLayoutManager

        suspendPaymentMethodAdapter =
            SuspendPaymentMethodAdapter(requireContext(), paymentList, this)
        binding.rvPaymentMethods.adapter = suspendPaymentMethodAdapter

        binding.lowBalance.setText("£10.00")


    }
    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.savedCardList, ::handleSaveCardResponse)

        }
    }

    inner class GenericTextWatcher() : TextWatcher {

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



                val text = binding.lowBalance.getText().toString().trim()
                val updatedText = text.replace("£", "")

                if (updatedText.isNotEmpty()) {
                    val str: String = updatedText.substringBeforeLast(".")
                    lowBalance = if (str.length < 8) {
                        if (updatedText.toDouble() < 10) {
                            binding.lowBalance.setErrorText(getString(R.string.str_top_up_amount_must_be_more))
                            false

                        } else {
                            binding.lowBalance.removeError()
                            true
                        }
                    } else {
                        binding.lowBalance.setErrorText(getString(R.string.str_top_up_amount_must_be_8_characters))
                        false
                    }

                } else {
                    binding.lowBalance.removeError()
                }
                binding.lowBalance.editText.removeTextChangedListener(this)
                if (updatedText.isNotEmpty())
                    binding.lowBalance.setText("£$updatedText")
                Selection.setSelection(
                    binding.lowBalance.getText(),
                    binding.lowBalance.getText().toString().length
                )
                binding.lowBalance.editText.addTextChangedListener(this)



            checkButton()
            checkNewPaymentMethodButton()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButton() {
        binding.btnContinue.isEnabled = lowBalance && cardSelection

    }
    private fun checkNewPaymentMethodButton(){
        binding.btnAddNewPayment.isEnabled=lowBalance
        binding.btnAddNewPaymentMethod.isEnabled=lowBalance


    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")

                val bundle = Bundle()
                bundle.putDouble(Constants.PAYMENT_TOP_UP, topUpAmount.toDouble())
                bundle.putInt(Constants.POSITION, position)
                bundle.putParcelable(Constants.PERSONALDATA,personalInformation)
                bundle.putString(Constants.CURRENTBALANCE,currentBalance)
                bundle.putParcelableArrayList(Constants.DATA, paymentList as ArrayList)
                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_accountSuspendedFinalPayFragment,
                    bundle
                )
            }

            R.id.btnAddNewPaymentMethod -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, topUpAmount.toDouble())
                bundle.putString(Constants.SUSPENDED, Constants.SUSPENDED)
                bundle.putParcelable(Constants.PERSONALDATA,personalInformation)
                bundle.putString(Constants.CURRENTBALANCE,currentBalance)
                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_nmiPaymentFragment,
                    bundle
                )
            }

            R.id.btnAddNewPayment -> {
                val topUpAmount = binding.lowBalance.getText().toString().trim().replace("£", "")
                val bundle = Bundle()
                bundle.putDouble(Constants.DATA, topUpAmount.toDouble())
                bundle.putString(Constants.SUSPENDED, Constants.SUSPENDED)
                bundle.putParcelable(Constants.PERSONALDATA,personalInformation)
                bundle.putString(Constants.CURRENTBALANCE,currentBalance)
                findNavController().navigate(
                    R.id.action_accountSuspendedPaymentFragment_to_nmiPaymentFragment,
                    bundle
                )
            }
        }
    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
               // paymentList?.clear()
                paymentList = status.data?.creditCardListType?.cardsList
                if (paymentList?.isNotEmpty() == true) {
                    suspendPaymentMethodAdapter.updateList(paymentList)
                    binding.rvPaymentMethods.visible()
                    binding.btnContinue.visible()

                    binding.noCardFoundLayout.gone()

                    binding.btnAddNewPayment.gone()
                    binding.btnAddNewPaymentMethod.visible()

                } else {
                    binding.btnAddNewPayment.visible()
                    binding.btnAddNewPaymentMethod.gone()
                    binding.noCardFoundLayout.visible()
                    binding.rvPaymentMethods.gone()
                    binding.btnContinue.gone()
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    override fun paymentMethodCallback(position: Int) {
        this.position = position
        suspendPaymentMethodAdapter?.notifyDataSetChanged()
        cardSelection = paymentList?.get(position)?.isSelected == true
        lowBalance = true
        checkButton()

    }


}