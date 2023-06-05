package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.databinding.FragmentManualAddressBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants.UK_COUNTRY
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualAddressFragment :  BaseFragment<FragmentManualAddressBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var loader: LoaderDialog? = null
    private var requiredAddress : Boolean = false
    private var requiredCityTown : Boolean = false
    private var requiredPostcode : Boolean = false
    private var requiredCountry : Boolean = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualAddressBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        if(NewCreateAccountRequestModel.personalAccount){
            binding.txtHeading.text = getString(R.string.personal_address)
        }

        if (NewCreateAccountRequestModel.zipCode.isNotEmpty()){
            binding.postCode.setText(NewCreateAccountRequestModel.zipCode)

        }

        binding.address.editText.addTextChangedListener(GenericTextWatcher())
        binding.address2.editText.addTextChangedListener(GenericTextWatcher())
        binding.townCity.editText.addTextChangedListener(GenericTextWatcher())
        binding.postCode.editText.addTextChangedListener(GenericTextWatcher())

        binding.country.dropDownItemSelectListener = this

        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i])
                ) {
                    return@InputFilter ""
                }
            }
            null
        }

        binding.postCode.editText.filters = arrayOf(filter)
        binding.postCode.setMaxLength(10)
    }



    override fun initCtrl() {
        binding.btnFindAddress.setOnClickListener(this)
        viewModel.getCountries()
    }

    override fun observer() {
        observe(viewModel.countriesList, ::getCountriesList)
    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.btnFindAddress->{
                findNavController().navigate(R.id.action_manualaddressfragment_to_createAccountEligibleLRDS2)
            }
        }
    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                countriesList.sortWith(
                    compareBy(String.CASE_INSENSITIVE_ORDER) { it }
                )


                if(countriesList.contains(UK_COUNTRY)){
                    countriesList.remove(UK_COUNTRY)
                    countriesList.add(0,UK_COUNTRY)
                }

                binding.apply {
                    country.dataSet.addAll(countriesList)
                }
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }
            else -> {
            }

        }
    }

    inner class GenericTextWatcher : TextWatcher {
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
            count: Int) {

            requiredAddress = binding.address.getText()?.isNotEmpty() == true
            requiredCityTown = binding.townCity.getText()?.isNotEmpty() == true
            requiredPostcode = binding.postCode.getText()?.isNotEmpty() == true

        }

        override fun afterTextChanged(editable: Editable?) {
            if (requiredAddress  && requiredCityTown && requiredPostcode && requiredCountry) {
                binding.btnFindAddress.enable()
            } else {
                binding.btnFindAddress.disable()
            }
        }


    }

    override fun onHashMapItemSelected(key: String?, value: Any?) {

    }

    override fun onItemSlected(position: Int, selectedItem: String) {
       requiredCountry = true
    }
}