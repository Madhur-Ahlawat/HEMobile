package com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryListResponseModel
import com.conduent.nationalhighways.data.model.raiseEnquiry.EnquiryStatusRequest
import com.conduent.nationalhighways.databinding.FragmentEnquiryStatusBinding
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.bottomnav.account.raiseEnquiry.viewModel.RaiseNewEnquiryViewModel
import com.conduent.nationalhighways.ui.landing.LandingActivity
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.startNewActivityByClearingStack
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class EnquiryStatusFragment : BaseFragment<FragmentEnquiryStatusBinding>() {

    val viewModel: RaiseNewEnquiryViewModel by activityViewModels()
    private var loader: LoaderDialog? = null
    var isViewCreated: Boolean = false
    var isApiCalled: Boolean = false
    var referenceNumberValidations: Boolean = false

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEnquiryStatusBinding =
        FragmentEnquiryStatusBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enquiryReferenceNumberEt.editText.addTextChangedListener(GenericTextWatcher(1))
        binding.enquiryReferenceNumberEt.editText.setText(viewModel.enquiry_status_number.value?:"")
        binding.btnNext.setOnClickListener {
            isApiCalled = false
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
        if (!isViewCreated) {
            binding.viewModel = viewModel
            binding.lifecycleOwner = this

            loader = LoaderDialog()
            loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)

            observe(viewModel.getAccountSRList, ::getAccountSRListResponse)
        }
        isViewCreated = true


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

                when {
                    charSequence.toString().trim().length==0->{
                        referenceNumberValidations=false
                        binding.enquiryReferenceNumberEt.removeError()

                    }
                    charSequence.toString().trim().length>1 && charSequence.toString().trim().length < 3 -> {
                        referenceNumberValidations = false
                        binding.enquiryReferenceNumberEt.setErrorText(resources.getString(R.string.str_reference_3_charac))
                    }

                    Utils.hasSpecialCharacters(
                        binding.enquiryReferenceNumberEt.getText().toString().trim(), ""
                    ) -> {
                        referenceNumberValidations = false
                        binding.enquiryReferenceNumberEt.setErrorText(getString(R.string.str_reference_validations))
                    }

                    else -> {
                        binding.enquiryReferenceNumberEt.removeError()
                        referenceNumberValidations = true
                    }
                }
            }
            checkButtonEnable()
        }

        override fun afterTextChanged(editable: Editable?) {

        }
    }

    private fun checkButtonEnable() {
        if (referenceNumberValidations) {
            binding.btnNext.enable()
        } else {
            binding.btnNext.disable()
        }
    }

    private fun getAccountSRListResponse(resource: Resource<EnquiryListResponseModel?>?) {
        if (!isApiCalled) {
            if (loader?.isVisible == true) {
                loader?.dismiss()
            }
            when (resource) {
                is Resource.Success -> {
                    if (resource.data?.statusCode == 0 && resource.data.serviceRequestList.serviceRequest.orEmpty().size > 0) {
                        if (resource.data.serviceRequestList.serviceRequest.get(0).closedDate == null) {
                            resource.data.serviceRequestList.serviceRequest.get(0).closedDate = ""
                        }
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
                        binding.enquiryReferenceNumberEt.setErrorText(
                            activity?.resources?.getString(
                                R.string.str_incorrect_enquiry
                            ) ?: ""
                        )
                    }
                }

                is Resource.DataError -> {

                }

                else -> {

                }
            }
        }
        isApiCalled = true
    }

}