package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.databinding.FragmentDeletePaymentMethodBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.payments.method.PaymentMethodViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePaymentMethodFragment : BaseFragment<FragmentDeletePaymentMethodBinding>(),
    View.OnClickListener {

    private val viewModel: PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var paymentList: CardListResponseModel? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletePaymentMethodBinding =
        FragmentDeletePaymentMethodBinding.inflate(inflater, container, false)

    override fun init() {
    }

    override fun initCtrl() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)


        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)

        if (arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA) != null) {
            paymentList = arguments?.getParcelable<CardListResponseModel>(Constants.PAYMENT_DATA)

        }

    }

    override fun observer() {
        observe(viewModel.deleteCard, ::handleDeleteCardResponse)

    }

    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (status) {
            is Resource.Success -> {
                if (status.data?.statusCode?.equals("500") == true||status.data?.statusCode?.equals("1209")==true) {
                    ErrorUtil.showError(binding.root, status.data.message)
                    return
                }
                val bundle= Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,Constants.DELETE_CARD)
                findNavController().navigate(R.id.action_deletePaymentMethodFragment_to_deletePaymentMethodSuccessFragment,bundle)


            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, status.errorMsg)
            }

            else -> {
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {
                viewModel.deleteCard(PaymentMethodDeleteModel(paymentList?.rowId))
                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

            }

            R.id.cancelBtn -> {
                findNavController().popBackStack()
            }

        }
    }


}