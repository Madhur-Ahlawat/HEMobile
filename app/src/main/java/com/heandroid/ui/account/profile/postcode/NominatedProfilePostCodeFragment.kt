package com.heandroid.ui.account.profile.postcode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.address.DataAddress
import com.heandroid.databinding.FragmentNominatedProfilePostcodeBinding
import com.heandroid.databinding.FragmentProfilePostcodeBinding
import com.heandroid.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.heandroid.ui.base.BaseFragment
import com.heandroid.ui.loader.LoaderDialog
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.ErrorUtil
import com.heandroid.utils.common.Resource
import com.heandroid.utils.common.observe
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.setSpinnerAdapter
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NominatedProfilePostCodeFragment: BaseFragment<FragmentNominatedProfilePostcodeBinding>(), View.OnClickListener {
    private val viewModel : CreateAccountPostCodeViewModel by viewModels()
    private var addressList : MutableList<String> = ArrayList()
    private var mainList : MutableList<DataAddress?> = ArrayList()
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)= FragmentNominatedProfilePostcodeBinding.inflate(inflater,container,false)
    override fun init() {
        binding.enable=false
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        binding.data=arguments?.getParcelable(Constants.DATA)
        binding.enable=true
        if(binding.data?.personalInformation?.zipcode?.isNotEmpty()==true) binding.tilAddress.visible()
    }
    override fun initCtrl() {
        binding.apply {
            btnFindAddress.setOnClickListener(this@NominatedProfilePostCodeFragment)
            btnAction.setOnClickListener(this@NominatedProfilePostCodeFragment)
            tvChange.setOnClickListener(this@NominatedProfilePostCodeFragment)
            binding.tilAddress.setOnClickListener(this@NominatedProfilePostCodeFragment)
            binding.tiePostCode.doAfterTextChanged { enable = tiePostCode.text.toString().isNotEmpty() && tieAddress.text.toString().isNotEmpty() && tieAddress.text.toString() != "Select Address" }
        }
    }
    override fun observer() {
        observe(viewModel.addresses,::handleAddressApiResponse)
    }


    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id) {
            R.id.btnAction -> {

                val bundle= Bundle()
                bundle.putParcelable(Constants.DATA,binding.data)
                findNavController().navigate(R.id.action_nominatedPostcodeFragment_to_nominatedPasswordFragment,bundle)

            }
            R.id.btnFindAddress -> {
                if(binding.tiePostCode.length()>0) {
                    loader?.show(requireActivity().supportFragmentManager,Constants.LOADER_DIALOG)
                    viewModel.fetchAddress(binding.tiePostCode.text.toString())
                }
                else {
                    ErrorUtil.showError(binding.root, getString(R.string.please_enter_postcode))
                }
            }

            R.id.tvChange -> { binding.btnFindAddress.performClick() }
            R.id.tilAddress ->{ binding.spnAddress.performClick() }
        }
    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress?>?>?) {
        try {
            loader?.dismiss()
            when (response) {
                is Resource.Success -> {
                    addressList.clear()
                    mainList= response.data?.toMutableList()?:ArrayList()
                    addressList.add(0,"Select Address")
                    for(address : DataAddress? in mainList){
                        address?.let {
                            addressList.add("${address.town} , ${address.street} ,  ${address.locality} , ${address.country}")
                        }
                    }
                    binding.apply {
                        spnAddress.setSpinnerAdapter(addressList)
                        spnAddress.onItemSelectedListener = spinnerListener
                        tilAddress.visible()
                        enable = true
                        btnFindAddress.strokeColor=null
                        btnFindAddress.strokeWidth=0
                        btnFindAddress.backgroundTintList=
                            ContextCompat.getColorStateList(requireActivity(), R.color.green)
                        btnFindAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    }
                }
                is Resource.DataError -> {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }
        } catch (e: Exception) { }
    }
    private val spinnerListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            if (position == 0) return
            binding.tieAddress.setText(parent.getItemAtPosition(position).toString())
            binding.data?.personalInformation?.addressLine1=parent.getItemAtPosition(position).toString()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }


}