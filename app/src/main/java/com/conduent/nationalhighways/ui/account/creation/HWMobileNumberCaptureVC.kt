package com.conduent.nationalhighways.ui.account.creation

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.AccountCreateRequestModel
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.databinding.FragmentMobileNumberCaptureVcBinding
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.USA
import com.conduent.nationalhighways.utils.common.Constants.USA_CODE
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Mohammed Sameer Ahmad .
 */
@AndroidEntryPoint
class HWMobileNumberCaptureVC : BaseFragment<FragmentMobileNumberCaptureVcBinding>(),
    View.OnClickListener, OnRetryClickListener {

    private var requiredFirstName = false
    private var requiredLastName = false
    private var loader: LoaderDialog? = null
    private var requestModel = AccountCreateRequestModel.RequestModel()
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesCodeList: MutableList<String> = ArrayList()
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMobileNumberCaptureVcBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.inputCountry.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.inputMobileNumber.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.inputMobileNumber.editText.inputType = InputType.TYPE_CLASS_NUMBER;

        binding.btnNext.setOnClickListener(this)
    }

    override fun initCtrl() {
        viewModel.getCountryCodesList()
    }

    override fun observer() {
        observe(viewModel.countriesCodeList, ::getCountryCodesList)
    }

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesCodeList.clear()
                response.data?.forEach {
                    it?.value?.let { it1 -> countriesCodeList.add(it1) }
                }
                countriesCodeList.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it }
                )


                if(countriesCodeList.contains(Constants.UK_CODE)){
                    countriesCodeList.remove(Constants.UK_CODE)
                    countriesCodeList.add(0, Constants.UK_CODE)
                }
                if(countriesCodeList.contains(USA_CODE)){
                    countriesCodeList.remove(USA_CODE)
                    countriesCodeList.add(0,USA_CODE)
                }
                binding.apply {
                    inputCountry.dataSet.addAll(countriesCodeList)
                    inputCountry.setSelectedValue(Constants.UK_CODE)
                }

            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }
            else -> {
            }

        }
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnNext.id -> {
                requestModel.userInfoModel.firstName = binding.inputCountry.getText().toString()
                requestModel.userInfoModel.lastName = binding.inputMobileNumber.getText().toString()

                val bundle = Bundle()
                bundle.putParcelable(accountRequestModelKey, requestModel)
                /*isPersonalAccount?.let { bundle.putBoolean(Constants.IS_PERSONAL_ACCOUNT, it) }
                findNavController().navigate(
                    R.id.action_createAccountPersonalInfo_to_createAccountPostCodeNew,
                    bundle
                )*/

            }
        }
    }

    override fun onRetryClick() {

    }

    inner class GenericTextWatcher(private val index: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            /*when (view) {
                binding.inputCountry.getEditText() -> {
                    requiredFirstName = binding.inputCountry.getText()?.isNotEmpty() == true
                }
                binding.inputMobileNumber.getEditText() -> {
                    requiredLastName = binding.inputMobileNumber.getText()?.isNotEmpty() == true
                }
            }*/
            requiredFirstName = binding.inputCountry.getText()?.isNotEmpty() == true
            val value = binding.inputMobileNumber.getText()?.length
            if (value != null) {
                requiredLastName = value>9
            }

        }

        override fun afterTextChanged(editable: Editable?) {
            if (requiredFirstName && requiredLastName){
                binding.btnNext.enable()
            }else{
                binding.btnNext.disable()
            }
        }
    }
}