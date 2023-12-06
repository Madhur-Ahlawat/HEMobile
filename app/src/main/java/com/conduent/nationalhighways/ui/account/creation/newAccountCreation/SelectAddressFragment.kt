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
import com.conduent.nationalhighways.data.model.address.DataAddress
import com.conduent.nationalhighways.data.model.profile.ProfileDetailModel
import com.conduent.nationalhighways.databinding.FragmentSelectAddressBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.SelectAddressAdapter
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.request.LrdsEligibiltyRequest
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.lrds.response.LrdsEligibilityResponse
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.viewModel.LrdsEligibilityViewModel
import com.conduent.nationalhighways.ui.account.creation.step3.CreateAccountPostCodeViewModel
import com.conduent.nationalhighways.ui.account.profile.ProfileViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.LoaderDialog
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_FROM_POST_CODE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT
import com.conduent.nationalhighways.utils.common.Constants.PROFILE_MANAGEMENT_ADDRESS_CHANGED
import com.conduent.nationalhighways.utils.common.ErrorUtil
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.visible
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
        if (arguments?.containsKey(Constants.ADDRESS_LIST) == true) {
            mainList = arguments?.getParcelableArrayList(Constants.ADDRESS_LIST) ?: ArrayList()


        }


        loader = LoaderDialog()
        loader?.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_NoTitle)
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.recylcerview.layoutManager = linearLayoutManager
        selectAddressAdapter = SelectAddressAdapter(requireContext(), mainList, this)
        binding.recylcerview.adapter = selectAddressAdapter

        selectAddressAdapter?.updateList(mainList)
        binding.txtAddressCount.text = "${mainList.size} Addresses Found"

        when (navFlowCall) {
            EDIT_SUMMARY, EDIT_ACCOUNT_TYPE -> {
                mainList.forEach { it?.isSelected = false }
                if (NewCreateAccountRequestModel.selectedAddressId != -1) {
                    mainList[NewCreateAccountRequestModel.selectedAddressId]?.isSelected =
                        true
                    selectAddressAdapter?.notifyDataSetChanged()
                    binding.btnNext.isEnabled = true
                }
            }
        }
        if (navFlowCall.equals(PROFILE_MANAGEMENT)) {
            binding.btnEnterAddressManually.gone()
            binding.btnUpdateAddressManually.visible()
        } else {
            binding.btnEnterAddressManually.visible()
            binding.btnUpdateAddressManually.gone()
        }

    }

    override fun initCtrl() {

        binding.btnNext.setOnClickListener(this)
        binding.btnEnterAddressManually.setOnClickListener(this)
        binding.btnUpdateAddressManually.setOnClickListener(this)
    }

    override fun observer() {
        if (!isViewCreated) {
//            loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)
//            viewModel.fetchAddress(NewCreateAccountRequestModel.zipCode)
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
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_selectaddressfragment_to_resetForgotPassword,
                    bundle
                )
            }

            is Resource.DataError -> {
                if ((resource.errorModel?.errorCode == Constants.TOKEN_FAIL && resource.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || resource.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(resource.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, resource.errorMsg)
                }
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
//                mainList = response.data?.toMutableList() ?: ArrayList()
               val dataAddresses = response.data?.toMutableList() ?: ArrayList()
                Log.e("TAG", "handleAddressApiResponse:!!@ "+dataAddresses.toString() )

                mainList = dataAddresses.sortedWith(compareBy { address ->
                    val street = address?.street ?: ""
                    if (street.all { it.isDigit() }) {
                        street.toInt()
                    } else {
                        Int.MAX_VALUE
                    }
                })?.toMutableList()?:ArrayList()

                Log.e("TAG", "handleAddressApiResponse: "+mainList.toString() )
                selectAddressAdapter?.updateList(mainList)
                binding.txtAddressCount.text = "${mainList.size} Addresses Found"

                when (navFlowCall) {
                    EDIT_SUMMARY, EDIT_ACCOUNT_TYPE -> {
                        mainList.forEach { it?.isSelected = false }
                        if (NewCreateAccountRequestModel.selectedAddressId != -1) {
                            mainList[NewCreateAccountRequestModel.selectedAddressId]?.isSelected =
                                true
                            selectAddressAdapter?.notifyDataSetChanged()
                            binding.btnNext.isEnabled = true
                        }
                    }
                }
            }

            is Resource.DataError -> {
//                ErrorUtil.showError(binding.root, response.errorMsg)
                if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && response.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    enterAddressManual()
                }
            }

            else -> {
                enterAddressManual()
            }
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnNext -> {

                if (navFlowCall.equals(PROFILE_MANAGEMENT, true)) {
                    loader?.show(requireActivity().supportFragmentManager, Constants.LOADER_DIALOG)

                    val data = navData as ProfileDetailModel?
                    updateProfileDetails(data)
                } else {
                    if (NewCreateAccountRequestModel.personalAccount) {
                        loader?.show(
                            requireActivity().supportFragmentManager,
                            Constants.LOADER_DIALOG
                        )
                        hitlrdsCheckApi()
                    } else {
                        redirectToNextPage()
                    }
                }
            }

            binding.btnEnterAddressManually, binding.btnUpdateAddressManually -> {
                val bundle = Bundle()
                bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
                bundle.putString(Constants.NAV_FLOW_FROM, EDIT_FROM_POST_CODE)
                if (navData != null) {
                    val data = navData as ProfileDetailModel?
                    bundle.putParcelable(Constants.NAV_DATA_KEY, data)
                }
                findNavController().navigate(R.id.fragment_manual_address, bundle)
            }
        }

    }

    private fun hitlrdsCheckApi() {
        val lrdsEligibilityCheck = LrdsEligibiltyRequest()
        if (NewCreateAccountRequestModel.country.equals(Constants.UK_COUNTRY, true)) {
            lrdsEligibilityCheck.country = "UK"
        } else {
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
        bundle.putString(Constants.NAV_FLOW_KEY, navFlowCall)
        if (navData != null) {
            val data = navData as ProfileDetailModel?
            bundle.putParcelable(Constants.NAV_DATA_KEY, data)
        }

        findNavController().navigate(R.id.fragment_manual_address, bundle)
    }


    override fun addressCallback(position: Int) {

        for (i in 0 until mainList.size) {
            mainList[i]?.isSelected = false
        }
        mainList[position]?.isSelected = true
        selectAddressAdapter?.notifyDataSetChanged()



        NewCreateAccountRequestModel.selectedAddressId = position
        NewCreateAccountRequestModel.addressline1 = mainList[position]?.street.toString()
        NewCreateAccountRequestModel.townCity = mainList[position]?.town.toString()
        NewCreateAccountRequestModel.country =
            mainList[position]?.country.toString()
        NewCreateAccountRequestModel.address_country_code = "UK"
        NewCreateAccountRequestModel.zipCode =
            mainList[position]?.postcode.toString().trim().replace(" ", "")

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
                    findNavController().navigate(
                        R.id.action_selectaddressfragment_to_createAccountEligibleLRDS2,
                        bundle
                    )

                } else {
                    redirectToNextPage()
                }

            }

            is Resource.DataError -> {
                if ((response.errorModel?.errorCode == Constants.TOKEN_FAIL && response.errorModel.error.equals(
                        Constants.INVALID_TOKEN
                    )) || response.errorModel?.errorCode == Constants.INTERNAL_SERVER_ERROR
                ) {
                    displaySessionExpireDialog(response.errorModel)
                } else {
                    ErrorUtil.showError(binding.root, response.errorMsg)
                }
            }

            else -> {

            }
        }

    }

    private fun redirectToNextPage() {
        NewCreateAccountRequestModel.isManualAddress = false

        val bundle = Bundle()
        bundle.putString(
            Constants.NAV_FLOW_KEY,
            navFlowCall
        )
        when (navFlowCall) {


            EDIT_SUMMARY -> {
                findNavController().navigate(
                    R.id.action_selectaddressfragment_to_createAccountSummary,
                    bundle
                )
            }

            else -> {
                if (NewCreateAccountRequestModel.personalAccount) {
                    findNavController().navigate(
                        R.id.action_selectaddressfragment_to_createAccountTypesFragment,
                        bundle
                    )

                } else {

                    findNavController().navigate(
                        R.id.action_selectaddressfragment_to_forgotPasswordFragment,
                        bundle
                    )

                }
            }

        }

    }

    private fun updateProfileDetails(data: ProfileDetailModel?) {

        val request = Utils.returnEditProfileModel(
            data?.accountInformation?.businessName ?: "",
            data?.accountInformation?.fein,
            data?.personalInformation?.firstName,
            data?.personalInformation?.lastName,
            NewCreateAccountRequestModel.addressline1,
            NewCreateAccountRequestModel.addressline2,
            NewCreateAccountRequestModel.townCity,
            "HE",
            NewCreateAccountRequestModel.zipCode,
            data?.personalInformation?.zipCodePlus,
            NewCreateAccountRequestModel.address_country_code,
            data?.personalInformation?.emailAddress,
            data?.personalInformation?.primaryEmailStatus,
            data?.personalInformation?.pemailUniqueCode,
            data?.personalInformation?.phoneCell,
            data?.personalInformation?.phoneCellCountryCode,
            data?.personalInformation?.phoneDay,
            data?.personalInformation?.phoneDayCountryCode,
            data?.personalInformation?.fax,
            data?.accountInformation?.smsOption,
            data?.personalInformation?.eveningPhone,
            data?.accountInformation?.stmtDelivaryMethod,
            data?.accountInformation?.correspDeliveryFrequency,
            Utils.retrunMfaStatus(data?.accountInformation?.mfaEnabled ?: ""),
            accountType = data?.accountInformation?.accountType

        )

        viewModelProfile.updateUserDetails(request)

    }


}


