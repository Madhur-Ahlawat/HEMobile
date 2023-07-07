package com.conduent.nationalhighways.ui.auth.suspended

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
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

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuspendHaltReopenedBinding= FragmentAccountSuspendHaltReopenedBinding.inflate(inflater,container,false)



    override fun initCtrl() {
        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA)!=null){
            responseModel=arguments?.getParcelable<CardResponseModel>(Constants.DATA)

            binding.tvAccountSuspended.text=getString(R.string.success)
            binding.tvYouWillNeedToPay.text=getString(R.string.str_you_have_successfully_added_new_card)
            binding.cardView.visibility=View.VISIBLE

            if (responseModel?.card?.type.equals("visa", true)) {
                binding.ivCardType.setImageResource(R.drawable.visablue)
            } else if (responseModel?.card?.type.equals("maestro", true)) {
                binding.ivCardType.setImageResource(R.drawable.maestro)

            } else {
                binding.ivCardType.setImageResource(R.drawable.mastercard)

            }
            val htmlText = Html.fromHtml(responseModel?.card?.type?.uppercase()+"<br>"+responseModel?.card?.number)

            binding.tvSelectPaymentMethod.text = htmlText
            binding.tvYouWillAlsoNeed.text=getString(R.string.str_you_have_less_than,"5.00")


        }else{
            binding.cardView.visibility=View.GONE
            binding.tvYouWillAlsoNeed.text=getString(R.string.str_you_have_less_than,"5.00")

            binding.tvYouWillNeedToPay.text=getString(R.string.str_we_have_sent_confirmation,
                NewCreateAccountRequestModel.emailAddress)
        }
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