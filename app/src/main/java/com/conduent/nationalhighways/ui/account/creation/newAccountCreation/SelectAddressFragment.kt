package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.databinding.FragmentSelectAddressBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.SelectAddressAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectAddressFragment : BaseFragment<FragmentSelectAddressBinding>(),
    View.OnClickListener, SelectAddressAdapter.AddressCallBack {

    private var selectAddressAdapter: SelectAddressAdapter? = null
    private val viewModel: CreateAccountPostCodeViewModel by viewModels()
    private var loader: LoaderDialog? = null
    private var mainList: MutableList<DataAddress?> = ArrayList()
    private var isViewCreated: Boolean = false


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectAddressBinding =
        FragmentSelectAddressBinding.inflate(inflater, container, false)

    override fun init() {
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.recylcerview.layoutManager = linearLayoutManager


        selectAddressAdapter = SelectAddressAdapter(requireContext(), mainList, this)
        binding.recylcerview.adapter = selectAddressAdapter
        binding.txtAddressCount.text = "${mainList.size} Addresses Found"
        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

    }

    override fun initCtrl() {

        binding.btnNext.setOnClickListener(this)
        binding.enterAddressManually.setOnClickListener(this)
    }

    override fun observer() {
        if (!isViewCreated) {
            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
            viewModel.fetchAddress(NewCreateAccountRequestModel.zipCode)
            observe(viewModel.addresses, ::handleAddressApiResponse)

        }
        isViewCreated = true

    }

    private fun handleAddressApiResponse(response: Resource<List<DataAddress?>?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (response) {
            is Resource.Success -> {
                mainList = response.data?.toMutableList() ?: ArrayList()
                selectAddressAdapter?.updateList(mainList)
                binding.txtAddressCount.text = "${mainList.size} Addresses Found"

            }
            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, response.errorMsg)
                enterAddressManual()
            }
            else -> {
                enterAddressManual()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnNext -> {
                findNavController().navigate(R.id.action_selectaddressfragment_to_createAccountEligibleLRDS2)
            }
            binding.enterAddressManually -> {
                findNavController().navigate(R.id.fragment_manual_address)
            }
        }

    }

    private fun enterAddressManual() {
        findNavController().navigate(R.id.fragment_manual_address)
    }


    override fun addressCallback(position: Int) {

        for (i in 0 until mainList.size) {
            mainList[i]?.isSelected = false
        }
        mainList[position]?.isSelected = true
        selectAddressAdapter?.notifyDataSetChanged()


        binding.btnNext.isEnabled = mainList[position]?.isSelected == true
    }


}