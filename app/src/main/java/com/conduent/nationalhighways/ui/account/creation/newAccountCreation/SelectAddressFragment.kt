package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.UpdateProfileRequest
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentSelectAddressBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.SelectAddressAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.viewModel.LrdsEligibilityViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_ADDRESS_CHANGED
import com.conduent.nationalhighways.utils.common.ErrorUtil
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
    private val lrdsViewModel: LrdsEligibilityViewModel by viewModels()
    private val viewModelProfile: ProfileViewModel by viewModels()
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
            observe(lrdsViewModel.lrdsEligibilityCheck, ::handleLrdsApiResponse)

        }
        isViewCreated = true
        observe(viewModelProfile.updateProfileApiVal, ::handleUpdateProfileDetail)
    }

    private fun handleUpdateProfileDetail(resource: Resource<EmptyApiResponse?>?) {
        loader?.dismiss()
        when (resource) {
            is Resource.Success -> {
                Log.d("Success", "Updated successfully")
                val data = navData as ProfileDetailModel?
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, PROFILE_MANAGEMENT_ADDRESS_CHANGED)
                bundle.putParcelable(Constants.NAV_DATA_KEY, data?.personalInformation)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON,false)
                findNavController().navigate(R.id.action_selectaddressfragment_to_resetForgotPassword,bundle)
            }
            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, resource.errorMsg)
            }
            else -> {
            }
        }
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

                loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
                if(navFlowCall.equals(PROFILE_MANAGEMENT,true)){
                    val data = navData as ProfileDetailModel?
                    if (data?.accountInformation?.accountType.equals(Constants.PERSONAL_ACCOUNT,true)) {
                        updateStandardUserProfile(data)
                    }else{
                        updateBusinessUserProfile(data)
                    }
                }else {
                    hitlrdsCheckApi()
                }
            }

            binding.enterAddressManually -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
                if(navData != null){
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY,data)
                }
                findNavController().navigate(R.id.fragment_manual_address,bundle)
            }
        }

    }

    private fun hitlrdsCheckApi() {
        val lrdsEligibilityCheck = LrdsEligibiltyRequest()
        if(NewCreateAccountRequestModel.country.equals(Constants.UK_COUNTRY,true)){
            lrdsEligibilityCheck.country = "UK"
        }else {
            lrdsEligibilityCheck.country = NewCreateAccountRequestModel.country
        }
        lrdsEligibilityCheck.addressline1 = NewCreateAccountRequestModel.addressline1
        lrdsEligibilityCheck.firstName = NewCreateAccountRequestModel.firstName
        lrdsEligibilityCheck.lastName = NewCreateAccountRequestModel.lastName
        lrdsEligibilityCheck.zipcode1 = NewCreateAccountRequestModel.zipCode
        lrdsEligibilityCheck.city = NewCreateAccountRequestModel.townCity
        lrdsEligibilityCheck.state = NewCreateAccountRequestModel.townCity
        lrdsEligibilityCheck.action = Constants.LRDS_ELIGIBILITY_CHECK




        lrdsViewModel.getLrdsEligibilityResponse(lrdsEligibilityCheck)
    }

    private fun enterAddressManual() {
        val bundle = Bundle()
        bundle.putString(Constants.NAV_FLOW_KEY,navFlowCall)
        if(navData != null){
            val data = navData as ProfileDetailModel?
            bundle.putParcelable(Constants.NAV_DATA_KEY,data)
        }

        findNavController().navigate(R.id.fragment_manual_address,bundle)
    }


    override fun addressCallback(position: Int) {

        for (i in 0 until mainList.size) {
            mainList[i]?.isSelected = false
        }
        mainList[position]?.isSelected = true
        selectAddressAdapter?.notifyDataSetChanged()



        NewCreateAccountRequestModel.addressline1 = mainList[position]?.street.toString()
        NewCreateAccountRequestModel.townCity = mainList[position]?.town.toString()
        NewCreateAccountRequestModel.country =
            mainList[position]?.country.toString()
        NewCreateAccountRequestModel.zipCode = mainList[position]?.postcode.toString().trim().replace(" ","")

        binding.btnNext.isEnabled = mainList[position]?.isSelected == true
    }

    private fun handleLrdsApiResponse(response: Resource<LrdsEligibilityResponse?>?) {
        if (loader?.isVisible == true) {
            loader?.dismiss()
        }
        val bundle = Bundle()
        bundle.putString(
            Constants.NAV_FLOW_KEY,
            navFlowCall
        )
        when (response) {
            is Resource.Success -> {
                NewCreateAccountRequestModel.isManualAddress = false
                if (response.data?.lrdsEligible.equals("true", true)) {
                    findNavController().navigate(R.id.action_selectaddressfragment_to_createAccountEligibleLRDS2,bundle)

                } else {

                    when(navFlowCall){


                        EDIT_SUMMARY -> {findNavController().navigate(R.id.action_selectaddressfragment_to_createAccountSummary,bundle)}

                        else -> { if (NewCreateAccountRequestModel.personalAccount) {
                            findNavController().navigate(R.id.action_selectaddressfragment_to_createAccountTypesFragment,bundle)

                        } else {

                            findNavController().navigate(
                                R.id.action_selectaddressfragment_to_forgotPasswordFragment,
                                bundle
                            )

                        }}

                    }
                }

            }

            is Resource.DataError -> {
                ErrorUtil.showError(binding.root, response.errorMsg)
            }

            else -> {

            }
        }

    }

    private fun updateStandardUserProfile(data: ProfileDetailModel?) {

        data?.personalInformation?.run {
            val request = UpdateProfileRequest(
                firstName = firstName,
                lastName = lastName,
                addressLine1 = NewCreateAccountRequestModel.addressline1,
                addressLine2 = NewCreateAccountRequestModel.addressline2,
                city = NewCreateAccountRequestModel.townCity,
                state = state,
                zipCode = NewCreateAccountRequestModel.zipCode,
                zipCodePlus = zipCodePlus,
                country = NewCreateAccountRequestModel.country,
                emailAddress = emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = pemailUniqueCode,
                phoneCell = phoneNumber ?: "",
                phoneDay = phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = ""
            )

            viewModelProfile.updateUserDetails(request)
        }

    }

    private fun updateBusinessUserProfile(data: ProfileDetailModel?) {
        data?.run {
            val request = UpdateProfileRequest(
                firstName = personalInformation?.firstName,
                lastName = personalInformation?.lastName,
                addressLine1 = NewCreateAccountRequestModel.addressline1,
                addressLine2 = NewCreateAccountRequestModel.addressline2,
                city = NewCreateAccountRequestModel.townCity,
                state = personalInformation?.state,
                zipCode = NewCreateAccountRequestModel.zipCode,
                zipCodePlus = personalInformation?.zipCodePlus,
                country = NewCreateAccountRequestModel.country,
                emailAddress = personalInformation?.emailAddress,
                primaryEmailStatus = Constants.PENDING_STATUS,
                primaryEmailUniqueID = personalInformation?.pemailUniqueCode,
                phoneCell = personalInformation?.phoneNumber ?: "",
                phoneDay = personalInformation?.phoneDay,
                phoneFax = "",
                smsOption = "Y",
                phoneEvening = "",
                fein = accountInformation?.fein,
                businessName = personalInformation?.customerName
            )

            viewModelProfile.updateUserDetails(request)
        }


    }

}


