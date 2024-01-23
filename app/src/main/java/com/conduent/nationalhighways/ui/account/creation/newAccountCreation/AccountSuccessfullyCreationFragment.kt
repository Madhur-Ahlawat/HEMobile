package com.conduent.nationalhighways.ui.account.creation.newAccountCreation

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.conduent.nationalhighways.R
import com.conduent.nationalhighways.data.model.account.CreateProfileDetailModelModel
import com.conduent.nationalhighways.databinding.FragmentAccountSuccessfullyCreationBinding
import com.conduent.nationalhighways.ui.account.creation.new_account_creation.model.NewCreateAccountRequestModel
import com.conduent.nationalhighways.ui.auth.login.LoginActivity
import com.conduent.nationalhighways.ui.base.BaseFragment
import com.conduent.nationalhighways.utils.common.Constants
import com.conduent.nationalhighways.utils.common.SessionManager
import com.conduent.nationalhighways.utils.common.Utils
import com.conduent.nationalhighways.utils.extn.startNormalActivityWithFinish
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountSuccessfullyCreationFragment :
    BaseFragment<FragmentAccountSuccessfullyCreationBinding>(), View.OnClickListener {
    private var createProfileDetailModelModel: CreateProfileDetailModelModel? = null

    @Inject
    lateinit var sessionManager: SessionManager

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountSuccessfullyCreationBinding =
        FragmentAccountSuccessfullyCreationBinding.inflate(inflater, container, false)

    override fun init() {
        Utils.validationsToShowRatingDialog(requireActivity(),sessionManager)
        binding.signIn.setOnClickListener(this)
    }

    override fun initCtrl() {
        if (arguments?.getParcelable<CreateProfileDetailModelModel>(Constants.DATA) != null) {
            createProfileDetailModelModel = arguments?.getParcelable(Constants.DATA)
        }

        if (createProfileDetailModelModel!=null){
            binding.accountNumber.text=createProfileDetailModelModel?.accountNumber
            binding.paymentReferenceNumber.text=createProfileDetailModelModel?.referenceNumber
        }
        binding.feedbackBt.movementMethod = LinkMovementMethod.getInstance()

        binding.emailConformationTxt.text = getString(
            R.string.we_sent_confirmation_email,
            NewCreateAccountRequestModel.emailAddress
        )

        if (NewCreateAccountRequestModel.prePay.not()) {
            binding.payAsGoText.visibility = View.VISIBLE
            binding.prePayCard.visibility = View.GONE
        } else {
            binding.payAsGoText.visibility = View.GONE
            binding.prePayCard.visibility = View.VISIBLE
        }
    }

    override fun observer() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.signIn -> {
                NewCreateAccountRequestModel.emailAddress = ""
                NewCreateAccountRequestModel.prePay = false


                requireActivity().startNormalActivityWithFinish(LoginActivity::class.java)
            }
        }
    }


}