package com.heandroid.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.heandroid.R
import com.heandroid.data.model.payment.CardModel
import com.heandroid.data.model.payment.CardResponseModel
import com.heandroid.data.model.vehicle.VehicleResponse
import com.heandroid.databinding.FragmentMakeOffPaymentCardBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.CardNumberFormatterTextWatcher
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Logg
import com.heandroid.utils.extn.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MakeOffPaymentCardFragment : BaseFragment<FragmentMakeOffPaymentCardBinding>(),
    View.OnClickListener {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMakeOffPaymentCardBinding =
        FragmentMakeOffPaymentCardBinding.inflate(inflater, container, false)

    override fun init() {
        binding.webview.loadSetting("file:///android_asset/NMI.html")
    }

    private var mScreeType = 0
    private var mOptionsType = ""
    private var list: MutableList<VehicleResponse?>? = ArrayList()
    private var mEmail = ""
    override fun initCtrl() {
        arguments?.getInt(Constants.VEHICLE_SCREEN_KEY, 0)?.let {
            mScreeType = it
        }

        mEmail = arguments?.getString(Constants.EMAIL).toString()

        mOptionsType = arguments?.getString(Constants.OPTIONS_TYPE).toString()

        list = arguments?.getParcelableArrayList<VehicleResponse>(Constants.DATA)

        binding.apply {
            tieCardNo.addTextChangedListener(CardNumberFormatterTextWatcher())
            tieExpiryDate.addExpriryListner()
            btnContinue.setOnClickListener(this@MakeOffPaymentCardFragment)
            webview.webChromeClient = consoleListener
        }
        binding.tvAmount.text = list!![0]?.price.toString()


        Logg.logging("testing", "  MakeOffPaymentCardFragment list  $list")
        Logg.logging("testing", "  MakeOffPaymentCardFragment mOptionsType  $mOptionsType")
        Logg.logging("testing", "  MakeOffPaymentCardFragment mEmail  $mEmail")
        Logg.logging("testing", "  MakeOffPaymentCardFragment mScreeType  $mScreeType")

    }

    override fun observer() {}
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnContinue -> {

                val bundle = Bundle()
                bundle.putString(Constants.EMAIL, mEmail)
                bundle.putInt(Constants.VEHICLE_SCREEN_KEY, mScreeType)
                bundle.putParcelableArrayList(
                    Constants.DATA,
                    arguments?.getParcelableArrayList(Constants.DATA)
                )
                bundle.putString(
                    Constants.OPTIONS_TYPE,
                    arguments?.getString(Constants.OPTIONS_TYPE).toString()
                )
                bundle.putParcelable(Constants.PAYMENT_DATA, mModel)

                findNavController().navigate(
                    R.id.action_makeOffPaymentCardFragment_to_makeOffPaymentConfirmationFragment,
                    bundle
                )

            }
        }
    }

    private lateinit var mModel: CardResponseModel

    private val consoleListener = object : WebChromeClient() {
        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            val url: String = consoleMessage.message()
            val check: Boolean = "tokenType" in url
            if (check) {
                Toast.makeText(context, url, Toast.LENGTH_LONG).show()
                binding.webview.gone()
                binding.mcvContainer.visible()
                val responseModel: CardResponseModel =
                    Gson().fromJson(consoleMessage.message(), CardResponseModel::class.java)
                mModel = responseModel
                Logg.logging("testing", "  Card Data  $mModel")
                Logg.logging("testing", "  responseModel.card.number  ${responseModel.card.number}")

                binding.model = CardModel(
                    cardNo = responseModel.card.number,
                    name = responseModel.check.name,
                    expiry = responseModel.card.exp.addCharAtIndex('/', 2), cvv = "***"
                )
                Logg.logging("testing", "  binding.model  ${binding.model}")
                Logg.logging("testing", "  binding.model.cardNo  ${binding.model?.cardNo!!}")

            }
            return true
        }


    }


}
