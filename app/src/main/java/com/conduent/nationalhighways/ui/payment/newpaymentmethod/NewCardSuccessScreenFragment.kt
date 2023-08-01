package com.conduent.nationalhighways.ui.payment.newpaymentmethod


import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.payment.CardResponseModel
import com.conduent.nationalhighways.databinding.FragmentNewCardSuccessScreenBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants

class NewCardSuccessScreenFragment : BaseFragment<FragmentNewCardSuccessScreenBinding>(),
    View.OnClickListener {

    private var flow: String = ""
    private var responseModel: CardResponseModel? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewCardSuccessScreenBinding =
        FragmentNewCardSuccessScreenBinding.inflate(inflater, container, false)


    override fun initCtrl() {
        flow = arguments?.getString(Constants.CARD_IS_ALREADY_REGISTERED) ?: ""

        if (arguments?.getParcelable<CardResponseModel>(Constants.DATA) != null) {
            responseModel = arguments?.getParcelable<CardResponseModel>(Constants.DATA)

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

            binding.maximumVehicleAdded.text=getString(R.string.success)
            binding.textMaximumVehicle.text=getString(R.string.str_you_have_successfully_added_card)

        }

        if (flow==Constants.CARD_IS_ALREADY_REGISTERED){
            binding.warningIcon.setImageResource(R.drawable.warningicon)
            binding.maximumVehicleAdded.text=getString(R.string.the_card_you_are_trying)
            binding.textMaximumVehicle.text=getString(R.string.str_do_you_want_to_another_card)
            binding.btnContinue.text=getString(R.string.str_add_another_card)

        }
    }

    override fun init() {
        binding.btnContinue.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnContinue -> {

            }

            R.id.cancel_btn -> {

            }
        }
    }

}