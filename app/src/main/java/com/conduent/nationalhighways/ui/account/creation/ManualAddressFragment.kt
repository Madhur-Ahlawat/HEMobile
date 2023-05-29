package com.conduent.nationalhighways.ui.account.creation

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.conduent.apollo.interfaces.DropDownItemSelectListener
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.databinding.FragmentManualAddressBinding
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.UK_COUNTRY
import com.conduent.nationalhighways.utils.common.Constants.USA
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.setSpinnerAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ManualAddressFragment :  BaseFragment<FragmentManualAddressBinding>(),
    View.OnClickListener, DropDownItemSelectListener {

    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var countriesList: MutableList<String> = ArrayList()
    private var loader: LoaderDialog? = null
    private var isPersonalAccount : Boolean? = false
    private var requiredAddress : Boolean = false
    private var requiredAddress2 : Boolean = false
    private var requiredCityTown : Boolean = false
    private var requiredPostcode : Boolean = false
    private var requiredCountry : Boolean = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentManualAddressBinding.inflate(inflater, container, false)

    override fun init() {
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        isPersonalAccount = arguments?.getBoolean(Constants.IS_PERSONAL_ACCOUNT,false)

        if(isPersonalAccount == true){
            binding.txtHeading.text = getString(R.string.personal_address)
        }
        arguments?.getString(Constants.POSTCODE)?.let {
            binding.postCode.setText(it) }

        binding.address.editText.addTextChangedListener(GenericTextWatcher(0))
        binding.address2.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.townCity.editText.addTextChangedListener(GenericTextWatcher(2))
        binding.postCode.editText.addTextChangedListener(GenericTextWatcher(3))

        binding.country.dropDownItemSelectListener = this
    }



    override fun initCtrl() {
        viewModel.getCountries()
    }

    override fun observer() {
        observe(viewModel.countriesList, ::getCountriesList)
    }

    override fun onClick(v: View?) {
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
                    compareBy(String.CASE_INSENSITIVE_ORDER, { it })
                )


                if(countriesList.contains(UK_COUNTRY)){
                    countriesList.remove(UK_COUNTRY)
                    countriesList.add(0,UK_COUNTRY)
                }
                if(countriesList.contains(USA)){
                    countriesList.remove(USA)
                    countriesList.add(0,USA)
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
            count: Int) {

            requiredAddress = binding.address.getText()?.isNotEmpty() == true
            requiredAddress2 = binding.address2.getText()?.isNotEmpty() == true
            requiredCityTown = binding.townCity.getText()?.isNotEmpty() == true
            requiredPostcode = binding.postCode.getText()?.isNotEmpty() == true

        }

        override fun afterTextChanged(editable: Editable?) {
            if (requiredAddress && requiredAddress2 && requiredCityTown && requiredPostcode && requiredCountry) {
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