package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.databinding.FragmentAccountSuccessfullyCreationBinding
import com.conduent.nationalhighways.ui.account.creation.controller.CreateAccountActivity
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.base.BaseFragment


class AccountSuccessfullyCreationFragment :
    BaseFragment<FragmentAccountSuccessfullyCreationBinding>(), View.OnClickListener {
    private var backIcon: ImageView? = null


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuccessfullyCreationBinding =
        FragmentAccountSuccessfullyCreationBinding.inflate(inflater, container, false)

    override fun init() {
        backIcon = requireActivity().findViewById(R.id.back_button)
        backIcon?.visibility = View.GONE

        binding.emailConformationTxt.text=getString(R.string.we_sent_confirmation_email,NewCreateAccountRequestModel.emailAddress)

    }

    override fun initCtrl() {
        binding.signIn.setOnClickListener(this)
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.signIn -> {
            }
        }
    }


}