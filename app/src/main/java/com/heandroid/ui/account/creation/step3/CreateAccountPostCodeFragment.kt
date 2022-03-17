package com.heandroid.ui.account.creation.step3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
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
import com.heandroid.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateAccountPostCodeFragment : BaseFragment<FragmentCreateAccountPostcodeBinding>(), View.OnClickListener {

    private var loader: LoaderDialog? = null

    private val viewModel : CreateAccountPostCodeViewModel by viewModels()
    private var model : CreateAccountRequestModel? =null


    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentCreateAccountPostcodeBinding.inflate(inflater,container,false)

    override fun init() {
        binding.enable=false
        model=arguments?.getParcelable(Constants.DATA)
        binding.tvStep.text= getString(R.string.str_step_f_of_l,3,5)

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
    }
    override fun initCtrl() {
        binding.apply {
            btnFindAddress.setOnClickListener(this@CreateAccountPostCodeFragment)
            btnAction.setOnClickListener(this@CreateAccountPostCodeFragment)
        }
    }
    override fun observer() {
        observe(viewModel.addresses,::handleAddressApiResponse)
    }
    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id) {
            R.id.btnAction -> {
//                // password fragment to be called with the same model data
//                val bundle = Bundle()
//                bundle.putParcelable(DATA,arguments?.getParcelable(DATA))
//                findNavController().navigate(R.id._,bundle)
            }
            R.id.btnFindAddress -> {
                if(binding.tiePostCode.length()>0) {
                    loader?.show(requireActivity().supportFragmentManager,"")
                    viewModel.fetchAddress(binding.tiePostCode.text.toString())
                }
                else { showError(binding.root,getString(R.string.please_enter_postcode)) }
            }
        }
    }


    private fun handleAddressApiResponse(response: Resource<List<DataAddress>?>?) {
        try {
            loader?.dismiss()
            when (response) {
                is Resource.Success -> {
                    response.data?.get(0)?.run {
                        model?.address1 = "$town , $street ,  $locality , $country"
                        binding.apply {
                            btnFindAddress.gone()
                            tilAddress.visible()
                        }
                        model?.countryType=country
                        model?.city=locality
                        model?.stateType="India"
                    }
                }
                is Resource.DataError -> { showError(binding.root, response.errorMsg) }
            }
        } catch (e: Exception) { }
    }

}