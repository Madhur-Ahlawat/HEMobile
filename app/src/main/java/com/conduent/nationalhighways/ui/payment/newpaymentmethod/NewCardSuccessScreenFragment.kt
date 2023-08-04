package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodResponseModel
import com.conduent.nationalhighways.databinding.FragmentNewCardSuccessScreenBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewCardSuccessScreenFragment : BaseFragment<FragmentNewCardSuccessScreenBinding>(),
    View.OnClickListener {

    private var flow: String = ""
    private var responseModel: CardResponseModel? = null
    private val viewModel: PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var paymentList: CardListResponseModel? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewCardSuccessScreenBinding =
        FragmentNewCardSuccessScreenBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        flow = arguments?.getString(Constants.CARD_IS_ALREADY_REGISTERED) ?: ""
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        if (arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA) != null) {
            paymentList = arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA)

        }
        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA) != null) {
            responseModel = arguments?.getParcelable(Constants.DATA)

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText =
                Html.fromHtml(responseModel?.card?.type?.uppercase() + "<br>" + responseModel?.card?.number)

            binding.tvSelectPaymentMethod.text = htmlText

            binding.maximumVehicleAdded.text = getString(R.string.success)
            binding.textMaximumVehicle.text =
                getString(R.string.str_you_have_successfully_added_card)
            binding.cancelBtn.visibility = View.GONE

        }

        if (flow == Constants.CARD_IS_ALREADY_REGISTERED) {
            binding.warningIcon.setImageResource(R.drawable.warningicon)
            binding.maximumVehicleAdded.text = getString(R.string.the_card_you_are_trying)
            binding.textMaximumVehicle.text = getString(R.string.str_do_you_want_to_another_card)
            binding.btnContinue.text = getString(R.string.str_add_another_card)
            binding.cardView.visibility = View.INVISIBLE


        } else if (flow == Constants.DELETE_CARD) {
            binding.maximumVehicleAdded.text =
                getString(R.string.str_payment_method_deleted, paymentList?.cardNumber)
            binding.textMaximumVehicle.text = getString(R.string.str_your_default_payment_method)
            binding.textDefault.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.GONE


            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.saveCardList()
        } else if (flow == Constants.DIRECT_DEBIT_DELETE) {
            binding.maximumVehicleAdded.text =
                getString(R.string.your_direct_debit_has_been_removed, paymentList?.cardNumber)
            binding.textMaximumVehicle.text = getString(R.string.str_your_default_payment_method)
            binding.textDefault.visibility = View.VISIBLE
            binding.cancelBtn.visibility = View.GONE


            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.saveCardList()

        } else if (flow == Constants.DIRECT_DEBIT) {
            binding.maximumVehicleAdded.text = getString(R.string.str_your_new_direct)
            binding.textMaximumVehicle.text = getString(R.string.str_while_your_new_direct_debit)
            binding.textDefault.visibility = View.GONE
            binding.cardView.visibility = View.INVISIBLE

            binding.cancelBtn.visibility = View.GONE


        } else if (flow == Constants.DIRECT_DEBIT_NOT_SET_UP) {
            binding.maximumVehicleAdded.text =
                getString(R.string.str_your_direct_debit_was_not_setup)
            binding.textMaximumVehicle.text = getString(R.string.str_you_can_try)
            binding.textDefault.visibility = View.GONE
            binding.cardView.visibility = View.INVISIBLE
            binding.btnContinue.text = getString(R.string.str_try_again)


            binding.cancelBtn.visibility = View.VISIBLE
        }
    }

    override fun init() {
        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
        observe(viewModel.savedCardList, ::handleSaveCardResponse)

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                when (binding.btnContinue.text) {

                    getString(R.string.str_add_another_card) -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.NAV_FLOW_KEY, Constants.ADD_PAYMENT_METHOD)
                        bundle.putDouble(Constants.DATA, 0.0)
                        findNavController().navigate(
                            R.id.action_paymentSuccessFragment_to_nmiPaymentFragment,
                            bundle
                        )
                    }

                    getString(R.string.str_continue) -> {
                        findNavController().navigate(R.id.action_paymentSuccessFragment_to_paymentMethodFragment)

                    }

                    getString(R.string.str_try_again) -> {
                        findNavController().popBackStack()
                    }
                }

            }

            R.id.cancel_btn -> {
                if (flow == Constants.CARD_IS_ALREADY_REGISTERED || flow == Constants.DIRECT_DEBIT_NOT_SET_UP) {
                    findNavController().navigate(R.id.action_paymentSuccessFragment_to_paymentMethodFragment)
                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.creditCardListType?.cardsList?.isNotEmpty() == true) {

                    if (status.data.creditCardListType.cardsList[0]?.cardType.equals(
                            "visa",
                            true
                        )
                    ) {
                        binding.ivCardType.setImageResource(R.drawable.visablue)
                    } else if (status.data.creditCardListType.cardsList[0]?.cardType.equals(
                            "maestro",
                            true
                        )
                    ) {
                        binding.ivCardType.setImageResource(R.drawable.maestro)

                    } else {
                        binding.ivCardType.setImageResource(R.drawable.mastercard)

                    }
                    val htmlText =
                        Html.fromHtml(
                            status.data.creditCardListType.cardsList[0]?.cardType?.uppercase() + "<br>" + status.data.creditCardListType.cardsList[0]?.cardNumber
                        )

                    binding.tvSelectPaymentMethod.text = htmlText
                }


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }


}