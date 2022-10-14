package com.conduent.nationalhighways.ui.account.creation.step3

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CountriesModel
import com.conduent.nationalhighways.data.model.account.CountryCodes
import com.conduent.nationalhighways.data.model.account.CreateAccountRequestModel
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPostcodeBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.BUSINESS_ACCOUNT
import com.conduent.nationalhighways.utils.common.Constants.PAYG
import com.conduent.nationalhighways.utils.common.ErrorUtil.showError
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.*
import com.conduent.nationalhighways.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPostCodeFragment : BaseFragment<FragmentCreateAccountPostcodeBinding>(),
    View.OnClickListener {

    private var mCountry = "UK"
    private var loader: LoaderDialog? = null
    private var isEditAccountType: Int? = null
    private var model: CreateAccountRequestModel? = null
    private var addressList: MutableList<String> = ArrayList()
    private var countriesList: MutableList<String> = ArrayList()
    private var mainList: MutableList<DataAddress?> = ArrayList()
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPostcodeBinding.inflate(inflater, container, false)

    override fun init() {
        model = arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        if (arguments?.containsKey(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE) == true) {
            isEditAccountType =
                arguments?.getInt(Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE)
        }
        binding.tvStep.text = getString(R.string.str_step_f_of_l, 3, 6)

        accountType()

        when (model?.planType) {
            PAYG -> {
                // binding.switchViewBusiness.gone()
                binding.tvLabel.text = getString(R.string.pay_as_you_go)
            }
            BUSINESS_ACCOUNT -> {
                // binding.switchViewBusiness.visible()
                binding.tvLabel.text = getString(R.string.business_prepay_account)
            }
            else -> {
                // binding.switchViewBusiness.gone()
                binding.tvLabel.text = getString(R.string.personal_pre_pay_account)
            }
        }
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        checkButton()
    }


    private fun checkButton() {
        binding.enable = if (mCountry == "UK") {
            (binding.tieStreetName.text.toString().trim().isNotEmpty() &&
                    binding.tieCity.text.toString().trim().isNotEmpty() &&
                    binding.tieHouseNumber.text.toString().trim().isNotEmpty())
        } else {
            (binding.tieCountry.text.toString().trim().isNotEmpty() &&
                    binding.tieHouseNumber.text.toString().trim().isNotEmpty() &&
                    binding.tieStreetName.text.toString().trim().isNotEmpty() &&
                    binding.tieCity.text.toString().trim().isNotEmpty() &&
                    binding.tieNonUkPostCode.text.toString().trim().isNotEmpty()
                    && mCountry != "UK" && mCountry != "" && !mCountry.contains("Select country"))
        }
    }

    private fun accountType() {
        when (model?.accountType) {
            BUSINESS_ACCOUNT -> {
                model?.planType = BUSINESS_ACCOUNT
            }
        }
    }

    override fun initCtrl() {
        binding.tieStreetName.onTextChanged {
            checkButton()
        }
        binding.tieCity.onTextChanged {
            checkButton()
        }
        binding.tieCountry.onTextChanged {
            checkButton()
        }
        binding.tieHouseNumber.onTextChanged {
            checkButton()
        }
        binding.tieStreetName.onTextChanged {
            checkButton()
        }
        binding.tieCity.onTextChanged {
            checkButton()
        }
        binding.tieNonUkPostCode.onTextChanged {
            checkButton()
        }

        binding.switchViewBusiness.setOnCheckedChangeListener { _, isChecked ->
            mCountry = if (isChecked) {
                "UK"
            } else ""
            checkButton()
        }

        binding.apply {
            tilNonUkPostCode.gone()
            spinnerCountry.gone()

            switchViewBusiness.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    tilNonUkPostCode.gone()
                    spinnerCountry.gone()
                    tilPostCode.visible()
                    btnFindAddress.visible()
                    tilCountry.gone()
                    tieStreetName.setText("")
                    tieCity.setText("")
                    tieHouseNumber.setText("")
                    tieNonUkPostCode.setText("")

                } else {
                    tilNonUkPostCode.visible()
                    spinnerCountry.visible()
                    tilCountry.visible()
                    tilPostCode.gone()
                    tvChange.gone()
                    tilAddress.gone()
                    btnFindAddress.gone()
                    tieStreetName.setText("")
                    tieCity.setText("")
                    tieHouseNumber.setText("")
                    tiePostCode.setText("")

                    if (countriesList.size == 0 && countriesList.isEmpty()) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        viewModel.getCountries()
                    }
//                    viewModel.getCountryCodesList()
                }

            }

            btnFindAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            btnAction.setOnClickListener(this@CreateAccountPostCodeFragment)
            tvChange.setOnClickListener(this@CreateAccountPostCodeFragment)
            tieAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            tieCountry.setOnClickListener(this@CreateAccountPostCodeFragment)
        }
    }

    override fun observer() {
        observe(viewModel.addresses, ::handleAddressApiResponse)
        observe(viewModel.countriesList, ::getCountriesList)
        observe(viewModel.countriesCodeList, ::getCountryCodesList)
    }

    private fun getCountriesList(response: Resource<List<CountriesModel?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                countriesList.clear()
                countriesList.add(0, getString(R.string.select_country))
                response.data?.forEach {
                    it?.countryName?.let { it1 -> countriesList.add(it1) }
                }
                binding.apply {
                    spinnerCountry.setSpinnerAdapter(countriesList)
                    spinnerCountry.onItemSelectedListener = countriesSpinnerListener
                }
            }
            is Resource.DataError -> {
                showError(binding.root, response.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun getCountryCodesList(response: Resource<List<CountryCodes?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
            }
            is Resource.DataError -> {
                showError(binding.root, response.errorMsg)
            }
            else -> {
            }
        }
    }

    private fun navigate() {

        val mPostCode = if (binding.tiePostCode.text.toString()
                .isEmpty()
        ) binding.tieNonUkPostCode.text.toString() else binding.tiePostCode.text.toString()

        model?.city = binding.tieCity.text.toString()
        model?.stateType = "HE"
        model?.zipCode1 = mPostCode
        model?.address1 = binding.tieStreetName.text.toString()
        model?.countryType = mCountry

        if (model?.accountType == Constants.PERSONAL_ACCOUNT) {
            binding.switchViewBusiness.gone()
            val bundle = Bundle().apply {
                putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
            }
            isEditAccountType?.let {
                bundle.putInt(
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                    Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                )
            }
            findNavController().navigate(
                R.id.action_postcodeFragment_to_createAccoutPasswordFragment,
                bundle
            )

        } else {
            binding.switchViewBusiness.visible()


            val bundle = Bundle().apply {
                putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
                isEditAccountType?.let {
                    putInt(
                        Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE,
                        Constants.FROM_CREATE_ACCOUNT_SUMMARY_TO_EDIT_ACCOUNT_TYPE_KEY
                    )
                }
            }


            findNavController().navigate(
                R.id.action_postcodeFragment_to_createAccoutPasswordFragment,
                bundle
            )
        }

    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {

            R.id.btnAction -> {
                checkForAddress()
            }
            R.id.btnFindAddress -> {
                if (binding.tiePostCode.length() > 0) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                    viewModel.fetchAddress(binding.tiePostCode.text.toString())
                } else {
                    showError(binding.root, getString(R.string.please_enter_postcode))
                }
            }
            R.id.tvChange -> {
                binding.btnFindAddress.performClick()
            }
            R.id.tieAddress -> {
                binding.spnAddress.performClick()
            }
            R.id.tieCountry -> {
                binding.spinnerCountry.performClick()

            }

        }
    }


    private fun checkForAddress() {
        binding.apply {
            if (model?.countryType == "UK") {

//                if (addressList?.size > 0 && addressList[addressList.size - 1] == Constants.NOT_IN_THE_LIST) {
                when {
//                        TextUtils.isEmpty(tieHouseNumber.text?.toString()) ->
//                            setError(tieHouseNumber, "Please enter house number")

                    TextUtils.isEmpty(tieStreetName.text?.toString()) ->
                        setError(tieStreetName, "Please enter street name")


                    TextUtils.isEmpty(tieCity.text?.toString()) ->
                        setError(tieCity, "Please enter city")
                    else -> {
                        navigate()
                    }
                }

            } else {
                when {
                    TextUtils.isEmpty(tieCountry.text?.toString()) && TextUtils.equals(
                        tieCountry.text?.toString(),
                        "Country"
                    ) -> setError(tieCountry, "Please select country")


                    TextUtils.isEmpty(tieHouseNumber.text?.toString()) ->
                        setError(tieHouseNumber, "Please enter house number")

                    TextUtils.isEmpty(tieStreetName.text?.toString()) ->
                        setError(tieStreetName, "Please enter street name")


                    TextUtils.isEmpty(tieCity.text?.toString()) ->
                        setError(tieCity, "Please enter city")

                    TextUtils.isEmpty(tieNonUkPostCode.text?.toString()) ->
                        setError(tieNonUkPostCode, "Please enter Post code")

                    else -> {
                        navigate()
                    }
                }
            }
        }
    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {

                addressList.clear()
                mainList = response.data?.toMutableList() ?: ArrayList()
                addressList.add(0, "Select Address")
                var list: MutableList<String>?
                for (address: DataAddress? in mainList) {
                    list = ArrayList()
                    address?.let {
                        address.town?.let { it1 -> list.add(it1) }
                        address.street?.let { it1 -> list.add(it1) }
                        address.locality?.let { it1 -> list.add(it1) }
                        address.country?.let { it1 -> list.add(it1) }
                        addressList.add(TextUtils.join(",", list))
                    }
                }
                addressList.add(Constants.NOT_IN_THE_LIST)
                binding.apply {

                    spnAddress.setSpinnerAdapter(addressList)
                    spnAddress.onItemSelectedListener = spinnerListener

                    btnFindAddress.gone()
                    tilAddress.visible()
                    model?.zipCode1 = binding.tiePostCode.text.toString()
                    when (model?.planType) {
                        PAYG -> {
                            model?.countryType = null
                            model?.city = null
                            model?.stateType = null
                            checkButton()
                        }
                    }

                    tvChange.visible()
                    tilPostCode.endIconDrawable = null
                }
            }
            is Resource.DataError -> {
                showError(binding.root, response.errorMsg)
            }
            else -> {
            }
        }

    }

    private fun setError(textInputEditText: TextInputEditText, errorMsg: String) {
        textInputEditText.error = errorMsg
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (position == 0) return

            binding.tieAddress.setText(parent.getItemAtPosition(position).toString())
            mainList[position - 1]?.run {
                model?.countryType = "UK"
                model?.city = town
                model?.stateType = "HE"
                model?.zipCode1 = binding.tiePostCode.text.toString()
                model?.address1 = street
                binding.tieHouseNumber.setText(poperty)
                binding.tieStreetName.setText(model?.address1)
                binding.tieCity.setText(model?.city)

            }

            when (model?.planType) {
                PAYG -> {
                    model?.zipCode1 = null
                }
                else -> {
                    if (binding.tiePostCode.text?.isNotEmpty() == true)
                        checkButton()
                }
            }

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }
    private val countriesSpinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            binding.tieCountry.setText("${parent.getItemAtPosition(position)}")
            mCountry = "${parent.getItemAtPosition(position)}"
            checkButton()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

}