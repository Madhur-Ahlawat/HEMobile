package com.conduent.nationalhighways.ui.account.creation.newAccountCreation


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.EmptyApiResponse
import com.conduent.nationalhighways.data.model.account.NewVehicleInfoDetails
import com.conduent.nationalhighways.databinding.FragmentCreateAccountSummaryBinding
import com.conduent.nationalhighways.ui.account.creation.adapter.VehicleListAdapter
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.account.creation.step5.CreateAccountVehicleViewModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.Constants.EDIT_ACCOUNT_TYPE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_MOBILE
import com.conduent.nationalhighways.utils.common.Constants.EDIT_SUMMARY
import com.conduent.nationalhighways.utils.common.Constants.NAV_DATA_KEY
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_FROM
import com.conduent.nationalhighways.utils.common.Constants.NAV_FLOW_KEY
import com.conduent.nationalhighways.utils.common.Resource
import com.conduent.nationalhighways.utils.common.observe
import com.conduent.nationalhighways.utils.extn.gone
import com.conduent.nationalhighways.utils.extn.makeLinks
import com.conduent.nationalhighways.utils.extn.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountSummaryFragment : BaseFragment<FragmentCreateAccountSummaryBinding>(),
    VehicleListAdapter.VehicleListCallBack,
    View.OnClickListener {
    private var url: String? = ""
    private var dataModel: NewCreateAccountRequestModel? = null
    private lateinit var title: TextView


    private lateinit var vehicleAdapter: VehicleListAdapter

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateAccountSummaryBinding =
        FragmentCreateAccountSummaryBinding.inflate(inflater, container, false)

    override fun init() {
        binding.btnNext.setOnClickListener(this)
        binding.editFullName.setOnClickListener(this)
        binding.editAddress.setOnClickListener(this)
        binding.editEmailAddress.setOnClickListener(this)
        binding.editMobileNumber.setOnClickListener(this)
        binding.editAccountType.setOnClickListener(this)
        binding.editCommunications.setOnClickListener(this)
        binding.editTwoStepVerification.setOnClickListener(this)
        binding.editAccountSubType.setOnClickListener(this)
        title = requireActivity().findViewById(R.id.title_txt)
        binding.emailAddressSummary.visible()
        binding.emailCardProfile.gone()
        dataModel = NewCreateAccountRequestModel
        (dataModel!!.firstName + " " + dataModel!!.lastName).also { binding.fullName.text = it }
        if (!dataModel!!.personalAccount) {
            binding.companyNameCard.visible()
            binding.companyName.text = dataModel!!.companyName
            binding.editCompanyName.setOnClickListener(this)
        } else {
            binding.companyNameCard.gone()

        }
        binding.passwordCard.gone()
        if (dataModel!!.communicationTextMessage) {
            binding.communications.text = getString(R.string.yes)
        } else {
            binding.communications.text = getString(R.string.no)
        }

        if (dataModel!!.twoStepVerification) {
            binding.twoStepVerification.text = getString(R.string.yes)
        } else {
            binding.twoStepVerification.text = getString(R.string.no)
        }

        binding.address.text =
            dataModel!!.addressline1 + "\n" + dataModel!!.townCity + "\n" + dataModel!!.zipCode
        binding.emailAddress.text = dataModel!!.emailAddress
        if (NewCreateAccountRequestModel.communicationTextMessage || NewCreateAccountRequestModel.twoStepVerification) {
            if (dataModel!!.mobileNumber?.isEmpty() != false) {
                binding.phoneCard.gone()
            } else {
                binding.phoneCard.visible()
                binding.mobileNumber.text =
                    dataModel!!.countryCode?.let { getRequiredText(it) } + " " + dataModel!!.mobileNumber
            }
        } else {
            if (dataModel!!.telephoneNumber?.isEmpty() != false) {
                binding.phoneCard.gone()
            } else {
                binding.phoneCard.visible()
                binding.mobileNumber.text =
                    dataModel!!.telephone_countryCode?.let { getRequiredText(it) } + " " + dataModel!!.telephoneNumber
            }
        }


        if (dataModel!!.personalAccount) {
            binding.accountSubType.visible()
            binding.accountType.text = getString(R.string.personal)
            if (NewCreateAccountRequestModel.prePay) {
                binding.textAccountSubType.text = getString(R.string.str_prepay)
            } else {
                binding.textAccountSubType.text = getString(R.string.pay_as_you_go)
            }

        } else {
            binding.accountType.text = getString(R.string.business)
            binding.accountSubType.gone()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val vehicleList = dataModel?.vehicleList as ArrayList<NewVehicleInfoDetails>
        Log.e("TAG", "init: " + vehicleList.size)
        for (i in 0 until vehicleList.size) {
            Log.e("TAG", "init: isDblaAvailable " + vehicleList[i].isDblaAvailable)
        }
        vehicleAdapter = VehicleListAdapter(requireContext(), vehicleList, this, false)
        binding.recyclerView.adapter = vehicleAdapter

        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.btnNext.enable()
            } else {
                binding.btnNext.disable()
            }
        }
        if (NewCreateAccountRequestModel.communicationTextMessage || NewCreateAccountRequestModel.twoStepVerification) {
            binding.txtMobileNumber.text = getString(R.string.mobile_phone_number)
        } else {
            binding.txtMobileNumber.text = getString(R.string.telephone_number)
        }

        binding.checkBoxTerms.makeLinks(Pair("terms & conditions", View.OnClickListener {
            findNavController().navigate(R.id.action_createAccountSummaryFragment_to_generalTermsAndConditions)
        }))

    }

    private fun getRequiredText(text: String) = text.substringAfter('(').replace(")", "")
    override fun initCtrl() {
        binding.btnNext.setOnClickListener(this)
    }

    override fun observer() {

    }

    private fun heartBeatApiResponse(resource: Resource<EmptyApiResponse?>?) {


    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btnNext -> {
                emailHeartBeatApi()
                smsHeartBeatApi()




                if (!NewCreateAccountRequestModel.prePay) {
                    val bundle = Bundle()

                    bundle.putDouble(Constants.DATA, 0.00)
                    bundle.putDouble(Constants.THRESHOLD_AMOUNT, 0.00)
                    bundle.putString(NAV_FLOW_KEY, Constants.NOTSUSPENDED)
                    bundle.putInt(Constants.PAYMENT_METHOD_SIZE, 0)
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_nmiPaymentFragment,
                        bundle
                    )

                } else {
                    findNavController().navigate(R.id.action_accountSummaryFragment_to_TopUpFragment)

                }
            }

            R.id.editFullName -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_personalInfoFragment,
                    enableEditMode()
                )
            }

            R.id.editCompanyName -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_personalInfoFragment,
                    enableEditMode()
                )
            }

            R.id.editAddress -> {
                if (NewCreateAccountRequestModel.isManualAddress) {
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_manualAddressFragment,
                        enableEditMode()
                    )
                } else {
                    findNavController().navigate(
                        R.id.action_accountSummaryFragment_to_postCodeFragment,
                        enableEditMode()
                    )
                }
            }

            R.id.editEmailAddress -> {
                val bundle = Bundle()
                bundle.putBoolean(Constants.IS_EDIT_EMAIL, true)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_emailAddressFragment,
                    enableEditMode()
                )
            }

            R.id.editMobileNumber -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, EDIT_MOBILE)
                bundle.putString(NAV_FLOW_FROM, EDIT_SUMMARY)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_mobileNumberFragment,
                    bundle
                )
            }

            R.id.editAccountType -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, EDIT_SUMMARY)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_typeAccountFragment,
                    bundle
                )
            }

            R.id.editCommunications -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_communicationFragment,
                    enableEditMode()
                )
            }

            R.id.editTwoStepVerification -> {
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_twoStepCommunicationFragment,
                    enableEditMode()
                )
            }

            R.id.editAccountSubType -> {
                val bundle = Bundle()
                bundle.putString(NAV_FLOW_KEY, EDIT_SUMMARY)
//                bundle.putParcelable(NAV_DATA_KEY, dataModel!!. as Parcelable)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_createAccountTypesFragment, bundle
                )
            }

        }
    }

    private fun enableEditMode(): Bundle {
        val bundle = Bundle()
        bundle.putString(NAV_FLOW_KEY, EDIT_SUMMARY)
        bundle.putString(NAV_FLOW_FROM, EDIT_SUMMARY)
        bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)


        return bundle
    }


    override fun vehicleListCallBack(
        position: Int,
        value: String,
        plateNumber: String?,
        isDblaAvailable: Boolean?
    ) {
        Log.e(
            "TAG",
            "vehicleListCallBack() called with: position = $position, value = $value, plateNumber = $plateNumber, isDblaAvailable = $isDblaAvailable"
        )
        enableEditMode()
        if (value == Constants.REMOVE_VEHICLE) {
            val bundle = Bundle()
            bundle.putInt(Constants.VEHICLE_INDEX, position)
            findNavController().navigate(R.id.action_accountSummaryFragment_to_removeVehicleFragment)
        } else {
            val bundle = Bundle()
            bundle.putString(Constants.NAV_FLOW_KEY, EDIT_SUMMARY)

            if (isDblaAvailable == true) {
                bundle.putString(Constants.PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)
                bundle.putBoolean(Constants.EDIT_SUMMARY, true)

                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_createAccountFindVehicleFragment,
                    bundle
                )
            } else {
                bundle.putString(Constants.OLD_PLATE_NUMBER, plateNumber)
                bundle.putInt(Constants.VEHICLE_INDEX, position)
                bundle.putBoolean(Constants.SHOW_BACK_BUTTON, false)

                if (isDblaAvailable != null) {
                    bundle.putBoolean(Constants.IS_DBLA_AVAILABLE, isDblaAvailable)
                }
                findNavController().navigate(
                    R.id.action_accountSummaryFragment_to_addNewVehicleDetailsFragment,
                    bundle
                )
            }
        }

    }

    override fun onResume() {
        if (requireActivity() is CreateAccountActivity) {
            title.text = getString(R.string.str_create_an_account)

        }
        super.onResume()
    }

}