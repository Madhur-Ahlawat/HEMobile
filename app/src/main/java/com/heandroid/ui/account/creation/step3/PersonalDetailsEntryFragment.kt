package com.heandroid.ui.account.creation.step3

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.heandroid.R
import com.heandroid.data.model.account.AccountDetails
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentPersonalDetailsEntryBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.toolbar
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalDetailsEntryFragment : BaseFragment<FragmentPersonalDetailsEntryBinding>(), View.OnClickListener {

    private lateinit var accountModel: AccountDetails
    private var entryType: String = Constants.PERSONAL_DETAILS
    private val viewModelCreateAccount: CreateAccountAccountViewModel by viewModels()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentPersonalDetailsEntryBinding.inflate(inflater, container, false)


    override fun init() {
        accountModel = AccountDetails()
        showPersonalDetailsEntryView()
    }

    override fun initCtrl() {
        binding.apply {
            tvStep.text = requireActivity().getString(R.string.str_step_f_of_l, 3, 5)
            tvPersonalInfo.setOnClickListener(this@PersonalDetailsEntryFragment)
            tvPostCode.setOnClickListener(this@PersonalDetailsEntryFragment)
            tvPin.setOnClickListener(this@PersonalDetailsEntryFragment)
            tvPassword.setOnClickListener(this@PersonalDetailsEntryFragment)

            // set button disable first time
            accountModel.enable = false
            model = accountModel
            btnAction.text = requireActivity().getText(R.string.str_next)
            btnAction.setOnClickListener(this@PersonalDetailsEntryFragment)
            btnFindAddress.setOnClickListener(this@PersonalDetailsEntryFragment)
            tvChange.setOnClickListener(this@PersonalDetailsEntryFragment)
            edtFullName.addTextChangedListener {
                //btnAction.isEnabled = true
                isFullNameEntered()
            }

            edtPostCode.addTextChangedListener {
                isPostalCodeEntered()
            }

        }
    }

    private fun isPostalCodeEntered() {
        binding.apply {
            btnFindAddress.visible()
            btnFindAddress.isEnabled = true
            // btnAction.isEnabled = !(TextUtils.isEmpty(edtPostCode.text.toString()) && TextUtils.isEmpty(tvAddress.text.toString()))
        }
    }

    private fun isFullNameEntered() {
        binding.apply {
            btnAction.isEnabled = !TextUtils.isEmpty(edtFullName.text.toString())
            Log.d("BtnEnable1", (edtFullName.text.toString()))
        }

    }


    private fun invalidateView() {

        binding.apply {
            viewOne.gone()
            viewTwo.gone()
            viewThree.gone()
            viewFour.gone()

            // center view
            clPersonalInfo.gone()
            clPassword.gone()
            clPinView.gone()
            clPostCode.gone()
            clLrds.gone()
        }

    }

    // view to enter personal details
    private fun showPersonalDetailsEntryView() {
        invalidateView()
        entryType = Constants.PERSONAL_DETAILS
        // accountModel.enable = accountModel.fullName.isNotEmpty()
        binding.apply {
            tvPersonalInfo.setDivider(true)
            viewOne.visible()
            clPersonalInfo.visible()
            // set button disable first time

            // model = accountModel
            btnAction.text = requireActivity().getText(R.string.str_next)
        }

    }


    // fun to enter address
    private fun showEnterAddressView() {
        invalidateView()
        entryType = Constants.POST_CODE_ADDRESS
        binding.apply {
            tvPostCode.setDivider(true)
            clPostCode.visible()
            viewTwo.visible()
// set button disable first time
            accountModel.enable =
                accountModel.postCode.isNotEmpty() && edtPostCode.text.isNullOrEmpty()
            model = accountModel

        }
    }

    // view to create password
    private fun showCreatePasswordView() {
        invalidateView()
        entryType = Constants.PASSWORD
        binding.apply {
            tvPassword.setDivider(true)
            clPassword.visible()
            viewThree.visible()
            // set button disable first time
            accountModel.enable =
                accountModel.createPassword.isNotEmpty() && edtConformPassword.text.isNullOrEmpty()
            model = accountModel


        }
    }

    // view to set pin
    private fun showSetPinView() {
        invalidateView()
        entryType = Constants.PIN
        binding.apply {
            tvPin.setDivider(true)
            clPinView.visible()
            viewFour.visible()
            // set button disable first time
            accountModel.enable = accountModel.pin.isNotEmpty()
            model = accountModel

        }
    }

    override fun observer() {
        observe(viewModelCreateAccount.addresses, ::handleAddressApiResponse)
    }

    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.tvPersonalInfo -> {
                    showPersonalDetailsEntryView()
                }
                R.id.tvPostCode -> {
                    showEnterAddressView()
                }
                R.id.tvPassword -> {
                    showCreatePasswordView()
                }

                R.id.tvPin -> {
                    showSetPinView()
                }

                R.id.btnAction -> {
                    performUserActionOnButtonClick()

                }

                R.id.btnFindAddress -> {
                    callApiTofetchAddress()
                }

                R.id.tv_change->{
                    binding.apply {
                        edtPostCode.setText("")
                        accountModel.postCode=""
                        model=accountModel
                    }

                }
                else->{

                }
            }
        }
    }

    // method to fetch address based on postal code
    private fun callApiTofetchAddress() {
        loader?.show(requireActivity().supportFragmentManager, "")
        viewModelCreateAccount.fetchAddress(binding.edtPostCode.text.toString())

    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress>?>?) {
        try {
            loader?.dismiss()
            when (response) {
                is Resource.Success -> {
                    Log.d("dataFound", "True")
                    response?.data?.let {
                        var addressItem = it[0]
                        accountModel.address =
                            "${addressItem.town} , ${addressItem.street} ,  ${addressItem.locality} , ${addressItem.country}"
                        binding.tvAddress.text = accountModel.address
                    }
                    binding.apply {

                        btnFindAddress.gone()
                        tvAddress.visible()
                    }

                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun performUserActionOnButtonClick() {

        when (entryType) {
            Constants.PERSONAL_DETAILS -> {
                Log.d("PersonalDet", accountModel.fullName)
                showEnterAddressView()
            }

            Constants.POST_CODE_ADDRESS -> {
                Log.d("PostCode", accountModel.postCode)
                showCreatePasswordView()
            }

            Constants.PASSWORD -> {
                Log.d("Password", accountModel.createPassword)
                showSetPinView()
            }

            Constants.PIN -> {
                Log.d("Pin", accountModel.pin)
                showLRDS()
            }
        }
    }


    // to show LRDS suggestion  view
    private fun showLRDS() {
        invalidateView()
        entryType = Constants.LRDS_SCREEN
        binding.apply {
            clHeader.gone()
            requireActivity().toolbar(requireActivity().getString(R.string.str_local_resident_information))
            // set button disable first time
            accountModel.enable = accountModel.fullName.isEmpty()
            model = accountModel
            btnAction.text = requireActivity().getText(R.string.str_next)
        }

    }


    fun isEnable() {
        if (binding.edtFullName.length() > 1 && entryType == Constants.PERSONAL_DETAILS) {

            binding.model = accountModel.apply {
                enable = true
                fullName = binding.edtFullName.text.toString()
            }
        }
        if (binding.edtPostCode.length() > 1 && entryType == Constants.POST_CODE_ADDRESS) {

            binding.model = accountModel.apply {
                enable = true
                fullName = binding.edtFullName.text.toString()
            }
        } else {
            binding.model?.let {
                it.enable = false
            } ?: AccountDetails(enable = false)
        }

        Log.d("ButtonEnable", accountModel.enable.toString());
    }

}