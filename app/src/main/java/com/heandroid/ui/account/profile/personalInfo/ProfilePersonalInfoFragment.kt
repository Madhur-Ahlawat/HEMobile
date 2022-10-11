package com.heandroid.ui.account.profile.personalInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.databinding.FragmentProfilePersonalInfoBinding
import com.heandroid.ui.base.BaseFragment
import com.heandroid.utils.common.Constants
import com.heandroid.utils.common.SessionManager
import com.heandroid.utils.common.Utils
import com.heandroid.utils.extn.gone
import com.heandroid.utils.extn.hideKeyboard
import com.heandroid.utils.extn.visible
import com.heandroid.utils.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfilePersonalInfoFragment : BaseFragment<FragmentProfilePersonalInfoBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    private var accountType: String = Constants.PERSONAL_ACCOUNT
    private var isSecondary: Boolean = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfilePersonalInfoBinding.inflate(inflater, container, false)

    override fun init() {
        binding.data = arguments?.getParcelable(Constants.DATA)
        accountType = sessionManager.getAccountType() ?: Constants.PERSONAL_ACCOUNT
        isSecondary = sessionManager.getSecondaryUser()
        setView()
        checkButton()
    }

    private fun checkButton() {
        binding.enable = Utils.isEmailValid(binding.tieEmailId.text.toString().trim())
    }

    private fun setView() {
        when (accountType) {
            Constants.PERSONAL_ACCOUNT -> {
                // hide business account view
                if (!isSecondary) {
                    binding.tilBusinessName.gone()
                    binding.tilRegNo.gone()
                } else {
                    // nominated user
                    binding.tilBusinessName.gone()
                    binding.tilRegNo.gone()
                }

            }

            Constants.BUSINESS_ACCOUNT -> {
                // show business account view
                if (!isSecondary) {
                    binding.tilBusinessName.visible()
                    binding.tilRegNo.visible()
                } else {
                    // nominated user
                    binding.tilBusinessName.visible()
                    binding.tilRegNo.visible()
                }
            }
        }
    }

    override fun initCtrl() {
        binding.btnAction.setOnClickListener(this)
        binding.btnChangeEmail.setOnClickListener(this)
        binding.tieEmailId.onTextChanged {
            checkButton()
        }
    }

    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            R.id.btnAction -> {
                val bundle = Bundle()
                bundle.putParcelable(Constants.DATA, binding.data)
                findNavController().navigate(
                    R.id.action_personalInfoFragment_to_postCodeFragment,
                    bundle
                )
            }
            R.id.btnChangeEmail -> {
                val bundle = Bundle()
                binding.data?.personalInformation?.run {
                    bundle.putParcelable(
                        Constants.DATA, ProfileUpdateEmailModel(
                            referenceId = null,
                            securityCode = null,
                            addressLine1 = addressLine1,
                            addressLine2 = addressLine2,
                            city = city,
                            country = country,
                            emailAddress = emailAddress,
                            phoneCell = cellPhone,
                            phoneDay = phoneDay,
                            phoneEvening = eveningPhone,
                            phoneFax = fax,
                            primaryEmailStatus = primaryEmailStatus,
                            primaryEmailUniqueID = pemailUniqueCode,
                            smsOption = "Y",
                            state = state,
                            zipCode = zipcode,
                            zipCodePlus = zipCodePlus
                        )
                    )

//                 bundle.putParcelable(Constants.DATA, ProfileUpdateEmailModel(referenceId = null,securityCode = null,emailAddress = emailAddress,
//                                                                                 primaryEmailStatus = primaryEmailStatus, primaryEmailUniqueID = pemailUniqueCode, smsOption = "Y"))
                }
                findNavController().navigate(
                    R.id.action_personalInfoFragment_to_emailFragment,
                    bundle
                )

            }
        }
    }
}