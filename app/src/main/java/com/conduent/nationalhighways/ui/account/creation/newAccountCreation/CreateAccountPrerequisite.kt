package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentCreateAccountPrerequisiteBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.ui.loader.OnRetryClickListener
import com.conduent.nationalhighways.utils.extn.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateAccountPrerequisite : BaseFragment<FragmentCreateAccountPrerequisiteBinding>(),
    View.OnClickListener, OnRetryClickListener {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentCreateAccountPrerequisiteBinding.inflate(inflater, container, false)

    override fun init() {
        val content = SpannableString(getString(R.string.sign_in))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.txtSignIn.text = content
        binding.btnCreateAccount.setOnClickListener(this)
        NewCreateAccountRequestModel.addedVehicleList.clear()
    }

    override fun initCtrl() {
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        hideKeyboard()
        when (v?.id) {
            binding.btnCreateAccount.id -> {
                val bundle = Bundle()
//                bundle.putParcelable("response", status.data)
                findNavController().navigate(
                    R.id.action_createAccountPrerequisite_to_fragment_choose_account_type,
                    bundle
                )
            }
        }
    }

    override fun onRetryClick() {

    }
}