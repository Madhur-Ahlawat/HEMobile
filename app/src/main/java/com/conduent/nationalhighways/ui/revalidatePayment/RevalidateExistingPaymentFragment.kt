package com.conduent.nationalhighways.ui.revalidatePayment

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.conduent.nationalhighways.data.model.manualtopup.PaymentWithExistingCardModel
import com.conduent.nationalhighways.data.model.payment.CardListResponseModel
import com.conduent.nationalhighways.data.model.payment.PaymentMethodDeleteResponseModel
import com.conduent.nationalhighways.data.model.profile.PersonalInformation
import com.conduent.nationalhighways.databinding.FragmentRevalidateExistingPaymentBinding
import com.conduent.nationalhighways.ui.auth.suspended.ManualTopUpViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RevalidateExistingPaymentFragment : BaseFragment<FragmentRevalidateExistingPaymentBinding>() {
    private var personalInformation: PersonalInformation? = null
    private var paymentList: ArrayList<CardListResponseModel> = ArrayList()
    private var position: Int = 0
    private val manualTopUpViewModel: ManualTopUpViewModel by viewModels()

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRevalidateExistingPaymentBinding =
        FragmentRevalidateExistingPaymentBinding.inflate(inflater, container, false)

    override fun init() {
        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable(Constants.PERSONALDATA)
        }
        if (arguments?.getParcelableArrayList<CardListResponseModel>(Constants.DATA) != null) {
            paymentList =
                arguments?.getParcelableArrayList(Constants.DATA) ?:ArrayList()
        }
        position = arguments?.getInt(Constants.POSITION, 0) ?: 0


    }

    override fun initCtrl() {

    }

    override fun observer() {
        lifecycleScope.launch {
            observe(
                manualTopUpViewModel.paymentWithExistingCard,
                ::handlePaymentWithExistingCardResponse
            )
        }
    }


    private fun handlePaymentWithExistingCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?) {
        dismissLoaderDialog()
        when (status) {
            is Resource.Success -> {

            }

            is Resource.DataError -> {
                if (checkSessionExpiredOrServerError(status.errorModel)
                ) {
                    displaySessionExpireDialog(status.errorModel)
                } else {
                    if (status.errorModel?.message.equals("Something went wrong. Try again later")) {
                    } else {
//                        redirectToTryAgainPage()
                    }
                }
            }

            else -> {
            }
        }
    }


    private fun payWithExistingCard() {
//        val model = PaymentWithExistingCardModel(
//            addressline1 = personalInformation?.addressLine1.toString().replace(" ", ""),
//            addressline2 = personalInformation?.addressLine2.toString().replace(" ", ""),
//            transactionAmount = "",
//            cardType = "",
//            cardNumber = "",
//            cvv = "",
//            rowId = paymentList[position].rowId,
//            saveCard = "",
//            useAddressCheck = "N",
//            firstName = paymentList[position].firstName,
//            middleName = paymentList[position].middleName,
//            lastName = paymentList[position].lastName,
//            paymentType = "",
//            primaryCard = "",
//            maskedCardNumber = "",
//            easyPay = "",
//            cavv = paymentSuccessResponse?.cavv,
//            xid = paymentSuccessResponse?.xid,
//            threeDsVersion = paymentSuccessResponse?.threeDsVersion,
//            directoryServerId = paymentSuccessResponse?.directoryServerId,
//            cardHolderAuth = paymentSuccessResponse?.cardHolderAuth,
//            eci = paymentSuccessResponse?.eci
//
//
//        )
//        Log.d("paymentRequest", Gson().toJson(model))
//        manualTopUpViewModel.paymentWithExistingCard(model)
    }


}