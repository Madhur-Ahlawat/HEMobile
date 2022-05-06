package com.heandroid.ui.account.creation.step3

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.account.CreateAccountRequestModel
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentCreateAccountPostcodeBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.Constants.BUSINESS_ACCOUNT
import com.heandroid.utils.common.Constants.PAYG
import com.heandroid.utils.common.Constants.PERSONAL_TYPE
import com.heandroid.utils.common.Constants.PERSONAL_TYPE_PAY_AS_U_GO
import com.heandroid.utils.common.Constants.PERSONAL_TYPE_PREPAY
import com.heandroid.utils.common.ErrorUtil.showError
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.setSpinnerAdapter
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPostCodeFragment : BaseFragment<FragmentCreateAccountPostcodeBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null

    private val viewModel : CreateAccountPostCodeViewModel by viewModels()
    private var model : CreateAccountRequestModel? =null

    private var addressList : MutableList<String> = ArrayList()
    private var mainList : MutableList<DataAddress> = ArrayList()

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPostcodeBinding.inflate(inflater,container,false)

    override fun init() {
        binding.enable=false
        model=arguments?.getParcelable(Constants.CREATE_ACCOUNT_DATA)
        binding.tvStep.text= getString(R.string.str_step_f_of_l,3,5)

        accountType()

        when(model?.planType){
            PAYG ->{  binding.tvLabel.text=getString(R.string.pay_as_you_go)  }
            BUSINESS_ACCOUNT -> { binding.tvLabel.text=getString(R.string.business_prepay_account) }
            else ->{ binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
        }

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }

    private fun accountType() {
        when(model?.accountType){
            BUSINESS_ACCOUNT -> { model?.planType = BUSINESS_ACCOUNT }
        }
    }

    override fun initCtrl() {
        binding.apply {
            btnFindAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            btnAction.setOnClickListener(this@CreateAccountPostCodeFragment)
            tvChange.setOnClickListener(this@CreateAccountPostCodeFragment)
            tieAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
        }
    }
    override fun observer() {
        observe(viewModel.addresses,::handleAddressApiResponse)
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id) {

            R.id.btnAction -> {

                if (model?.accountType == Constants.PERSONAL_ACCOUNT) {
                    binding.switchViewBusiness.gone()
                    val bundle = Bundle().apply {
                        putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
                    }
                    findNavController().navigate(
                        R.id.action_postcodeFragment_to_createAccoutPasswordFragment,
                        bundle
                    )

                } else {
                    binding.switchViewBusiness.visible()
                    var country = "UK"
                    country = if (!binding.switchViewBusiness.isChecked) {
                        "Non-UK"
                    } else {
                        "UK"
                    }
                    model?.countryType = country

                    val bundle = Bundle().apply {
                        putParcelable(Constants.CREATE_ACCOUNT_DATA, model)
                    }
                    findNavController().navigate(
                        R.id.action_postcodeFragment_to_createAccoutPasswordFragment,
                        bundle
                    )
                }
            }
            R.id.btnFindAddress -> {
                if(binding.tiePostCode.length()>0) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    viewModel.fetchAddress(binding.tiePostCode.text.toString())
                }
                else { showError(binding.root,getString(R.string.please_enter_postcode)) }
            }
            R.id.tvChange -> { binding.btnFindAddress.performClick() }
            R.id.tieAddress ->{ binding.spnAddress.performClick() }

        }
    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress>?>?) {
        try {
            loader?.dismiss()
            when (response) {
                is Resource.Success -> {

                    addressList.clear()
                    mainList= response.data?.toMutableList()?:ArrayList()
                    addressList?.add(0,"Select Address")
                    var list : MutableList<String>? = null
                    for(address : DataAddress in mainList){
                        list = ArrayList()
                        list.add(address.town)
                        list.add(address.street)
                        list.add(address.locality)
                        list.add(address.country)
                        addressList.add(TextUtils.join(",", list))
                    }
                    binding.apply {

                        spnAddress.setSpinnerAdapter(addressList)
                        spnAddress.onItemSelectedListener = spinnerListener


                        btnFindAddress.gone()
                        tilAddress.visible()
                        model?.zipCode1=binding.tiePostCode.text.toString()


                        when(model?.planType) {
                            PAYG -> {
                                model?.countryType=null
                                model?.city=null
                                model?.stateType=null
                                enable = true
                            }
                        }

                        tvChange.visible()
                        tilPostCode.endIconDrawable = null
                    }
                }
                is Resource.DataError -> { showError(binding.root, response.errorMsg) }
            }
        } catch (e: Exception) { }
    }
    private val spinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (position == 0) return

            binding.tieAddress.setText(parent.getItemAtPosition(position).toString())
            mainList[position-1].run {
                model?.countryType="UK"
                model?.city=town
                model?.stateType="HE"
                model?.zipCode1=postcode
                model?.address1=street
            }

            when(model?.planType){
                PAYG ->{ model?.zipCode1=null }
                else -> { if(binding.tiePostCode.text?.isNotEmpty()==true) binding.enable = true }
            }

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }

}