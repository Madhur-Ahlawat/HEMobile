package com.heandroid.ui.bottomnav.account.payments.method

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.heandroid.R
import com.heandroid.data.model.auth.login.LoginResponse
import com.heandroid.data.model.payment.*
import com.heandroid.databinding.FragmentPaymentMethodBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.showToast
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.logging.Handler

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>(), View.OnClickListener, RadioGroup.OnCheckedChangeListener, (Boolean,Int,CardListResponseModel) -> Unit {

    private val viewModel : PaymentMethodViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var rowId: String?=null
    private var isDefaultDeleted=false
    private var cardsList: MutableList<CardListResponseModel?>? = ArrayList()
    private var position : Int? =0

    private var defaultCardModel: CardListResponseModel? =null


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
              viewModel.editDefaultCard(PaymentMethodEditModel(cardType = defaultCardModel?.bankAccountType?:"", easyPay = "Y", paymentType = defaultCardModel?.bankAccountType?:"", primaryCard ="Y",rowId=defaultCardModel?.rowId?:"" ))
          }
          R.id.btnDelete ->{
              loader?.show(requireActivity().supportFragmentManager,"")
              viewModel.deleteCard(PaymentMethodDeleteModel(rowId))
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
                    if(defaultCardModel!=null){
                        binding.tvDefaultLabel.gone()
                        binding.rbDefaultMethod.gone()
                        binding.viewDefault.gone()
                    }
                    else {
                        val spannableString = SpannableString(defaultCardModel?.bankAccountType+"\n"+ defaultCardModel?.bankAccountNumber)
                        spannableString.setSpan( ForegroundColorSpan(Color.LTGRAY), spannableString.length-16, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        binding.rbDefaultMethod.text = spannableString

                        cardsList?.remove(defaultCardModel)
                        binding.rbDefaultMethod.isChecked=true
                        binding.rbDefaultMethod.tag=defaultCardModel?.rowId
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

                    if(isDefaultDeleted){
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {


        when(group?.checkedRadioButtonId){
            R.id.rbDefaultMethod -> {

                isDefaultDeleted=true
                rowId=binding.rbDefaultMethod.tag.toString()
                binding.btnDelete.visible()
                binding.btnDefault.gone()
                binding.btnAdd.gone()
            }
            R.id.rbAddCard -> {
                binding.btnAdd.visible()
                binding.btnDelete.gone()
                binding.btnDefault.gone()
            }
            R.id.rbDirectDebit ->{
                binding.btnAdd.visible()
                binding.btnDelete.gone()
                binding.btnDefault.gone()
            }
        }
        for(i in cardsList?.indices!!){
            cardsList?.get(i)?.check=false
        }
        binding.rvOtherPayment.adapter?.notifyDataSetChanged()
    }

    override fun invoke(check: Boolean,position: Int,model: CardListResponseModel) {
        if(check){
            defaultCardModel=model
            this.position=position
            isDefaultDeleted=false
            binding.rgPayment.clearCheck()

            binding.btnDefault.visible()
            binding.btnAdd.gone()
            binding.btnDelete.visible()
            android.os.Handler(Looper.getMainLooper()).postDelayed(Runnable {
                  binding.rvOtherPayment.adapter?.notifyDataSetChanged()
            },200)
        }
        else {
            binding.btnDelete.gone()
            binding.btnDefault.gone()
        }

    }
}