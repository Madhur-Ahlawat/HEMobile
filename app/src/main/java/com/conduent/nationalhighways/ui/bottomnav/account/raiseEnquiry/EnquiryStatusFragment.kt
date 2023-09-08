package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.databinding.FragmentEnquiryStatusBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class EnquiryStatusFragment : BaseFragment<FragmentEnquiryStatusBinding>() {

    lateinit var viewModel: RaiseNewEnquiryViewModel
    private var loader: LoaderDialog? = null

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryStatusBinding =
        FragmentEnquiryStatusBinding.inflate(inflater, container, false)

    override fun init() {

        binding.enquiryReferenceNumberEt.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.lastNameEt.editText.addTextChangedListener(GenericTextWatcher(2))

        binding.btnNext.setOnClickListener {
            val jsonObject = EnquiryStatusRequest(
                viewModel.enquiry_status_number.value?.trim().toString(),
                viewModel.enquiry_last_name.value?.trim().toString()
            )
            viewModel.getAccountSRDetails(jsonObject)
            loader?.show(
                requireActivity().supportFragmentManager,
                Constants.LOADER_DIALOG
            )

        }
    }

    override fun initCtrl() {

    }

    override fun observer() {
        viewModel = ViewModelProvider(requireActivity()).get(
            RaiseNewEnquiryViewModel::class.java
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

        observe(viewModel.getAccountSRList, ::getAccountSRListResponse)
    }

    inner class GenericTextWatcher(val type: Int) : TextWatcher {
        override fun beforeTextChanged(
            charSequence: CharSequence?, start: Int, count: Int, after: Int
        ) {
        }

        override fun onTextChanged(
            charSequence: CharSequence?, start: Int, before: Int, count: Int
        ) {
            if (type == 1) {
                viewModel.enquiry_status_number.value = charSequence.toString()
            } else {
                viewModel.enquiry_last_name.value = charSequence.toString()
            }
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun getAccountSRListResponse(resource: Resource<EnquiryListResponseModel?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        when (resource) {
            is Resource.Success -> {
                if (resource.data?.statusCode == 0) {
                    val bundle: Bundle = Bundle()
                    bundle.putParcelable(
                        Constants.EnquiryResponseModel,
                        resource.data.serviceRequestList.serviceRequest.get(0)
                    )
                    findNavController().navigate(
                        R.id.action_enquiryStatusFragment_to_casesEnquiryDetailsFragment,
                        bundle
                    )
                } else {

                }
            }

            is Resource.DataError -> {

            }

            else -> {

            }
        }
    }

}