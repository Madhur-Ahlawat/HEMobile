package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.makeoneofpayment.OneOfPaymentModelResponse
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.data.model.payment.PaymentReceiptDeliveryTypeSelectionRequest
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentSuccessfulBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.ui.makeoneoffpayment.MakeOneOfPaymentViewModel
import com.heandroid.utils.common.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody

@AndroidEntryPoint
class MakeOffPaymentSuccessfulFragment : BaseFragment<FragmentMakeOffPaymentSuccessfulBinding>() {

    private val viewModel: MakeOneOfPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var mScreeType = 0
    private var list: MutableList<VehicleResponse> = ArrayList()
    private var mEmail = ""
    private var mPaymentResp: OneOfPaymentModelResponse? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOffPaymentSuccessfulBinding =
        FragmentMakeOffPaymentSuccessfulBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        mEmail = arguments?.getString(Constants.EMAIL)!!
        list = arguments?.getParcelableArrayList(Constants.DATA)!!
        mPaymentResp = arguments?.getParcelable(Constants.ONE_OF_PAYMENTS_PAY_RESP)
        loader?.show(requireActivity().supportFragmentManager, "")
        val mSelection = PaymentReceiptDeliveryTypeSelectionRequest(mPaymentResp?.refrenceNumber!!,arguments?.getString(Constants.OPTIONS_TYPE,"Email")!!)
        viewModel.whereToReceivePaymentReceipt(mSelection)

    }

    override fun initCtrl() {}

    override fun observer() {
        observe(viewModel.whereToReceivePaymentReceipt, ::receipt)
    }

    private fun receipt(resource: Resource<ResponseBody?>?) {
        when (resource) {
            is Resource.Success -> {
                resource.data?.let {
                    loader?.dismiss()
                    it?.let {
                        Logg.logging(
                            "testing",
                            " MakeOffPaymentSuccessfulFragment success it  $it"
                        )

                    }
                }
            }
            is Resource.DataError -> {
                Logg.logging("testing", " MakeOffPaymentSuccessfulFragment error called")

                loader?.dismiss()
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
        }

    }

}