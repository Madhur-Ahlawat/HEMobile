package com.conduent.nationalhighways.ui.auth.suspended

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.PersonalInformation
import com.conduent.nationalhighways.data.model.account.payment.PaymentSuccessResponse
import com.conduent.nationalhighways.data.model.payment.Card
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuspendHaltReopenedBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.HomeActivityMain
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSuspendReOpenFragment:BaseFragment<FragmentAccountSuspendHaltReopenedBinding>(), View.OnClickListener {
    private var responseModel: CardResponseModel?=null
    private var paymentSuccessResponse:PaymentSuccessResponse?=null
    private var personalInformation: PersonalInformation? = null
    private var currentBalance:String=""
    private var transactionId:String=""
    private var backIcon:ImageView?=null
    private var navFlow:String=""

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltReopenedBinding= FragmentAccountSuspendHaltReopenedBinding.inflate(inflater,container,false)



    override fun initCtrl() {

        transactionId= arguments?.getString(Constants.TRANSACTIONID).toString()

        if (arguments?.getParcelable<PaymentSuccessResponse>(Constants.NEW_CARD)!=null){
            paymentSuccessResponse=arguments?.getParcelable(Constants.NEW_CARD)
        }

        if (arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA) != null) {
            personalInformation =
                arguments?.getParcelable<PersonalInformation>(Constants.PERSONALDATA)

        }
        currentBalance = arguments?.getString(Constants.CURRENTBALANCE) ?: ""


        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA)!=null){
            responseModel=arguments?.getParcelable<CardResponseModel>(Constants.DATA)

            if (responseModel?.checkCheckBox == true){
                binding.cardView.visibility=View.VISIBLE

            }else{
                binding.cardView.visibility=View.GONE

            }

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText = Html.fromHtml(responseModel?.card?.type?.uppercase()+"<br>"+responseModel?.card?.number)

            binding.tvSelectPaymentMethod.text = htmlText


        }else{
            binding.cardView.visibility=View.GONE

        }
        if (currentBalance.isNotEmpty()){
            val balance = currentBalance.replace("£", "")
            val doubleBalance = balance.toDouble()
            val intBalance = doubleBalance.toInt()
            val finalCurrentBalance = 5.00 - doubleBalance
            if (finalCurrentBalance<5.00){
                binding.tvYouWillAlsoNeed.visibility=View.VISIBLE
            }else{
                binding.tvYouWillAlsoNeed.visibility=View.GONE

            }
        }


        binding.tvYouWillAlsoNeed.text=getString(R.string.str_you_have_less_than,"£5.00")

        binding.tvYouWillNeedToPay.text=getString(R.string.str_we_have_sent_confirmation,
            personalInformation?.emailAddress)

        val htmlText = Html.fromHtml("<b>"+getString(R.string.str_payment_reference)+"</b>"+"<br>"+transactionId)

        binding.tvPaymentReference.text = htmlText


        if (arguments?.getString(Constants.NAV_FLOW_KEY) != null) {
            navFlow = arguments?.getString(Constants.NAV_FLOW_KEY) ?: ""

        }
        Toast.makeText(requireContext(),navFlow,Toast.LENGTH_SHORT).show()

    }
    override fun init() {
        binding.btnTopUpNow.setOnClickListener(this)

    }
    override fun observer() {
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnTopUpNow->{

                requireActivity().startNewActivityByClearingStack(HomeActivityMain::class.java)
            }
        }
    }
}