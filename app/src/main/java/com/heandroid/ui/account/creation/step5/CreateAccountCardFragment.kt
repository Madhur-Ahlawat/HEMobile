package com.heandroid.ui.account.creation.step5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.account.CreateAccountResponseModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.data.repository.auth.CreateAccountRespository
import com.heandroid.databinding.FragmentCreateAccountCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.DATA
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class CreateAccountCardFragment : BaseFragment<FragmentCreateAccountCardBinding>(), View.OnClickListener {

    private val viewModel: CreateAccountPaymentViewModel by viewModels()
    private var loader: LoaderDialog? = null

    private var model: CreateAccountRequestModel? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreateAccountCardBinding = FragmentCreateAccountCardBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        loader?.show(requireActivity().supportFragmentManager,"")

        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 5, 5)
        binding.webview.loadSetting("file:///android_asset/NMI.html")
    }

    override fun initCtrl() {
        binding.apply {
            btnPay.setOnClickListener(this@CreateAccountCardFragment)
            webview.webViewClient = progressListener
            webview.webChromeClient = consoleListener
        }
    }

    override fun observer() {
        observe(viewModel.createAccount, ::handleCreateAccountResponse)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnPay -> {
                when(model?.planType){
                    Constants.PAYG ->  model?.smsOption=null
                }
                loader?.show(requireActivity().supportFragmentManager,"")
                viewModel.createAccount(model)
            }
        }
    }

    private val progressListener = object : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            view?.loadUrl("file:///android_asset/NMI.html")
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            loader?.dismiss()
        }

    }

    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url: String = consoleMessage.message()
            val check: Boolean = "tokenType" in url
            if (check) {
                if (arguments?.getInt(Constants.PERSONAL_TYPE) == Constants.PERSONAL_TYPE_PAY_AS_U_GO) binding.tvPaymentAmount.invisible()
                else binding.tvPaymentAmount.visible()
               // Toast.makeText(context, url, Toast.LENGTH_LONG).show()

                binding.webview.gone()
                binding.mcvContainer.visible()
                val responseModel: CardResponseModel = Gson().fromJson(consoleMessage.message(), CardResponseModel::class.java)
                Log.e("cardDetails",responseModel.toString())
                model?.creditCExpMonth = responseModel.card.exp.substring(0, 2)
                model?.creditCExpYear = "/"+responseModel.card.exp.substring(2, 4)
                model?.maskedNumber = responseModel.card.number
                model?.creditCardNumber = responseModel.token
                model?.creditCardType = responseModel.card.type
                model?.securityCode = responseModel.card.hash

                val fullName: List<String?>? = responseModel.check.name?.split(" ")
                when (fullName?.size) {

                    1 -> {
                        model?.cardFirstName = fullName[0]
                        model?.cardMiddleName = " "
                        model?.cardLastName = " "
                    }

                    2 -> {
                        fullName[0].also { model?.cardFirstName = it }
                        model?.cardMiddleName = " "
                        fullName[1].also { model?.cardLastName = it }
                    }

                    3 -> {
                        fullName[0].also { model?.cardFirstName = it }
                        fullName[1].also { model?.cardMiddleName = " $it" }
                        fullName[2].also { model?.cardLastName = " $it" }
                    }

                    else -> {
                        model?.cardFirstName = ""
                        model?.cardMiddleName = ""
                        model?.cardLastName = ""
                    }

                }
                binding.model = model
            }
            return true
        }
    }

    private fun handleCreateAccountResponse(status: Resource<CreateAccountResponseModel?>?) {
        try {
            loader?.dismiss()
            when (status) {
                is Resource.Success -> {
                    status.data?.accountType = model?.accountType
                    // Add Payment Method
                    val bundle = Bundle()
                    bundle.putParcelable("response", status.data)
                    findNavController().navigate(R.id.action_cardFragment_to_successfulFragment, bundle)
                }

                is Resource.DataError -> { showError(binding.root, status.errorMsg) }
                else -> {}
            }

        } catch (e: Exception) {
        }
    }


}
