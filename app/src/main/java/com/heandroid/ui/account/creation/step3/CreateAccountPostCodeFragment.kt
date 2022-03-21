package com.heandroid.ui.account.creation.step3

import android.os.Bundle
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
        model=arguments?.getParcelable(Constants.DATA)
        binding.tvStep.text= getString(R.string.str_step_f_of_l,3,5)

        when(arguments?.getInt(Constants.PERSONAL_TYPE,0)){
            Constants.PERSONAL_TYPE_PREPAY ->{ binding.tvLabel.text=getString(R.string.personal_pre_pay_account) }
            Constants.PERSONAL_TYPE_PAY_AS_U_GO ->{  binding.tvLabel.text=getString(R.string.pay_as_you_go)  }
        }

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        binding.spnAddress.setSpinnerAdapter(addressList)

    }
    override fun initCtrl() {
        binding.apply {
            btnFindAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            btnAction.setOnClickListener(this@CreateAccountPostCodeFragment)
            tvChange.setOnClickListener(this@CreateAccountPostCodeFragment)
            binding.tilAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            spnAddress.onItemSelectedListener = spinnerListener
        }
    }
    override fun observer() {
        observe(viewModel.addresses,::handleAddressApiResponse)
    }
    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id) {
            R.id.btnAction -> {
                val bundle = Bundle().apply {
                    putParcelable(Constants.DATA,arguments?.getParcelable(Constants.DATA))
                    putInt(Constants.PERSONAL_TYPE, arguments?.getInt(Constants.PERSONAL_TYPE)?:0)
                }
                findNavController().navigate(R.id.action_postcodeFragment_to_createAccoutPasswordFragment,bundle)
            }
            R.id.btnFindAddress -> {
                if(binding.tiePostCode.length()>0) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    viewModel.fetchAddress(binding.tiePostCode.text.toString())
                }
                else { showError(binding.root,getString(R.string.please_enter_postcode)) }
            }

            R.id.tvChange -> { binding.btnFindAddress.performClick() }
            R.id.tilAddress ->{ binding.spnAddress.performClick() }
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
                    for(address : DataAddress in mainList){
                        addressList.add("${address.town} , ${address.street} ,  ${address.locality} , ${address.country}")
                    }

                    binding.apply {
                        btnFindAddress.gone()
                        tilAddress.visible()
                        enable = true
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
                model?.countryType=country
                model?.city=locality
                model?.stateType=town
                model?.zipCode1=postcode
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}