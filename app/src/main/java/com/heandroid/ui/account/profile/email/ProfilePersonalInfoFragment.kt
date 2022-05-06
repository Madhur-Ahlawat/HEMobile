package com.heandroid.ui.account.profile.email

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
import com.heandroid.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePersonalInfoFragment : BaseFragment<FragmentProfilePersonalInfoBinding>(), View.OnClickListener {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?)=FragmentProfilePersonalInfoBinding.inflate(inflater,container,false)
    override fun init() {
        binding.enable=true
        binding.data=arguments?.getParcelable(Constants.DATA)
    }
    override fun initCtrl() {
        binding.btnAction.setOnClickListener(this)
        binding.btnChangeEmail.setOnClickListener(this)
    }
    override fun observer() {}
    override fun onClick(v: View?) {
        hideKeyboard()
        when(v?.id){
            R.id.btnAction -> {
                val bundle= Bundle()
                bundle.putParcelable(Constants.DATA,binding.data)
                findNavController().navigate(R.id.action_personalInfoFragment_to_postCodeFragment,bundle)
            }
            R.id.btnChangeEmail ->{
                val bundle = Bundle()
                binding.data?.personalInformation?.run {
                   bundle.putParcelable(Constants.DATA, ProfileUpdateEmailModel(referenceId = null,securityCode = null,addressLine1 = addressLine1, addressLine2 = addressLine2, city = city,
                                                                                country = country, emailAddress = emailAddress, phoneCell = cellPhone,
                                                                                phoneDay = phoneDay, phoneEvening = eveningPhone, phoneFax = fax,
                                                                                primaryEmailStatus = primaryEmailStatus, primaryEmailUniqueID = pemailUniqueCode, smsOption = "Y",
                                                                                state = state, zipCode = zipcode, zipCodePlus = zipCodePlus))

//                    bundle.putParcelable(Constants.DATA, ProfileUpdateEmailModel(referenceId = null,securityCode = null,emailAddress = emailAddress,
//                                                                                 primaryEmailStatus = primaryEmailStatus, primaryEmailUniqueID = pemailUniqueCode, smsOption = "Y"))
                }
                findNavController().navigate(R.id.action_personalInfoFragment_to_emailFragment,bundle)

            }
        }
    }
}