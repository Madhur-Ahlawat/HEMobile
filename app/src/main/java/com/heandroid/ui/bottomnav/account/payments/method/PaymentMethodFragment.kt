package com.heandroid.ui.bottomnav.account.payments.method

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.payment.*
import com.heandroid.databinding.FragmentPaymentMethodBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception


@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener, (Boolean?,Int?,CardListResponseModel?) -> Unit {

    private val viewModel : PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var isDefaultDeleted=false
    private var cardsList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position : Int? =0

    private var defaultCardModel: CardListResponseModel? =null
    private var defaultConstantCardModel: CardListResponseModel? =null


    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<LinearLayout>(R.id.tabLikeButtonsLayout).visible()
    }


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentPaymentMethodBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager,"")
        viewModel.saveCardList()
    }


    override fun initCtrl() {
        binding.apply {
            btnAdd.setOnClickListener(this@PaymentMethodFragment)
            btnDefault.setOnClickListener(this@PaymentMethodFragment)
            btnDelete.setOnClickListener(this@PaymentMethodFragment)
            rgPayment.setOnCheckedChangeListener(this@PaymentMethodFragment)
        }
    }

    override fun observer() {
        lifecycleScope.launch {
            observe(viewModel.savedCardList,::handleSaveCardResponse)
            observe(viewModel.deleteCard,::handleDeleteCardResponse)
            observe(viewModel.defaultCard,::handleDefaultCardResponse)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
          R.id.btnAdd -> {
              when(binding.rgPayment.checkedRadioButtonId){
                R.id.rbAddCard -> { findNavController().navigate(R.id.action_paymentMethodFragment_to_paymentMethodCardFragment) }
                R.id.rbDirectDebit ->{ showError(binding.root,"Under Developement") }
              }
          }
          R.id.btnDefault -> {
              loader?.show(requireActivity().supportFragmentManager,"")
              if(defaultCardModel?.bankAccount==true)
              viewModel.editDefaultCard(PaymentMethodEditModel(cardType = defaultCardModel?.bankAccountType?:"", easyPay = "Y", paymentType = "ach", primaryCard ="Y",rowId=defaultCardModel?.rowId?:"" ))
              else viewModel.editDefaultCard(PaymentMethodEditModel(cardType = defaultCardModel?.cardType?:"", easyPay = "Y", paymentType = "card", primaryCard ="Y",rowId=defaultCardModel?.rowId?:"" ))

          }
          R.id.btnDelete ->{
              if(defaultCardModel?.primaryCard==false) {
                  loader?.show(requireActivity().supportFragmentManager,"")
                  viewModel.deleteCard(PaymentMethodDeleteModel(defaultCardModel?.rowId))
              } else {
                  showError(binding.root,"Primary card cann't be deleted")
              }
          }
        }
    }


    private fun handleSaveCardResponse(status: Resource<PaymentMethodResponseModel?>?){
        try {
            binding.clMain.visible()
            loader?.dismiss()
            when(status){
                is Resource.Success ->{
                    cardsList?.clear()
                    cardsList=status.data?.creditCardListType?.cardsList
                    defaultCardModel =status.data?.creditCardListType?.cardsList?.filter { it?.primaryCard==true }?.get(0)
                    defaultConstantCardModel=defaultCardModel
                    if(defaultCardModel==null){
                        binding.tvDefaultLabel.gone()
                        binding.rbDefaultMethod.gone()
                        binding.viewDefault.gone()
                    }
                    else {

                        binding.tvDefaultLabel.visible()
                        binding.rbDefaultMethod.visible()
                        binding.viewDefault.visible()
                        val spannableString = if(defaultCardModel?.bankAccount == true) SpannableString(defaultCardModel?.bankAccountType+"\n"+ defaultCardModel?.bankAccountNumber)
                        else SpannableString(defaultCardModel?.cardType+"\n"+ defaultCardModel?.cardNumber)
                        spannableString?.setSpan( ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.txt_disable)), spannableString.length-(defaultCardModel?.cardNumber?.length?:0), spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        binding.rbDefaultMethod.text = spannableString
                        binding.rbDefaultMethod.isChecked=true

                        cardsList?.remove(defaultCardModel)
                        binding.enable=false
                    }

                    binding.rvOtherPayment.layoutManager=LinearLayoutManager(requireActivity())
                    binding.rvOtherPayment.adapter=PaymentCardAdapter(requireActivity(),cardsList,this)
                }
                is Resource.DataError ->{ showError(binding.root,status.errorMsg) }
            }
        }catch (e: Exception){

        }
    }

    private fun handleDefaultCardResponse(status: Resource<PaymentMethodEditResponse?>?){
        try {
            loader?.dismiss()
            when(status){
                is Resource.Success ->{
                    requireActivity().showToast(status.data?.message)
                    findNavController().navigate(R.id.paymentMethodFragment)
                }
                is Resource.DataError ->{ showError(binding.root,status.errorMsg) }
            }
        }catch (e: Exception){ }
    }


    private fun handleDeleteCardResponse(status: Resource<PaymentMethodDeleteResponseModel?>?){
        try {
            loader?.dismiss()
            when(status){
                is Resource.Success -> {
                    if(status.data?.statusCode?.equals("500")==true){
                        showError(binding.root,status.data.message)
                        return
                    }

                    if(isDefaultDeleted) {
                        binding.tvDefaultLabel.gone()
                        binding.rbDefaultMethod.gone()
                        binding.viewDefault.gone()
                        isDefaultDeleted=false
                    }
                    else{
                        cardsList?.removeAt(position?:0)
                        binding.rvOtherPayment.adapter?.notifyItemRemoved(position?:0)
                    }
                }
                is Resource.DataError ->{ showError(binding.root,status.errorMsg) }
            }
        }catch (e: Exception){

        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {


        when(group?.checkedRadioButtonId){

            R.id.rbDefaultMethod -> {
                isDefaultDeleted=true
                binding.btnDelete.visible()
                binding.btnDefault.visible()
                binding.enable=false
                binding.btnAdd.gone()
                if(binding.rbDefaultMethod.isChecked) {
                    defaultCardModel=defaultConstantCardModel
                    loadCheck()
                }
            }
            R.id.rbAddCard -> {
                binding.btnAdd.visible()
                binding.btnDelete.gone()
                binding.btnDefault.gone()
                if(binding.rbAddCard.isChecked) loadCheck()

            }
            R.id.rbDirectDebit ->{
                binding.btnAdd.visible()
                binding.btnDelete.gone()
                binding.btnDefault.gone()
                if(binding.rbDirectDebit.isChecked) loadCheck()
            }
        }



    }

    private fun loadCheck(){
        if(cardsList!=null){
            for(i in cardsList?.indices!!){
                cardsList?.get(i)?.check=false
            }
            binding.rvOtherPayment.adapter?.notifyItemRangeChanged(0,cardsList?.size?:0)
        }
    }

    override fun invoke(check: Boolean?,position: Int?,model: CardListResponseModel?) {

        this.position=position
        isDefaultDeleted=false

        binding.rgPayment.clearCheck()

        if(cardsList?.get(position?:0)?.check != false){
            binding.btnDefault.visible()
            binding.btnAdd.gone()
            binding.btnDelete.visible()
        }

        else {
            binding.btnDelete.gone()
            binding.btnDefault.gone()
        }

        binding.enable=true

        this.defaultCardModel=model


    }
}