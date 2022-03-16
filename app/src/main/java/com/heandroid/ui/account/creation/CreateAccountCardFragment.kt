package com.heandroid.ui.account.creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountResponseModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.databinding.FragmentCreateAccountCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.addExpriryListner
import com.heandroid.utils.extn.loadSetting
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class CreateAccountCardFragment : BaseFragment<FragmentCreateAccountCardBinding>(),View.OnClickListener {

    private val viewModel : CreateAccountViewModel by viewModels()
    private var loader: LoaderDialog?=null


    private var model : CreateAccountRequestModel?=null
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateAccountCardBinding = FragmentCreateAccountCardBinding.inflate(inflater,container,false)
    override fun init() {
        model=arguments?.getParcelable("data")
        binding.webview.loadSetting("file:///android_asset/NMI.html")
    }
    override fun initCtrl() {
        binding.apply {
            tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
            tieExpiryDate.addExpriryListner()
            btnPay.setOnClickListener(this@CreateAccountCardFragment)
            webview.webChromeClient=consoleListener
        }
    }

    override fun observer() {
        observe(viewModel.createAccount,::handleCreateAccountResponse)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPay -> {
                loader = LoaderDialog()
                loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
                viewModel.createAccount(model)
            }
        }
    }

    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url : String = consoleMessage.message()
            val check : Boolean = "tokenType" in url
            if (check){
                Toast.makeText(context,url, Toast.LENGTH_LONG).show()
                binding.webview.gone()
                binding.mcvContainer.visible()
                val responseModel : CardResponseModel = Gson().fromJson(consoleMessage.message(),CardResponseModel::class.java)
                model?.creditCExpMonth = responseModel.card.exp.substring(0,1)
                model?.creditCExpYear = responseModel.card.exp.substring(2,3)
                model?.maskedNumber = responseModel.card.number
                model?.creditCardNumber = responseModel.token
                model?.creditCardType = responseModel.card.type
                model?.securityCode = responseModel.card.hash
                val fullName : List<String?>? =responseModel.check.name?.split(" ")
                when(fullName?.size){

                    1 -> { model?.cardFirstName= fullName[0]
                           model?.cardMiddleName=""
                           model?.cardLastName=""
                         }

                    2 -> { fullName.get(0).also { model?.cardFirstName = it }
                           model?.cardMiddleName=""
                           fullName.get(1).also { model?.cardLastName = it }
                        }
                    3 -> { fullName.get(0).also { model?.cardFirstName = it }
                           fullName.get(1).also { model?.cardMiddleName = it }
                           fullName.get(2).also { model?.cardLastName = it }
                         }

                    else ->{
                        model?.cardFirstName=""
                        model?.cardMiddleName=""
                        model?.cardLastName=""
                    }

                }

            }
            return true
        }


    }

    private fun handleCreateAccountResponse(status : Resource<CreateAccountResponseModel?>?){
        try {
            loader?.dismiss()
            when(status){
                is Resource.Success -> {
                    val bundle = Bundle()
                    bundle.putParcelable("data",status.data)
                    findNavController().navigate(R.id.action_cardFragment_to_successfulFragment,bundle)
                }

                is Resource.DataError ->{ showError(binding.root,status.errorMsg) }
                else -> {}
            }

        }catch (e: Exception){

        }
    }


}
