package com.heandroid.ui.account.profile.personalInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.heandroid.R
import com.heandroid.data.model.profile.ProfileUpdateEmailModel
import com.heandroid.databinding.FragmentNominatedPersonalInfoBinding
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
class NominatedPersonalInfoFragment : BaseFragment<FragmentNominatedPersonalInfoBinding>(),
    View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    private var accountType: String = Constants.PERSONAL_ACCOUNT
    private var isSecondary: Boolean = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentNominatedPersonalInfoBinding.inflate(inflater, container, false)

    override fun init() {
        binding.enable = true
        binding.nominated = arguments?.getParcelable(Constants.NOMINATED_ACCOUNT_DATA)
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
                bundle.putParcelable(Constants.NOMINATED_ACCOUNT_DATA, binding.nominated)
                findNavController().navigate(
                    R.id.action_nominatedPersonalInfoFragment_to_nominatedPostCodeFragment,
                    bundle
                )
            }
            R.id.btnChangeEmail -> {
                val bundle = Bundle()
                binding.nominated?.run {
                    bundle.putParcelable(
                        Constants.DATA, ProfileUpdateEmailModel(
                            referenceId = null,
                            securityCode = null,
                            addressLine1 = null,
                            addressLine2 = null,
                            city = null,
                            country = null,
                            emailAddress = emailAddress,
                            phoneCell = phoneNumber,
                            phoneDay = phoneNumber,
                            phoneEvening = null,
                            phoneFax = null,
                            primaryEmailStatus = null,
                            primaryEmailUniqueID = null,
                            smsOption = "Y",
                            state = null,
                            zipCode = null,
                            zipCodePlus = null
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